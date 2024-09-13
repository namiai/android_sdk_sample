package ai.nami.demo.sdk.pairing.customizeUI

import ai.nami.demo.sdk.pairing.shared.HomeScreen
import ai.nami.demo.sdk.pairing.shared.PairingSuccessNavigation
import ai.nami.demo.sdk.pairing.shared.PairingSuccessScreen
import ai.nami.sdk.NamiSDK
import ai.nami.sdk.NamiSDKUI
import ai.nami.sdk.common.NamiLog
import ai.nami.sdk.positioning.viewmodels.di.NamiPositioningViewModelModule
import ai.nami.sdk.routing.common.NamiPairingInput
import ai.nami.sdk.routing.common.NamiPositioningInput
import ai.nami.sdk.routing.pairing.ui.navigation.NamiPairingSdkNavigation
import ai.nami.sdk.routing.pairing.ui.navigation.namiPairingSdkGraph
import ai.nami.sdk.routing.positioning.ui.navigation.NamiPositioningSdkRoute
import ai.nami.sdk.routing.positioning.ui.navigation.namiPositioningSDKGraph
import ai.nami.sdk.sample.pairing.shared.HomeViewModel
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun SkyNetHostScreen() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        composable(route = "home") {
            HomeScreen(viewModel = HomeViewModel()) { roomId, deviceCategory ->
                val params = mutableMapOf<String, String>()
                val route = NamiSDKUI.startPairing(
                    context = navController.context,
                    input = NamiPairingInput(
                        roomId = roomId,
                        parameters = params,
                        deviceCategory = deviceCategory
                    ),
                )
                NamiLog.e("Home Screen start : $route", "debug_sample_nami")
                navController.navigate(route)
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
                    val input = NamiPositioningInput(
                        deviceUrn = deviceUrn,
                        placeId = placeId,
                        deviceName = deviceName
                    )

                    val positioningStartRoute = NamiSDKUI.startPositioning(context = navController.context, input)
                    navController.navigate(positioningStartRoute)
                } else {
                    navController.navigate(
                        PairingSuccessNavigation.createRoute(
                            placeId = placeId,
                            zoneId = output.zoneId,
                            zoneName = output.zoneName,
                            roomId = output.roomId,
                            deviceName = output.deviceName,
                            deviceCategory = output.deviceCategory
                        )
                    )
                }
            },

            )

        composable(
            route = PairingSuccessNavigation.route,
            arguments = PairingSuccessNavigation.arguments()
        ) {

            val placeId = PairingSuccessNavigation.placeId(it)
            val zoneId = PairingSuccessNavigation.zoneId(it)
            val zoneName = PairingSuccessNavigation.getZoneName(it)
            val roomId = PairingSuccessNavigation.roomId(it)
            val deviceName = PairingSuccessNavigation.getDeviceName(it)
            val deviceCategory = PairingSuccessNavigation.deviceCategory(it)

            PairingSuccessScreen(onPairAnotherDevice = {
                val route = NamiPairingSdkNavigation.createRoute(
                    input = NamiPairingInput(
                        roomId = roomId.toString(),
                        deviceCategory = deviceCategory,
                        placeId = placeId,
                        zoneId = zoneId,
                        zoneName = zoneName,
                    ),
                )
                navController.navigate(route)
            }, onFinishPairing = {
                navController.popBackStack("home", false)
            })
        }

        namiPositioningSDKGraph(navController = navController, onCancel = {
            navController.popBackStack(NamiPositioningSdkRoute, true)
        }, onPositionDone = {
            NamiPositioningViewModelModule.reset()
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