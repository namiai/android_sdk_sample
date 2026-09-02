package ai.nami.shared_sample


import ai.nami.sdk.publicApisImpl.NamiApiModule
import ai.nami.sdk_ui_extensions.NamiSDUISDK
import ai.nami.sdk_ui_extensions.config.NamiAppearance
import ai.nami.sdk_ui_extensions.config.NamiMeasureSystem
import ai.nami.sdk_ui_extensions.config.SdkConfig
import ai.nami.sdk_ui_extensions.entry_point.NamiSdkUiExtensionsEntryPoint
import ai.nami.sdk_ui_extensions.entry_point.withEntityID
import ai.nami.sdk_ui_extensions.models.NamiSdkUiExtensionsInput
import ai.nami.shared_sample.home.HomeScreen
import ai.nami.shared_sample.home.HomeViewModel
import ai.nami.shared_sample.home.TypeStartingEntryPoint
import ai.nami.shared_sample.session_code.SessionCodeScreen
import ai.nami.shared_sample.session_code.SessionCodeViewModel
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import kotlinx.serialization.Serializable

sealed interface MainNavKey : NavKey

@Serializable
data object SessionCode : MainNavKey

@Serializable
data object Home : MainNavKey

@Serializable
data class NamiSdkKey(
    val clientID: String,
    val typeEntryPoint: String,
    val shouldCreateDefaultRoomForNewZone: Boolean,
    val appearance: String,
    val baseUrl: String,
    val customRelativePath: String?,
    val language: String,
    val countryCode: String,
    val entityId: String?,
    val placeId: Int?
) : MainNavKey

@Serializable
data object FakePairing : MainNavKey

@Serializable
data object ErrorKey : MainNavKey

@Serializable
data object PairingSuccessKey : MainNavKey

object AppModuleDI {
    var sduiSDK: NamiSDUISDK? = null
        private set


    fun addNew(namiSDK: NamiSDUISDK) {
        if (sduiSDK == null) {
            sduiSDK = namiSDK
        }
    }

    fun reset() {
        sduiSDK = null
    }
}

