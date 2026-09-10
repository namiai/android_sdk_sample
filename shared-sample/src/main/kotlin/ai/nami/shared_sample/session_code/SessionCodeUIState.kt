package ai.nami.shared_sample.session_code


data class SessionCodeUIState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val isNeedASessionCode: Boolean? = null,
)

sealed interface SessionCodeViewIntent {
    data class InitNamiSDK(val sessionCode: String) : SessionCodeViewIntent
}

sealed interface SessionCodeEffect {
    data object NavigateHome : SessionCodeEffect
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

            is InitSuccess -> currentState.copy(isLoading = false)
            is InitFail -> currentState.copy(
                isLoading = false,
                errorMessage = error
            )
        }
    }
}

/**
 * Returns the one-shot effect caused by this state change, if any.
 *
 * Loading, initialization, and failure states do not navigate. A valid saved session represents
 * the navigation milestone because both the place ID and customer token are then available.
 *
 * @return the effect to emit, or `null` when this state change has no one-shot effect.
 */
internal fun SessionCodePartialState.toEffect(): SessionCodeEffect? = when (this) {
    is SessionCodePartialState.Loading -> null
    is SessionCodePartialState.LoadedSessionCode -> {
        if (isNeedASessionCode) null else SessionCodeEffect.NavigateHome
    }

    is SessionCodePartialState.InitSuccess -> null
    is SessionCodePartialState.InitFail -> null
}
