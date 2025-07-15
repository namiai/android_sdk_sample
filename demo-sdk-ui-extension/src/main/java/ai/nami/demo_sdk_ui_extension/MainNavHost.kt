package ai.nami.demo_sdk_ui_extension

//import ai.nami.sdk.NamiSDKUI
//import ai.nami.sdk.routing.common.NamiPairingInput
//import ai.nami.sdk.routing.pairing.ui.navigation.namiPairingSdkGraph
import ai.nami.sdk_ui_extensions.NamiSdkUiExtensions
import ai.nami.sdk_ui_extensions.config.NamiAppearance
import ai.nami.sdk_ui_extensions.config.NamiMeasureSystem
import ai.nami.sdk_ui_extensions.config.SdkConfig
import ai.nami.sdk_ui_extensions.entry_point.NamiSdkUiExtensionsEntryPoint
import ai.nami.sdk_ui_extensions.ui.navigation.sdkUiExtensionsGraph
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

@Composable
fun MainNavHost(navController: NavHostController) {
    val startDestination = "main_screen"

    NavHost(navController, startDestination = startDestination) {

        composable(startDestination) {
            HomeScreen(onPresentTemplate = { clientId ->
                val route = NamiSdkUiExtensions.presentTemplate(
                    navController.context,
                    NamiSdkUiExtensionsEntryPoint().startSetupAKitUrl,
                    sdkConfig = SdkConfig(
                        baseUrl = "https://mobile-screens.nami.surf/divkit/precompiled_layouts",
                        countryCode = "us",
                        measureSystem = NamiMeasureSystem.METRIC,
                        clientID = clientId,
                        language = "en",
                        appearance = NamiAppearance.Light,
                    )
                )
                navController.navigate(route)
            }, viewModel = HomeViewModel())
        }

        sdkUiExtensionsGraph(navController = navController, onExit = {
            navController.popBackStack( )
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