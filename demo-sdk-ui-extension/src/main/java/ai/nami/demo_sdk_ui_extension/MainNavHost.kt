package ai.nami.demo_sdk_ui_extension

import ai.nami.sdk.NamiSDKUI
import ai.nami.sdk.routing.common.NamiPairingInput
import ai.nami.sdk.routing.pairing.ui.navigation.namiPairingSdkGraph
import ai.nami.sdk_ui_extensions.NamiSdkUiExtensions
import ai.nami.sdk_ui_extensions.entry_point.NamiSdkUiExtensionsEntryPoint
import ai.nami.sdk_ui_extensions.ui.navigation.sdkUiExtensionsGraph
//import ai.nami.sdk.NamiSDKUI
//import ai.nami.sdk.routing.common.NamiPairingInput
//import ai.nami.sdk.routing.pairing.ui.navigation.namiPairingSdkGraph
import android.util.Log
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
            HomeScreen(onPresentTemplate = {
                val route = NamiSdkUiExtensions.presentTemplate(
                    navController.context,
                    NamiSdkUiExtensionsEntryPoint().startSetupAKitUrl
                )
                navController.navigate(route)
            }, viewModel = HomeViewModel())
        }

        sdkUiExtensionsGraph(navController = navController, onExit = {
            navController.popBackStack( )
        }, onFinish = { output ->
            val roomId = output.roomID.toString()
            val placeId = output.placeID
            val route = NamiSDKUI.startPairing(
                context = navController.context,
                input = NamiPairingInput(
                    placeId = placeId,
                    roomId = roomId,
                    deviceCategory = null,
                    zoneId = output.zoneID,
                    zoneName = output.zoneName
                )
            )
            Log.e("nami-log", "finish presenting template $roomId")
            navController.navigate(route)

        })

        namiPairingSdkGraph(navController = navController, onPairSuccess = {
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