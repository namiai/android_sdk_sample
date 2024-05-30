package ai.nami.sdk.sample.pairing.shared

import ai.nami.sdk.pairing.ui.navigation.NamiPairingSdkNavigation
import ai.nami.sdk.pairing.ui.navigation.NamiParameterKey
import ai.nami.sdk.pairing.ui.navigation.namiPairingSdkGraph
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController


@Composable
fun HostScreen(showDeviceCategory: Boolean = true) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "home") {
        composable(route = "home") {
            HomeScreen(showDeviceCategory) { sessionCode, roomId, deviceCategory ->
                val params = mutableMapOf<String, String>()
                // optional param
                deviceCategory?.let {
                    params[NamiParameterKey.DeviceCategory.key] = it
                }

                navController.navigate(
                    NamiPairingSdkNavigation.createRoute(
                        sessionCode = sessionCode,
                        roomId = roomId,
                        parameters = params
                    )
                )
            }
        }

        namiPairingSdkGraph(
            navController = navController,
            onPairSuccess = { listPairingDeviceInfo, parameters ->

                navController.navigate("pair-success")
            }
        )

        composable("pair-success") {
            PairingSuccessScreen()
        }
    }
}