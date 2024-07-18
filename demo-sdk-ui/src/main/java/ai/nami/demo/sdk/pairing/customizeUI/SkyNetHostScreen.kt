package ai.nami.demo.sdk.pairing.customizeUI

import ai.nami.demo.sdk.pairing.shared.HomeScreen
import ai.nami.demo.sdk.pairing.shared.PairingSuccessScreen
import ai.nami.sdk.NamiSDKUI
import ai.nami.sdk.routing.common.NamiPairingInput
import ai.nami.sdk.routing.common.NamiPositioningInput
import ai.nami.sdk.routing.pairing.ui.navigation.NamiPairingSdkNavigation
import ai.nami.sdk.routing.pairing.ui.navigation.namiPairingSdkGraph
import ai.nami.sdk.routing.positioning.ui.navigation.NamiPositionSdkNavigation
import ai.nami.sdk.routing.positioning.ui.navigation.NamiPositioningNavigation
import ai.nami.sdk.routing.positioning.ui.navigation.NamiPositioningSdkRoute
import ai.nami.sdk.routing.positioning.ui.navigation.namiPositioningSDKGraph
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun SkyNetHostScreen() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        composable(route = "home") {
            HomeScreen { sessionCode, roomId ->
                val params = mutableMapOf<String, String>()
                params["from"] = "home"

                NamiSDKUI.initPairing(context = navController.context)

                navController.navigate(
                    NamiPairingSdkNavigation.createRoute(
                        input = NamiPairingInput(
                            sessionCode = sessionCode,
                            roomId = roomId,
                            parameters = params
                        )

                    )
                )
            }
        }


        namiPairingSdkGraph(
            navController = navController,
            onPairSuccess = { output ->
                val isWidarDevice = output.isWidarDevice
                val placeId = output.placeId
                val deviceName = output.deviceName
                val deviceUrn = output.listPairedDevices.firstOrNull()?.deviceUrn
                if (isWidarDevice && deviceUrn != null) {
                    NamiSDKUI.initPositioning(context = navController.context)
                    val widarRoute = NamiPositionSdkNavigation.createRoute(
                        input = NamiPositioningInput(
                            deviceUrn = deviceUrn,
                            placeId = placeId,
                            deviceName = deviceName
                        )

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

        composable(
            route = SkyNetPairingGuideNavigation.route,
            arguments = SkyNetPairingGuideNavigation.arguments()
        ) {
            val startedScreenName = SkyNetPairingGuideNavigation.startedScreenName(it)
            SkyNetPairingGuideRoute(startedScreenName = startedScreenName,
                onNext = {

                NamiPairingSdkNavigation.resumeRoute()?.let {route ->
                    navController.navigate(route){
                        popUpTo(SkyNetPairingGuideNavigation.route){
                            inclusive = true
                        }
                    }
                }
            },
                onCancel = {
                    NamiPairingSdkNavigation.resumeRoute(shouldCancelPairing = true)?.let {route ->
                        navController.navigate(route){
                            popUpTo(SkyNetPairingGuideNavigation.route){
                                inclusive = true
                            }
                        }
                    }
                }
            )
        }

    }
}