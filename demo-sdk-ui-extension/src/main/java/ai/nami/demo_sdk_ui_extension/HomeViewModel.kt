package ai.nami.demo_sdk_ui_extension

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

class HomeViewModel : ViewModel() {

    private val viewIntentFlow = MutableSharedFlow<HomeViewIntent>()

    val uiState: StateFlow<HomeUIState>

    init {
        NamiLog.e("debug_sample_nami", "HomeViewModel init")
        val initialState = HomeUIState()
        uiState =
            merge(viewIntentFlow.toPartialState()).scan(initialState) { currentState, partialState ->
                partialState.reduce(currentState)
            }.stateIn(viewModelScope, SharingStarted.Eagerly, initialState)
    }

    fun handleViewIntent(viewIntent: HomeViewIntent) {
        viewModelScope.launch {
            viewIntentFlow.emit(viewIntent)
        }
    }

    private fun Flow<HomeViewIntent>.toPartialState(): Flow<HomePartialState> {
        return flatMapLatest {
            when (it) {
                is HomeViewIntent.InitNamiSDK -> initSDK(it.sessionCode)
            }
        }
    }

    private fun initSDK(sessionCode: String): Flow<HomePartialState> =
        flow<HomePartialState> {
            val result = NamiSDK.init(sessionCode)
            emit(HomePartialState.InitSuccess(result))
        }.onStart {
            emit(HomePartialState.Loading)
        }.catch { e -> emit(HomePartialState.InitFail(error = e.message)) }
}