package ai.nami.sdk.sample.pairing.shared

import ai.nami.sdk.pairing.ui.navigation.NamiPairingSdkNavigation
import ai.nami.sdk.pairing.ui.navigation.namiPairingSdkGraph
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController


@Composable
fun HostScreen() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "home") {
        composable(route = "home") {
            HomeScreen { sessionCode, roomId ->
                navController.navigate(
                    NamiPairingSdkNavigation.createRoute(
                        sessionCode = sessionCode,
                        roomId = roomId
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