package ai.nami.shared_sample.session_code

import ai.nami.sdk.NamiSDK
import ai.nami.sdk_ui_extensions.NamiSDUISDK
import ai.nami.shared_sample.AppModuleDI
import ai.nami.shared_sample.NamiLocalStorage
import ai.nami.shared_sample.home.isValid
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transformWhile
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class SessionCodeViewModel(private val namiLocalStorage: NamiLocalStorage) : ViewModel() {

    private val viewIntentFlow = MutableSharedFlow<SessionCodeViewIntent>()
    private val effectChannel = Channel<SessionCodeEffect>(capacity = Channel.BUFFERED)

    val uiState: StateFlow<SessionCodeUIState>
    val effect: Flow<SessionCodeEffect> = effectChannel.receiveAsFlow()

    init {
        val initialState = SessionCodeUIState(isLoading = true)
        uiState =
            merge(
                fetchSavedSession(),
                viewIntentFlow.toPartialState()
            ).onEach { partialState ->
                partialState.toEffect()?.let { effectChannel.send(it) }
            }.scan(initialState) { currentState, partialState ->
                partialState.reduce(currentState)
            }.stateIn(viewModelScope, SharingStarted.Eagerly, initialState)
    }


    /** Converts user intents into state changes produced by the requested operation. */
    private fun Flow<SessionCodeViewIntent>.toPartialState(): Flow<SessionCodePartialState> {
        return flatMapLatest {
            when (it) {
                is SessionCodeViewIntent.InitNamiSDK -> initSDK(it.sessionCode)
            }
        }
    }

    /**
     * Handles a user intent.
     *
     * @param viewIntent the operation requested by the session-code UI.
     */
    fun handleViewIntent(viewIntent: SessionCodeViewIntent): Unit {
        viewModelScope.launch {
            viewIntentFlow.emit(viewIntent)
        }
    }

    /**
     * Initializes the SDK and persists the resulting place identifier.
     *
     * @param sessionCode the session code used to initialize the SDK.
     * @return state changes describing initialization progress and its result.
     */
    private fun initSDK(sessionCode: String): Flow<SessionCodePartialState> =
        flow<SessionCodePartialState> {
            val namiSDK = NamiSDUISDK(sessionCode = sessionCode)
            AppModuleDI.addNew(namiSDK)
            val currentPlaceID = NamiSDK.placeId()
            if (currentPlaceID != null) {
                namiLocalStorage.saveCurrentPlaceId(currentPlaceID)
                emit(SessionCodePartialState.InitSuccess)
            } else {
                namiLocalStorage.clearCurrentPlaceId()
                namiLocalStorage.clearCustomerAccessToken()
                emit(SessionCodePartialState.InitFail(error = "can not load the place"))
            }
        }.onStart {
            emit(SessionCodePartialState.Loading)
        }.catch { e -> emit(SessionCodePartialState.InitFail(error = e.message)) }

    /**
     * Loads saved-session status until both required values are ready.
     *
     * The flow remains active while the session is incomplete so the place-ID write from SDK
     * initialization and the token write from the SDK callback can converge. It completes after
     * the first valid session so later credential refreshes cannot replay navigation.
     *
     * @return loading and saved-session states through the first valid session.
     */
    private fun fetchSavedSession(): Flow<SessionCodePartialState> = loadSavedSessionStates(
        namiLocalStorage.customerAccessToken.map { token -> token?.isValid() },
        namiLocalStorage.currentPlaceId,
    )
        .catch {
            emit(
                SessionCodePartialState.LoadedSessionCode(
                    isNeedASessionCode = true,
                )
            )
        }
        .onStart { emit(SessionCodePartialState.Loading) }
}

/**
 * Observes stored credentials until the first valid session is available.
 *
 * @param tokenValidity validity updates for the saved customer token.
 * @param placeIds saved place-ID updates.
 * @return distinct saved-session states through the first valid state, then completes.
 */
@OptIn(ExperimentalCoroutinesApi::class)
internal fun loadSavedSessionStates(
    tokenValidity: Flow<Boolean?>,
    placeIds: Flow<Int?>,
): Flow<SessionCodePartialState> = combine(tokenValidity, placeIds) { isTokenValid, placeId ->
    isTokenValid != true || placeId == null
}
    .distinctUntilChanged()
    .map { isNeedASessionCode ->
        SessionCodePartialState.LoadedSessionCode(
            isNeedASessionCode = isNeedASessionCode,
        )
    }
    .transformWhile { partialState ->
        emit(partialState)
        partialState.isNeedASessionCode
    }
