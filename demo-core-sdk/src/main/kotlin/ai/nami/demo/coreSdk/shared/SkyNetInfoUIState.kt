package ai.nami.demo.coreSdk.shared

data class SkyNetInfoUIState(
    val isLoading: Boolean = false,
    val initSDKSuccess: Boolean? = null,
    val errorMessage: String? = null
)

sealed interface SkyNetInfoViewIntent {
    data class InitNamiSDK(val sessionCode: String): SkyNetInfoViewIntent
}

sealed interface SkyNetInfoPartialState {
    object Loading: SkyNetInfoPartialState
    data class InitSuccess(val isSuccess: Boolean): SkyNetInfoPartialState

    data class InitFail(val error: String?): SkyNetInfoPartialState

    fun reduce(currentState: SkyNetInfoUIState): SkyNetInfoUIState {
        return when (this) {
            is Loading -> currentState.copy(isLoading = true, errorMessage = null)
            is InitSuccess -> currentState.copy(isLoading = false, initSDKSuccess = isSuccess)
            is InitFail -> currentState.copy(
                isLoading = false,
                initSDKSuccess = false,
                errorMessage = error
            )
        }
    }
}