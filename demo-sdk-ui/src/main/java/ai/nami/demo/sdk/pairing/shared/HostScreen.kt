package ai.nami.demo.sdk.pairing.shared


import ai.nami.sdk.NamiSDKUI
import ai.nami.sdk.common.NamiSdkSession
import ai.nami.sdk.routing.common.NamiPairingInput
import ai.nami.sdk.routing.common.NamiPositioningInput
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

        Log.e("debug_sample_nami", "HostScreen")

        composable(route = "home") {
            HomeScreen { sessionCode, roomId, deviceCategory ->
                val params = mutableMapOf<String, String>()
                params["from"] = "home"
                Log.e("debug_sample_nami", "Home Screen : sessionCode $sessionCode ")
                val realSessionCode = if (sessionCode?.isEmpty() == true) null else sessionCode
                NamiSDKUI.initPairing(navController.context)
                val route = NamiPairingSdkNavigation.createRoute(
                    input = NamiPairingInput(
                        sessionCode = realSessionCode,
                        roomId = roomId,
                        parameters = params,
                        deviceCategory = deviceCategory
                    ),
                )
                Log.e("debug_sample_nami", "Home Screen start : $route ")
                Log.e(
                    "debug_sample_nami",
                    "token : ${NamiSdkSession.getCustomerAccessToken()} -- refresh token: ${NamiSdkSession.getRefreshToken()} ${NamiSdkSession.sessionCode}",
                )
                navController.navigate(
                    route
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
            NamiSDKUI.resetPairingSession()
            NamiSDKUI.resetPositioningSession()
            navController.navigate("pair-success") {
                // make sure that you do this step in  your project
                popUpTo(NamiPositioningSdkRoute)
            }
        })

    }
}