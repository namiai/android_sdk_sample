package ai.nami.demo_sdk_fragment

import ai.nami.sdk_ui_extensions.NamiSdkUiExtensions
import ai.nami.sdk_ui_extensions.config.NamiMeasureSystem
import ai.nami.sdk_ui_extensions.config.SdkConfig
import ai.nami.sdk_ui_extensions.entry_point.NamiSdkUiExtensionsEntryPoint
import ai.nami.sdk_ui_extensions.ui.navigation.sdkUiExtensionsGraph
import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@SuppressLint("ViewModelConstructorInComposable")
@Composable
fun MainNavHost(navController: NavHostController) {
    val startDestination = "main_screen"

    NavHost(navController, startDestination = startDestination) {

        composable(startDestination) {
            HomeScreen(onPresentTemplate = { clientID, typeEntryPoint, shouldCreateDefaultRoomForNewZone, appearance, baseUrl, language ->
                val currentState = mutableMapOf<String, String>()
                currentState["should_show_pairing_success"] = "0"
                val entryPoint = when (typeEntryPoint) {
                    TypeStartingEntryPoint.Settings -> NamiSdkUiExtensionsEntryPoint().settingUrl
                    TypeStartingEntryPoint.StartingSetupASingleDevice -> NamiSdkUiExtensionsEntryPoint().startSetupASingleDeviceUrl
                    else -> NamiSdkUiExtensionsEntryPoint().startSetupAKitUrl
                }
                val route = NamiSdkUiExtensions.presentTemplate(
                    navController.context,
                    entryPoint,
                    sdkConfig = SdkConfig(
                        baseUrl = baseUrl,
                        countryCode = "us",
                        measureSystem = NamiMeasureSystem.METRIC,
                        clientID = clientID.ifEmpty { "client_001" },
                        language = language,
                        appearance = appearance,
                        topologyRoomsSupported = !shouldCreateDefaultRoomForNewZone
                    ),
                )
                navController.navigate(route)
            }, viewModel = HomeViewModel())
        }

        sdkUiExtensionsGraph(navController = navController, onExit = {
            // the user cancels the setting up flow
            // you can navigate to another screen
            // if you don't do anything, the system will back to the screen before opening the SDK
        }, onFinish = { output ->
            navController.navigate("fake_pairing_screen")
        })


        composable(
            route = "fake_pairing_screen"
        ) { backStackEntry ->
            FakePairingScreen()
        }


    }
}

@Composable
fun FakePairingScreen() {
    Box(
        Modifier
            .fillMaxSize()
            .padding(16.dp), contentAlignment = Alignment.Center
    ) {
        Text("Pairing Success")
    }
}