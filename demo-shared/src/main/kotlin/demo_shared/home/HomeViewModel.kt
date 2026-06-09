package demo_shared.home

import ai.nami.sdk.NamiSDK
import ai.nami.sdk.common.NamiLog
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import demo_shared.NamiLocalStorage
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
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ai.nami.sdk.model.device.PlaceDevicesQuery
import ai.nami.sdk.publicApis.NamiSdkApi


@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel(
    private val namiLocalStorage: NamiLocalStorage,
    private val namiSdkApi: NamiSdkApi,
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

                    val isNeedASessionCode = token?.isValid() == false || placeId == null

                    if (isNeedASessionCode) {
                        emit(
                            HomePartialState.LoadedSessionCode(
                                isNeedASessionCode = true,
                                place = null,
                                listDevices = emptyList(),
                                clientID = ""
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

                    val place = namiSdkApi.getPlaceById(placeId = placeId)
                    val listDevices =
                        namiSdkApi.listDevices(query = PlaceDevicesQuery(placeIds = listOf(placeId)))


                    emit(
                        HomePartialState.LoadedSessionCode(
                            isNeedASessionCode = false,
                            place = place,
                            listDevices = listDevices,
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
                            place = null,
                            listDevices = emptyList(),
                            clientID = ""
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
                        place = null,
                        listDevices = emptyList(),
                        clientID = ""
                    )
                )
            }
            .onStart { emit(HomePartialState.LoadingSessionCode) }

    private fun Flow<HomeViewIntent>.toPartialState(): Flow<HomePartialState> {
        return flatMapLatest {
            when (it) {
                is HomeViewIntent.InitNamiSDK -> initSDKOrUseCurrentState(it.clientID)
                is HomeViewIntent.OpenedSDK -> flow {
                    emit(HomePartialState.InitSuccess(false))
                }

                is HomeViewIntent.SignOut -> signOut()
            }
        }
    }

    private fun initSDKOrUseCurrentState(
        clientID: String
    ): Flow<HomePartialState> =  flow {
        namiLocalStorage.saveClientId(clientID)
        val currentState = uiState.value
        val canOpenWithSavedSession =
            currentState.isNeedASessionCode == false && currentState.place?.id != null
        emit(HomePartialState.InitSuccess(canOpenWithSavedSession))
    }

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



}