@Composable
fun MainNavDisplay(modifier: Modifier = Modifier) {
    val context = LocalContext.current.applicationContext

    val namiLocalStorage = remember(context) {
        NamiLocalStorage.getInstance(context)
    }


    val backStack = rememberNavBackStack(SessionCode)



    NavDisplay(
        modifier = modifier.fillMaxSize(),
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        ),
        entryProvider = entryProvider {
            entry<SessionCode> {
                val sessionCodeViewModel = viewModel {
                    SessionCodeViewModel(
                        namiLocalStorage = namiLocalStorage
                    )
                }
                SessionCodeScreen(viewModel = sessionCodeViewModel) {
                    backStack.clear()
                    backStack.add(Home)
                }
            }

            entry<Home> {

                val homeViewModel = viewModel {
                    val namiSdkApi = NamiApiModule(context).namiSdkApi
                    HomeViewModel(
                        namiLocalStorage = namiLocalStorage,
                        namiSdkApi = namiSdkApi
                    )
                }

                HomeScreen(
                    onPresentTemplate = { clientID, typeEntryPoint, shouldCreateDefaultRoomForNewZone,
                                          appearance, baseUrl, customRelativePath, language,
                                          countryCode, entityId, placeId ->
                        backStack.add(
                            NamiSdkKey(
                                clientID = clientID,
                                typeEntryPoint = typeEntryPoint.name,
                                shouldCreateDefaultRoomForNewZone = shouldCreateDefaultRoomForNewZone,
                                appearance = appearance.name,
                                baseUrl = baseUrl,
                                customRelativePath = customRelativePath,
                                language = language,
                                countryCode = countryCode,
                                entityId = entityId,
                                placeId = placeId
                            )
                        )
                    },
                    viewModel = homeViewModel,
                    onNavigateSessionCodeScreen = {
                        backStack.add(SessionCode)
                    },
                )
            }

            entry<NamiSdkKey> { key ->

                val typeEntryPoint = TypeStartingEntryPoint.valueOf(key.typeEntryPoint)
                val appearance = NamiAppearance.valueOf(key.appearance)

                val sdkConfig = SdkConfig(
                    baseUrl = key.baseUrl,
                    countryCode = key.countryCode,
                    measureSystem = NamiMeasureSystem.METRIC,
                    clientID = key.clientID.ifEmpty { "client_001" },
                    language = key.language,
                    appearance = appearance,
                    topologyRoomsSupported = !key.shouldCreateDefaultRoomForNewZone,
                    applyImePadding = true,
                    applyStatusBarPadding = true,
                )

                val entryPoint = when (typeEntryPoint) {
                    TypeStartingEntryPoint.SettingsPin -> NamiSdkUiExtensionsEntryPoint().settingsPinsUrl
                    TypeStartingEntryPoint.SettingsEntryExitDelays -> NamiSdkUiExtensionsEntryPoint().settingsEntryExitDelaysUrl
                    TypeStartingEntryPoint.SettingsSensitivity -> NamiSdkUiExtensionsEntryPoint().settingsSensitivityUrl
                    TypeStartingEntryPoint.Settings -> NamiSdkUiExtensionsEntryPoint().settingUrl
                    TypeStartingEntryPoint.StartingSetupASingleDevice -> NamiSdkUiExtensionsEntryPoint().startSetupASingleDeviceUrl
                    TypeStartingEntryPoint.StartingSetupAKit -> NamiSdkUiExtensionsEntryPoint().startSetupAKitUrl
                    TypeStartingEntryPoint.SystemTest -> NamiSdkUiExtensionsEntryPoint().systemTestUrl
                    TypeStartingEntryPoint.SettingsWithEntity -> {
                        val base = NamiSdkUiExtensionsEntryPoint().settingUrl
                        if (!key.entityId.isNullOrBlank()) base.withEntityID(key.entityId) else base
                    }

                    TypeStartingEntryPoint.Custom -> CustomEntryPoint(
                        relativePath = key.customRelativePath ?: "",
                    )
                }

                val namiSDUISDK: NamiSDUISDK? = remember {
                    AppModuleDI.sduiSDK
                }

                val namiSDKViewModel = viewModel {
                    NamiSDKViewModel(namiSDUISDK = namiSDUISDK)
                }

                LaunchedEffect(Unit) {
                    namiSDKViewModel.effect.collect { event ->
                        when (event) {
                            is NamiSDKUIEffect.Back -> {
                                backStack.removeLastOrNull()
                            }

                            is NamiSDKUIEffect.NavigateToError -> {
                                val sdkIndex = backStack.indexOfFirst { it is NamiSdkKey }
                                if (sdkIndex != -1) {
                                    backStack.removeAt(index = sdkIndex)
                                }

                                backStack.add(ErrorKey)
                            }

                            is NamiSDKUIEffect.NavigateToSuccessScreen -> {
                                val sdkIndex = backStack.indexOfFirst { it is NamiSdkKey }
                                if (sdkIndex != -1) {
                                    backStack.removeAt(index = sdkIndex)
                                }
                                backStack.add(PairingSuccessKey)
                            }
                        }

                    }
                }
                if (key.placeId != null) {
                    namiSDUISDK?.PresentTemplate(
                        templateUrl = entryPoint,
                        sdkConfig = sdkConfig,
                        input = NamiSdkUiExtensionsInput(placeID = key.placeId)
                    )
                } else {
                    namiSDUISDK?.PresentTemplate(
                        templateUrl = entryPoint,
                        sdkConfig = sdkConfig,
                    )
                }


            }

            entry<PairingSuccessKey> {
                FakePairingScreen()
            }

            entry<ErrorKey> {
                ConsumerErrorScreen { backStack.removeLastOrNull() }
            }


        },
    )
}
