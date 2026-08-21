package ai.nami.shared_sample

import ai.nami.shared_sample.home.HomeScreen
import ai.nami.shared_sample.home.HomeViewModel
import ai.nami.shared_sample.home.TypeStartingEntryPoint
import ai.nami.shared_sample.session_code.SessionCodeScreen
import ai.nami.shared_sample.session_code.SessionCodeViewModel
import ai.nami.sdk.common.NamiLog
import ai.nami.sdk.publicApisImpl.NamiApiModule
import ai.nami.sdk_ui_extensions.NamiSDUISDK
import ai.nami.sdk_ui_extensions.config.NamiAppearance
import ai.nami.sdk_ui_extensions.config.NamiMeasureSystem
import ai.nami.sdk_ui_extensions.config.SdkConfig
import ai.nami.sdk_ui_extensions.entry_point.NamiSdkUiExtensionsEntryPoint
import ai.nami.sdk_ui_extensions.entry_point.NamiSdkUiExtensionsUri
import ai.nami.sdk_ui_extensions.entry_point.withEntityID
import ai.nami.sdk_ui_extensions.models.NamiSdkUiExtensionsInput
import ai.nami.sdk_ui_extensions.ui.navigation.sdkUiExtensionsGraph
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

data class CustomEntryPoint(val sdkConfig: SdkConfig, val relativePath: String) :
    NamiSdkUiExtensionsUri {

    override val uri: Uri
        get() = "${sdkConfig.baseUrlWithPath}/$relativePath".toUri()

}

