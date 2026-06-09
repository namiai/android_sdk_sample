package demo_shared.session_code

import demo_shared.NamiLocalStorage
import ai.nami.sdk.NamiSDK
import ai.nami.sdk.common.NamiLog
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class SessionCodeViewModel(private val namiLocalStorage: NamiLocalStorage) : ViewModel() {

    private val viewIntentFlow = MutableSharedFlow<SessionCodeViewIntent>()

    val uiState: StateFlow<SessionCodeUIState>

    init {
        val initialState = SessionCodeUIState(isLoading = true)
        uiState =
            merge(
                fetchSavedSession(),
                viewIntentFlow.toPartialState()
            ).scan(initialState) { currentState, partialState ->
                partialState.reduce(currentState)
            }.stateIn(viewModelScope, SharingStarted.Eagerly, initialState)
    }


    private fun Flow<SessionCodeViewIntent>.toPartialState(): Flow<SessionCodePartialState> {
        return flatMapLatest {
            when (it) {
                is SessionCodeViewIntent.InitNamiSDK -> initSDK(it.sessionCode)
            }
        }
    }
    fun handleViewIntent(viewIntent: SessionCodeViewIntent) {
        viewModelScope.launch {
            viewIntentFlow.emit(viewIntent)
        }
    }
    private fun initSDK(sessionCode: String): Flow<SessionCodePartialState> =
        flow<SessionCodePartialState> {
            val isInitSDKSuccess = NamiSDK.init(sessionCode)
            if (isInitSDKSuccess) {
                val currentPlaceID = NamiSDK.placeId()
                if(currentPlaceID != null){
                    NamiLog.e(message = "currentPlaceID $currentPlaceID", tag = "debug-session")
                    namiLocalStorage.saveCurrentPlaceId(currentPlaceID)
                    emit(SessionCodePartialState.InitSuccess)
                }else{
                    namiLocalStorage.clearCurrentPlaceId()
                    namiLocalStorage.clearCustomerAccessToken()
                    emit(SessionCodePartialState.InitFail(error = "can not load the place"))
                }
            }else {
                emit(SessionCodePartialState.InitFail(error = "can not init the SDK"))
            }
        }.onStart {
            emit(SessionCodePartialState.Loading)
        }.catch { e -> emit(SessionCodePartialState.InitFail(error = e.message)) }

    private fun fetchSavedSession(): Flow<SessionCodePartialState> = combine(
        namiLocalStorage.customerAccessToken,
        namiLocalStorage.currentPlaceId,
    ) { token, placeId -> token to placeId }
        .distinctUntilChanged()
        .map { (token, placeId) ->
            val isNeedASessionCode = token?.isValid() == false || placeId == null

            SessionCodePartialState.LoadedSessionCode(
                isNeedASessionCode = isNeedASessionCode,
            ) as SessionCodePartialState
        }
        .catch { e ->
            NamiLog.e(
                tag = "debug-session",
                message = "fetchSavedSession upstream error: ${e.message}"
            )
            emit(
                SessionCodePartialState.LoadedSessionCode(
                    isNeedASessionCode = true,
                )
            )
        }
        .onStart { emit(SessionCodePartialState.Loading) }


}
