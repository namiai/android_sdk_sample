package demo_shared

import ai.nami.sdk.common.NamiLog
import ai.nami.sdk.publicApisImpl.NamiApiModule
import ai.nami.sdk_ui_extensions.NamiSdkUiExtensions
import ai.nami.sdk_ui_extensions.config.NamiMeasureSystem
import ai.nami.sdk_ui_extensions.config.SdkConfig
import ai.nami.sdk_ui_extensions.entry_point.NamiSdkUiExtensionsEntryPoint
import ai.nami.sdk_ui_extensions.entry_point.NamiSdkUiExtensionsUri
import ai.nami.sdk_ui_extensions.entry_point.withEntityID
import ai.nami.sdk_ui_extensions.ui.navigation.sdkUiExtensionsGraph
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import demo_shared.home.HomeScreen
import demo_shared.home.HomeViewModel
import demo_shared.home.TypeStartingEntryPoint
import demo_shared.session_code.SessionCodeScreen
import demo_shared.session_code.SessionCodeViewModel
import kotlin.sequences.ifEmpty


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
            HomeScreen(onPresentTemplate = { clientID, typeEntryPoint, shouldCreateDefaultRoomForNewZone, appearance, baseUrl, customRelativePath, language, countryCode, entityId ->
                val currentState = mutableMapOf<String, String>()
                currentState["should_show_pairing_success"] = "0"
                NamiLog.e(tag = "sdkui", message = "MainNavHost start presenting template")
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


                val route = NamiSdkUiExtensions.presentTemplate(
                    context,
                    entryPoint,
                    sdkConfig = sdkConfig,
                )
                navController.navigate(route)
            }, viewModel = homeViewModel, onNavigateSessionCodeScreen = {
                navController.navigate(startDestination){
                    popUpTo(startDestination) { inclusive = true }
                }
            })
        }

        sdkUiExtensionsGraph(navController = navController, onExit = {
            // the user cancels the setting up flow
            // you can navigate to another screen
            // if you don't do anything, the system will back to the screen before opening the SDK
            navController.navigate("fake_pairing_screen")
        }, onFinish = { output ->
            navController.navigate("fake_pairing_screen")
        })


        composable(
            route = "fake_pairing_screen"
        ) { backStackEntry ->
            FakePairingScreen() {
                navController.popBackStack()
            }
        }


    }
}

@Composable
fun FakePairingScreen(onBack: () -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp), verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Pairing Success",
            style = MaterialTheme.typography.body1.copy(color = MaterialTheme.colors.onBackground)
        )
        Spacer(Modifier.height(16.dp))
        Button(onClick = {
            onBack()
        }) {
            Text(
                "Back to home",
                style = MaterialTheme.typography.body1.copy(
                    color = MaterialTheme.colors.onPrimary
                )
            )
        }
    }
}