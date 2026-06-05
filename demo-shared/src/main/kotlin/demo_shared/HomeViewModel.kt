package demo_shared

import ai.nami.sdk.NamiSDK
import ai.nami.sdk.common.NamiLog
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.time.format.DateTimeParseException
import java.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel(
    private val namiLocalStorage: NamiLocalStorage,
) : ViewModel() {

    private val viewIntentFlow = MutableSharedFlow<HomeViewIntent>()

    val uiState: StateFlow<HomeUIState>

    init {
        NamiLog.e("debug_sample_nami", "HomeViewModel init")
        val initialState = HomeUIState()
        uiState =
            merge(
                fetchSavedSession(),
                viewIntentFlow.toPartialState()
            ).scan(initialState) { currentState, partialState ->
                partialState.reduce(currentState)
            }.stateIn(viewModelScope, SharingStarted.Eagerly, initialState)
    }

    fun handleViewIntent(viewIntent: HomeViewIntent) {
        viewModelScope.launch {
            viewIntentFlow.emit(viewIntent)
        }
    }

    private fun fetchSavedSession(): Flow<HomePartialState> =
        combine(
            namiLocalStorage.customerAccessToken,
            namiLocalStorage.currentPlaceId,
            namiLocalStorage.clientId,
        ) { token, placeId, clientID -> Triple(token, placeId, clientID) }
            .distinctUntilChanged()
            .flatMapLatest { (token, placeId, clientID) ->
                flow {
                    emit(HomePartialState.LoadingSessionCode)

                    if (!token.isValid() || placeId == null) {
                        emit(
                            HomePartialState.LoadedSessionCode(
                                isNeedASessionCode = true,
                                placeID = null,
                                clientID = clientID
                            )
                        )
                        return@flow
                    }
                    NamiSDK.saveCurrentSelectedPlaceID(placeId)
                    NamiSDK.saveCustomerAccessToken(
                        access = token?.accessToken,
                        refresh = token?.refreshToken,
                        expiresAt = token?.expiresAt
                    )
                    emit(
                        HomePartialState.LoadedSessionCode(
                            isNeedASessionCode = false,
                            placeID = placeId,
                            clientID = clientID
                        )
                    )
                }.catch { e ->
                    NamiLog.e(
                        tag = "debug-session",
                        message = "fetchSavedSession error: ${e.message}"
                    )
                    emit(
                        HomePartialState.LoadedSessionCode(
                            isNeedASessionCode = true,
                            placeID = null,
                            clientID = clientID
                        )
                    )
                }
            }.catch { e ->
                NamiLog.e(
                    tag = "debug-session",
                    message = "fetchSavedSession upstream error: ${e.message}"
                )
                emit(
                    HomePartialState.LoadedSessionCode(
                        isNeedASessionCode = true,
                        placeID = null,
                        clientID = ""
                    )
                )
            }
            .onStart { emit(HomePartialState.LoadingSessionCode) }

    private fun Flow<HomeViewIntent>.toPartialState(): Flow<HomePartialState> {
        return flatMapLatest {
            when (it) {
                is HomeViewIntent.InitNamiSDK -> initSDKOrUseCurrentState(
                    sessionCode = it.sessionCode,
                    clientID = it.clientID
                )
                is HomeViewIntent.OpenedSDK -> flow {
                    emit(HomePartialState.InitSuccess(false))
                }

                is HomeViewIntent.SignOut -> signOut()
            }
        }
    }

    private fun initSDKOrUseCurrentState(
        sessionCode: String?,
        clientID: String
    ): Flow<HomePartialState> = flow {
        namiLocalStorage.saveClientId(clientID)

        if (!sessionCode.isNullOrBlank()) {
            emitAll(initSDK(sessionCode))
        } else {
            val currentState = uiState.value
            val canOpenWithSavedSession =
                currentState.isNeedASessionCode == false && currentState.placeID != null
            emit(HomePartialState.InitSuccess(canOpenWithSavedSession))
        }
    }.catch { e -> emit(HomePartialState.InitFail(error = e.message)) }

    private fun initSDK(sessionCode: String): Flow<HomePartialState> =
        flow<HomePartialState> {
            val isInitSDKSuccess = NamiSDK.init(sessionCode)
            if (isInitSDKSuccess) {
                NamiSDK.placeId()?.let { currentPlaceID ->
                    NamiLog.e(message = "currentPlaceID $currentPlaceID", tag = "debug-session")
                    namiLocalStorage.saveCurrentPlaceId(currentPlaceID)
                } ?: namiLocalStorage.clearCurrentPlaceId()
            }
            emit(HomePartialState.InitSuccess(isInitSDKSuccess))
        }.onStart {
            emit(HomePartialState.Loading)
        }.catch { e -> emit(HomePartialState.InitFail(error = e.message)) }

    private fun signOut(): Flow<HomePartialState> =
        flow<HomePartialState> {
            namiLocalStorage.clearCurrentPlaceId()
            namiLocalStorage.clearCustomerAccessToken()
            emit(HomePartialState.SignedOut)
        }.onStart {
            emit(HomePartialState.SigningOut)
        }.catch { e ->
            NamiLog.e(tag = "debug-session", message = "signOut error: ${e.message}")
            emit(HomePartialState.SignedOut)
        }


    private fun CustomerAccessToken?.isValid(): Boolean {
        if (this == null) return false
        if (accessToken.isBlank() || refreshToken.isBlank() || expiresAt.isBlank()) return false

        val expiryInstant = try {
            Instant.parse(expiresAt) // e.g. 2026-06-03T21:26:13.148Z
        } catch (_: DateTimeParseException) {
            return false
        }

        return expiryInstant.isAfter(Instant.now())
    }
}