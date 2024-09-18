package ai.nami.demo.sdk.positioning.shared

import ai.nami.sdk.NamiSDKUI
import ai.nami.sdk.routing.common.NamiPositioningInput
import ai.nami.sdk.routing.positioning.ui.navigation.NamiPositionSdkNavigation
import ai.nami.sdk.routing.positioning.ui.navigation.NamiPositioningSdkRoute
import ai.nami.sdk.routing.positioning.ui.navigation.namiPositioningSDKGraph
import ai.nami.demo.common.NamiLocalStorage
import ai.nami.demo.sdk.positioning.info.WidarNetworkInfoScreen
import ai.nami.demo.sdk.positioning.info.WidarNetworkInfoViewModel
import ai.nami.sdk.NamiSDK
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController


@Composable
fun StandardPositioningHostScreen() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "widar_info") {

        val widarNetworkInfoViewModel =
            WidarNetworkInfoViewModel(NamiLocalStorage.getInstance(context = navController.context))

        namiPositioningSDKGraph(navController = navController, onCancel = {
            navController.popBackStack(NamiPositioningSdkRoute, true)
        }, onPositionDone = {
            NamiSDKUI.clear()
            navController.navigate("done_position") {
                // make sure that you do this step in  your project
                popUpTo(NamiPositioningSdkRoute)
            }
        })

        composable(route = "widar_info") {
            WidarNetworkInfoScreen(viewModel =  WidarNetworkInfoViewModel(NamiLocalStorage.getInstance(context = navController.context))) { placeId, deviceUrn ->
                val widarRoute =   NamiSDKUI.startPositioning(context = navController.context,  input = NamiPositioningInput(
                    deviceUrn = deviceUrn,
                    placeId = placeId,
                    deviceName = "WiDar device's name"
                ))

                navController.navigate(widarRoute)
            }
        }

        composable(route = "done_position") {
            AfterPositioningScreen() {
                navController.popBackStack("widar_info", false)
            }
        }

    }
}

