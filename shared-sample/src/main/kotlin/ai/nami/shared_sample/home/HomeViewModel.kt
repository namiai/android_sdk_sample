package ai.nami.shared_sample.home

import ai.nami.shared_sample.AppModuleDI
import ai.nami.shared_sample.NamiLocalStorage
import ai.nami.sdk.model.NamiAccessToken
import ai.nami.sdk.model.device.PlaceDevicesQuery
import ai.nami.sdk.publicApis.NamiSdkApi
import ai.nami.sdk_ui_extensions.NamiSDUISDK
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
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.format.DateTimeParseException

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel(
    private val namiLocalStorage: NamiLocalStorage,
    private val namiSdkApi: NamiSdkApi,
) : ViewModel() {

    private val viewIntentFlow = MutableSharedFlow<HomeViewIntent>()

    val uiState: StateFlow<HomeUIState>

    init {
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
        ) { token, placeId -> token to placeId }
            .distinctUntilChanged()
            .flatMapLatest { (token, placeId) ->
                flow {
                    emit(HomePartialState.LoadingSessionCode)

                    val isNeedASessionCode = token == null || !token.isValid() || placeId == null

                    if (isNeedASessionCode) {
                        emit(
                            HomePartialState.LoadedSessionCode(
                                isNeedASessionCode = true,
                                place = null,
                                listDevices = emptyList()
                            )
                        )
                        return@flow
                    }
                    val  accessToken = NamiAccessToken(
                    accessToken = token.accessToken,
                    refreshToken = token.refreshToken,
                    expiresAt = token.expiresAt
                    )
                    val namiSDK = NamiSDUISDK(accessToken)
                    AppModuleDI.addNew(namiSDK)

                    val place = namiSdkApi.getPlaceById(placeId = placeId)
                    val listDevices =
                        namiSdkApi.listDevices(query = PlaceDevicesQuery(placeIds = listOf(placeId)))


                    emit(
                        HomePartialState.LoadedSessionCode(
                            isNeedASessionCode = false,
                            place = place,
                            listDevices = listDevices
                        )
                    )
                }.catch { e ->
                    emit(
                        HomePartialState.LoadedSessionCode(
                            isNeedASessionCode = true,
                            place = null,
                            listDevices = emptyList()
                        )
                    )
                }
            }.catch { e ->
                emit(
                    HomePartialState.LoadedSessionCode(
                        isNeedASessionCode = true,
                        place = null,
                        listDevices = emptyList()
                    )
                )
            }
            .onStart { emit(HomePartialState.LoadingSessionCode) }

    private fun Flow<HomeViewIntent>.toPartialState(): Flow<HomePartialState> {
        return flatMapLatest {
            when (it) {
                is HomeViewIntent.InitNamiSDK -> initSDKOrUseCurrentState()
                is HomeViewIntent.OpenedSDK -> flow {
                    emit(HomePartialState.InitSuccess(false))
                }

                is HomeViewIntent.SignOut -> signOut()
            }
        }
    }

    private fun initSDKOrUseCurrentState(): Flow<HomePartialState> = flow {
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
            emit(HomePartialState.SignedOut)
        }

    override fun onCleared() {
        super.onCleared()
        // you can use the same the instance of NamiSDK to launch the SDK several times while the app opens
        // or reset it whenever the user exits the SDK
        // HomeViewModel creates instance of NamiSDK, so it should clear it.
      //  AppModuleDI.reset()
    }

}

fun NamiAccessToken?.isValid(): Boolean {
    if (this == null) return false
    if (accessToken.isBlank() || refreshToken.isBlank() || expiresAt.isBlank()) return false

    val expiryInstant = try {
        Instant.parse(expiresAt) // e.g. 2026-06-03T21:26:13.148Z
    } catch (_: DateTimeParseException) {
        return false
    }

    return expiryInstant.isAfter(Instant.now())
}