package ai.nami.sdk.sample.positioning.info

import ai.nami.sdk.sample.data.NamiLocalStorage
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.stateIn

class WidarNetworkInfoViewModel(private val namiLocalStorage: NamiLocalStorage): ViewModel() {

    val uiState: StateFlow<WidarNetworkInfoState>

    init {
        val initState = WidarNetworkInfoState(isLoading = true)
        uiState = namiLocalStorage.listPairedDeviceUrn.scan(initState) { currentState, deviceUrns ->
            currentState.copy(isLoading = false, listPairedDeviceUrns = deviceUrns.toList())
        }.stateIn(viewModelScope, SharingStarted.Eagerly, initState)
    }

}


data class WidarNetworkInfoState(
    val isLoading: Boolean = false,
    val listPairedDeviceUrns: List<String> = emptyList()
)