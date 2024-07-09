package ai.nami.sdk.sample.pairing.shared


import ai.nami.sdk.NamiSDKUI
import ai.nami.sdk.pairing.viewmodels.di.NamiPairingViewModelModule
import ai.nami.sdk.positioning.viewmodels.di.NamiPositioningViewModelModule
import ai.nami.sdk.routing.pairing.ui.navigation.NamiPairingSdkNavigation
import ai.nami.sdk.routing.pairing.ui.navigation.namiPairingSdkGraph
import ai.nami.sdk.routing.positioning.ui.navigation.NamiPositionSdkNavigation
import ai.nami.sdk.routing.positioning.ui.navigation.NamiPositioningSdkRoute
import ai.nami.sdk.routing.positioning.ui.navigation.namiPositioningSDKGraph
import android.util.Log
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
                val params = mutableMapOf<String, String>()
                params["from"] = "home"

                NamiSDKUI.initPairing(context = navController.context)

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
            onPairSuccess = { listPairingDeviceInfo, parameters, output ->

                val isWidarDevice = output.isWidarDevice
                val placeId = output.placeId
                val deviceName = output.deviceName
                val deviceUrn = listPairingDeviceInfo.firstOrNull()?.deviceUrn
                if (isWidarDevice && deviceUrn != null) {
                    NamiSDKUI.initPositioning(context = navController.context)
                    val widarRoute = NamiPositionSdkNavigation.createRoute(
                        deviceUrn = deviceUrn,
                        placeId = placeId,
                        deviceName = deviceName
                    )
                    navController.navigate(widarRoute)
                } else {
                    NamiSDKUI.resetPairingSession()
                    navController.navigate("pair-success")
                }
            },
            onPairAnotherDevice = null
        )

        composable("pair-success") {
            PairingSuccessScreen(onBack = {
                navController.popBackStack("home", false)
            })
        }

        namiPositioningSDKGraph(navController = navController, onCancel = {
            navController.popBackStack(NamiPositioningSdkRoute, true)
        }, onPositionDone = {
            NamiSDKUI.resetPairingSession()
            NamiSDKUI.resetPositioningSession()
            navController.navigate("pair-success") {
                // make sure that you do this step in  your project
                popUpTo(NamiPositioningSdkRoute)
            }
        })

    }
}