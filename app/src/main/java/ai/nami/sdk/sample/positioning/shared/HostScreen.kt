package ai.nami.sdk.sample.positioning.shared

import ai.nami.sdk.common.NamiSdkSession
import ai.nami.sdk.positioning.viewmodels.di.NamiPositioningViewModelModule
import ai.nami.sdk.routing.positioning.ui.navigation.NamiPositionSdkNavigation
import ai.nami.sdk.routing.positioning.ui.navigation.NamiPositioningSdkRoute
import ai.nami.sdk.routing.positioning.ui.navigation.namiPositioningSDKGraph
import ai.nami.sdk.sample.data.NamiLocalStorage
import ai.nami.sdk.sample.positioning.info.WidarNetworkInfoScreen
import ai.nami.sdk.sample.positioning.info.WidarNetworkInfoViewModel
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
            NamiPositioningViewModelModule.reset()
            navController.navigate("done_position") {
                // make sure that you do this step in  your project
                popUpTo(NamiPositioningSdkRoute)
            }
        })

        composable(route = "widar_info") {
            WidarNetworkInfoScreen(viewModel = widarNetworkInfoViewModel) { placeId, sessionCode, deviceUrn ->
                NamiPositioningViewModelModule.init(context = navController.context)
                NamiSdkSession.init(sessionCode = sessionCode)
                val widarRoute = NamiPositionSdkNavigation.createRoute(
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

