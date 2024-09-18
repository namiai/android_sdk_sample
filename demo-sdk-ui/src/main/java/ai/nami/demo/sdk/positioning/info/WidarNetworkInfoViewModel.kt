package ai.nami.demo.sdk.positioning.info

import ai.nami.demo.common.NamiLocalStorage
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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class WidarNetworkInfoViewModel(namiLocalStorage: NamiLocalStorage): ViewModel() {

    private val viewIntentFlow = MutableSharedFlow<WidarNetworkInfoViewIntent>()

    private val viewIntentFlow = MutableSharedFlow<WidarNetworkInfoViewIntent>()

    val uiState: StateFlow<WidarNetworkInfoState>

    init {
        val initState = WidarNetworkInfoState(isLoading = true)

        val listPairedDeviceUrnFlow = namiLocalStorage.listPairedDeviceUrn.map { deviceUrns ->
            WidarNetworkInfoPartialState.LoadPairedDeviceUrns(deviceUrns.toList())
        }

        uiState = merge(
            listPairedDeviceUrnFlow,
            viewIntentFlow.toPartialState()
        ).onEach {
            NamiLog.e("WidarNetworkInfoViewModel PartialState $it","debug_sample_nami")
        }.scan(initState) { currentState, partialState ->
            partialState.reduce(currentState)
        }.onEach {
            NamiLog.e("WidarNetworkInfoViewModel UIState $it","debug_sample_nami")
        }.stateIn(viewModelScope, SharingStarted.Eagerly, initState)
    }

    fun handleViewIntent(viewIntent: WidarNetworkInfoViewIntent) {
        viewModelScope.launch {
            viewIntentFlow.emit(viewIntent)
        }
    }

    private fun Flow<WidarNetworkInfoViewIntent>.toPartialState(): Flow<WidarNetworkInfoPartialState> {
        return flatMapLatest {
            when (it) {
                is WidarNetworkInfoViewIntent.InitNamiSDK -> initSDK(it.sessionCode)
            }
        }
    }

    private fun initSDK(sessionCode: String): Flow<WidarNetworkInfoPartialState> =
        flow<WidarNetworkInfoPartialState> {
            val result = NamiSDK.init(sessionCode)
            emit(WidarNetworkInfoPartialState.InitSuccess(result))
        }.onStart {
            emit(WidarNetworkInfoPartialState.Loading)
        }.catch { e ->
            emit(WidarNetworkInfoPartialState.InitFail(e.message))
        }
}


data class WidarNetworkInfoState(
    val isLoading: Boolean = false,
    val listPairedDeviceUrns: List<String> = emptyList(),
    val initSDKSuccess: Boolean? = null,
    val errorMessage: String? = null
)

sealed interface WidarNetworkInfoViewIntent {
    data class InitNamiSDK(val sessionCode: String): WidarNetworkInfoViewIntent
}

sealed interface WidarNetworkInfoPartialState {
    object Loading: WidarNetworkInfoPartialState
    data class InitSuccess(val isSuccess: Boolean): WidarNetworkInfoPartialState

    data class InitFail(val error: String?): WidarNetworkInfoPartialState

    data class LoadPairedDeviceUrns(val listPairedDeviceUrns: List<String>):
        WidarNetworkInfoPartialState

    fun reduce(currentState: WidarNetworkInfoState): WidarNetworkInfoState {
        return when (this) {
            is Loading -> currentState.copy(isLoading = true, errorMessage = null)
            is InitSuccess -> currentState.copy(isLoading = false, initSDKSuccess = isSuccess)
            is InitFail -> currentState.copy(
                isLoading = false,
                initSDKSuccess = false,
                errorMessage = error
            )

            is LoadPairedDeviceUrns -> currentState.copy(
                listPairedDeviceUrns = listPairedDeviceUrns,
                isLoading = false
            )
        }
    }
}