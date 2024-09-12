package ai.nami.demo.coreSdk.shared

import ai.nami.sdk.NamiSDK
import ai.nami.sdk.common.NamiLog
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SkyNetInfoViewModel : ViewModel() {


    private val viewIntentFlow = MutableSharedFlow<SkyNetInfoViewIntent>()

    val uiState: StateFlow<SkyNetInfoUIState>

    init {
        NamiLog.e("debug_sample_nami","SkyNetInfoViewModel init")
        val initialState = SkyNetInfoUIState()
        uiState =
            merge(viewIntentFlow.toPartialState()).scan(initialState) { currentState, partialState ->
                partialState.reduce(currentState)
            }.stateIn(viewModelScope, SharingStarted.Eagerly, initialState)
    }

    fun handleViewIntent(viewIntent: SkyNetInfoViewIntent) {
        viewModelScope.launch {
            viewIntentFlow.emit(viewIntent)
        }
    }

    private fun Flow<SkyNetInfoViewIntent>.toPartialState(): Flow<SkyNetInfoPartialState> {
        return flatMapLatest {
            when (it) {
                is SkyNetInfoViewIntent.InitNamiSDK -> initSDK(it.sessionCode)
            }
        }
    }

    private fun initSDK(sessionCode: String): Flow<SkyNetInfoPartialState> =
        flow<SkyNetInfoPartialState> {
            val result = NamiSDK.init(sessionCode)
            emit(SkyNetInfoPartialState.InitSuccess(result))
        }.onStart {
            emit(SkyNetInfoPartialState.Loading)
        }.catch { e -> emit(SkyNetInfoPartialState.InitFail(error = e.message)) }
}