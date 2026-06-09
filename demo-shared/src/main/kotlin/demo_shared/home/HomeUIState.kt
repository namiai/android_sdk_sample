package demo_shared.home

import ai.nami.sdk.model.Device
import ai.nami.sdk.model.Place

data class HomeUIState(
    val isLoading: Boolean = false,
    val initSDKSuccess: Boolean? = null,
    val errorMessage: String? = null,
    val isNeedASessionCode: Boolean? = null,
    val place: Place? = null,
    val listDevices: List<Device> = emptyList(),
    val clientID: String = ""
)

sealed interface HomeViewIntent {
    data class InitNamiSDK(val clientID: String) : HomeViewIntent

    data object OpenedSDK : HomeViewIntent

    data object SignOut : HomeViewIntent
}

sealed interface HomePartialState {
    object Loading : HomePartialState
    data class InitSuccess(val isSuccess: Boolean) : HomePartialState

    data class InitFail(val error: String?) : HomePartialState

    data object LoadingSessionCode : HomePartialState

    data class LoadedSessionCode(
        val isNeedASessionCode: Boolean,
        val place: Place?,
        val listDevices: List<Device>,
        val clientID: String
    ) :
        HomePartialState

    data object SigningOut : HomePartialState

    data object SignedOut : HomePartialState

    fun reduce(currentState: HomeUIState): HomeUIState {
        return when (this) {
            is Loading -> currentState.copy(isLoading = true, errorMessage = null)
            is InitSuccess -> currentState.copy(isLoading = false, initSDKSuccess = isSuccess)
            is InitFail -> currentState.copy(
                isLoading = false,
                initSDKSuccess = false,
                errorMessage = error
            )

            is LoadingSessionCode -> currentState.copy(isLoading = true)

            is LoadedSessionCode -> currentState.copy(
                isNeedASessionCode = isNeedASessionCode,
                place = place,
                listDevices = listDevices,
                isLoading = false,
                clientID = clientID
            )

            is SigningOut -> currentState.copy(isLoading = true)

            is SignedOut -> currentState.copy(
                isLoading = false,
                isNeedASessionCode = true,
                place = null,
                listDevices = emptyList()
            )
        }
    }
}