package ai.nami.shared_sample.session_code


data class SessionCodeUIState(
    val isLoading: Boolean = true,
    val initSDKSuccess: Boolean? = null,
    val errorMessage: String? = null,
    val isNeedASessionCode: Boolean? = null,
)

sealed interface SessionCodeViewIntent {
    data class InitNamiSDK(val sessionCode: String) : SessionCodeViewIntent
}

sealed interface SessionCodePartialState {
    object Loading : SessionCodePartialState
    data class LoadedSessionCode(
        val isNeedASessionCode: Boolean,
    ) : SessionCodePartialState

    data object InitSuccess : SessionCodePartialState

    data class InitFail(val error: String?) : SessionCodePartialState

    fun reduce(currentState: SessionCodeUIState): SessionCodeUIState {
        return when (this) {
            is Loading -> currentState.copy(isLoading = true, errorMessage = null)
            is LoadedSessionCode -> currentState.copy(
                isLoading = false,
                isNeedASessionCode = isNeedASessionCode
            )

            is InitSuccess -> currentState.copy(isLoading = false, initSDKSuccess = true)
            is InitFail -> currentState.copy(
                isLoading = false,
                initSDKSuccess = false,
                errorMessage = error
            )
        }
    }
}