@Composable
fun MainNavHost(navController: NavHostController) {

    val startDestination = "main_screen"
    val context = LocalContext.current.applicationContext

    val namiLocalStorage = remember(context) {
        NamiLocalStorage.getInstance(context)
    }
    val homeViewModel = viewModel {
        val namiSdkApi = NamiApiModule(context).namiSdkApi
        HomeViewModel(namiLocalStorage = namiLocalStorage, namiSdkApi = namiSdkApi)
    }

    val sessionCodeViewModel = viewModel {
        SessionCodeViewModel(namiLocalStorage = namiLocalStorage)
    }

    NavHost(navController, startDestination = startDestination) {

        composable(route = startDestination) {
            SessionCodeScreen(viewModel = sessionCodeViewModel){
                navController.navigate("home_screen"){
                    popUpTo(startDestination) { inclusive = true }
                }
            }
        }

        composable("home_screen") {
            HomeScreen(onPresentTemplate = { clientID, typeEntryPoint, shouldCreateDefaultRoomForNewZone, appearance, baseUrl, customRelativePath, language, countryCode, entityId, placeID ->
                val query = listOf(
                    "clientID" to clientID,
                    "typeEntryPoint" to typeEntryPoint.name,
                    "shouldCreateDefaultRoomForNewZone" to shouldCreateDefaultRoomForNewZone.toString(),
                    "appearance" to appearance.name,
                    "baseUrl" to baseUrl,
                    "customRelativePath" to customRelativePath,
                    "language" to language,
                    "countryCode" to countryCode,
                    "entityId" to entityId,
                    "placeId" to placeID?.toString(),
                ).filter { it.second != null }
                    .joinToString("&") { "${it.first}=${Uri.encode(it.second)}" }

                navController.navigate("nami_sdk?$query")
            }, viewModel = homeViewModel, onNavigateSessionCodeScreen = {
                navController.navigate(startDestination) {
                    popUpTo(startDestination) { inclusive = true }
                }
            })
        }


        composable(
            route = "nami_sdk?clientID={clientID}&typeEntryPoint={typeEntryPoint}" +
                    "&shouldCreateDefaultRoomForNewZone={shouldCreateDefaultRoomForNewZone}" +
                    "&appearance={appearance}&baseUrl={baseUrl}&customRelativePath={customRelativePath}" +
                    "&language={language}&countryCode={countryCode}&entityId={entityId}&placeId={placeId}",
            arguments = listOf(
                navArgument("clientID") { type = NavType.StringType; defaultValue = "" },
                navArgument("typeEntryPoint") {
                    type = NavType.StringType; defaultValue = TypeStartingEntryPoint.Settings.name
                },
                navArgument("shouldCreateDefaultRoomForNewZone") {
                    type = NavType.BoolType; defaultValue = false
                },
                navArgument("appearance") {
                    type = NavType.StringType; defaultValue = NamiAppearance.Light.name
                },
                navArgument("baseUrl") { type = NavType.StringType; defaultValue = "" },
                navArgument("customRelativePath") {
                    type = NavType.StringType; nullable = true; defaultValue = null
                },
                navArgument("language") { type = NavType.StringType; defaultValue = "en_US" },
                navArgument("countryCode") { type = NavType.StringType; defaultValue = "us" },
                navArgument("entityId") {
                    type = NavType.StringType; nullable = true; defaultValue = null
                },
                navArgument("placeId") {
                    type = NavType.StringType; nullable = true; defaultValue = null
                },
            )
        ) { backStackEntry ->
            val args = backStackEntry.arguments

            val clientID = args?.getString("clientID").orEmpty()
            val typeEntryPoint = TypeStartingEntryPoint.valueOf(
                args?.getString("typeEntryPoint") ?: TypeStartingEntryPoint.Settings.name
            )
            val shouldCreateDefaultRoomForNewZone =
                args?.getBoolean("shouldCreateDefaultRoomForNewZone") ?: false
            val appearance = NamiAppearance.valueOf(
                args?.getString("appearance") ?: NamiAppearance.Light.name
            )
            val baseUrl = args?.getString("baseUrl").orEmpty()
            val customRelativePath = args?.getString("customRelativePath")
            val language = args?.getString("language") ?: "en_US"
            val countryCode = args?.getString("countryCode") ?: "us"
            val entityId = args?.getString("entityId")
            val placeId = args?.getString("placeId")
            NamiLog.e(tag = "debug-nav3", message = "MainNavHost Recomposition sdkScreen $baseUrl")


            val sdkConfig = SdkConfig(
                baseUrl = baseUrl,
                countryCode = countryCode,
                measureSystem = NamiMeasureSystem.METRIC,
                clientID = clientID.ifEmpty { "client_001" },
                language = language,
                appearance = appearance,
                topologyRoomsSupported = !shouldCreateDefaultRoomForNewZone,
                applyImePadding = true,
                applyStatusBarPadding = true
            )
            NamiLog.e(tag = "debug-nav3", message = "MainNavHost sdkScreen $sdkConfig")
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
                    if (!entityId.isNullOrBlank()) base.withEntityID(entityId) else base
                }

                TypeStartingEntryPoint.Custom -> CustomEntryPoint(
                    sdkConfig = sdkConfig,
                    relativePath = customRelativePath ?: ""
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
                            navController.popBackStack()
                        }

                        is NamiSDKUIEffect.NavigateToError -> {
                            navController.navigate("error_screen")
                        }

                        is NamiSDKUIEffect.NavigateToSuccessScreen -> {
                            navController.navigate("fake_pairing_screen")
                        }
                    }

                }
            }

            if (placeId != null) {
                namiSDUISDK?.PresentTemplate(
                    templateUrl = entryPoint,
                    sdkConfig = sdkConfig,
                    input = NamiSdkUiExtensionsInput(placeID = placeId.toIntOrNull())
                )
            } else {
                namiSDUISDK?.PresentTemplate(
                    templateUrl = entryPoint,
                    sdkConfig = sdkConfig,
                )
            }

        }

        sdkUiExtensionsGraph(navController = navController, onExit = {
            NamiLog.e(
                tag = "sdkui",
                message = "MainNavHost onExit pop all screens of SdkUiExtensions"
            )
            navController.navigate("error_screen")
        }, onFinish = { output ->
            NamiLog.e(
                tag = "sdkui",
                message = "MainNavHost onFinish in consumer app ${output.parameters}"
            )
            if (output.parameters.get("should_show_pairing_success") == "1") {
                navController.navigate("fake_pairing_screen")
            }
        })

        composable(
            route = "fake_pairing_screen"
        ) { backStackEntry ->
            FakePairingScreen()
        }

        composable("error_screen") {
            ConsumerErrorScreen { navController.popBackStack() }
        }

    }
}

@Composable
fun FakePairingScreen() {
    Box(
        Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(16.dp), contentAlignment = Alignment.Center
    ) {
        Text("Consumer app : Pairing Success")
    }
}

@Composable
fun ConsumerErrorScreen(modifier: Modifier = Modifier, onBack: () -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text("Something went wrong")

        Button(onClick = onBack) { Text("Back to home") }
    }
}