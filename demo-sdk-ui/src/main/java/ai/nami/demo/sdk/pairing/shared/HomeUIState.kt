package ai.nami.demo.sdk.pairing.shared

data class HomeUIState(
    val isLoading: Boolean = false,
    val initSDKSuccess: Boolean? = null,
    val errorMessage: String? = null
)

sealed interface HomeViewIntent {
    data class InitNamiSDK(val sessionCode: String): HomeViewIntent
}

sealed interface HomePartialState {
    object Loading: HomePartialState
    data class InitSuccess(val isSuccess:Boolean): HomePartialState

    data class InitFail(val error: String?): HomePartialState

    fun reduce(currentState: HomeUIState): HomeUIState {
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