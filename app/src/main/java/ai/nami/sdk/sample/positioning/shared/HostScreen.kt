package ai.nami.sdk.sample.positioning.shared

import ai.nami.sdk.sample.data.NamiLocalStorage
import ai.nami.sdk.sample.positioning.info.WidarNetworkInfoScreen
import ai.nami.sdk.sample.positioning.info.WidarNetworkInfoViewModel
import ai.nami.sdk.widar.core.WidarSdkSession
import ai.nami.sdk.widar.ui.navigation.NamiWidarSdkRoute
import ai.nami.sdk.widar.ui.navigation.WidarSdkNavigation
import ai.nami.sdk.widar.ui.navigation.namiWidarSDKGraph
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

        namiWidarSDKGraph(navController = navController, onCancel = {
            navController.popBackStack(NamiWidarSdkRoute, true)
        }, onPositionDone = {
            navController.navigate("done_position") {
                // make sure that you do this step in  your project
                popUpTo(NamiWidarSdkRoute)
            }
        })

        composable(route = "widar_info") {
            WidarNetworkInfoScreen(viewModel = widarNetworkInfoViewModel) { placeId, sessionCode, deviceUrn ->
                WidarSdkSession.init(sessionCode = sessionCode)
                val widarRoute = WidarSdkNavigation.createRoute(
                    deviceUrn = deviceUrn,
                    placeId = placeId,
                    deviceName = "WiDar device's name"
                )
                navController.navigate(widarRoute)

            }
        }

        composable(route = "done_position") {
            AfterPositioningScreen()
        }


    }
}

