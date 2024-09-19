package ai.nami.demo.sdk.pairing.shared


import ai.nami.sdk.NamiSDKUI
import ai.nami.sdk.common.NamiLog
import ai.nami.sdk.model.DeviceCategory
import ai.nami.sdk.pairing.model.PairingErrorCode
import ai.nami.sdk.routing.common.NamiPairingInput
import ai.nami.sdk.routing.common.NamiPositioningInput
import ai.nami.sdk.routing.pairing.ui.navigation.NamiPairingSdkNavigation
import ai.nami.sdk.routing.pairing.ui.navigation.namiPairingSdkGraph
import ai.nami.sdk.routing.positioning.ui.navigation.NamiPositioningSdkRoute
import ai.nami.sdk.routing.positioning.ui.navigation.namiPositioningSDKGraph
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController


@Composable
fun HostScreen() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "home") {

        composable(route = "home") {
            HomeRoute(onPairNamiDevice = { roomId, deviceCategory ->
                val params = mutableMapOf<String, String>()
                params["from"] = "home"
                val route = NamiPairingSdkNavigation.createRoute(
                    input = NamiPairingInput(
                        roomId = roomId,
                        parameters = params,
                        deviceCategory = deviceCategory
                    ),
                )
                navController.navigate(route)
            }, viewModel = HomeViewModel())

        }


        namiPairingSdkGraph(
            navController = navController,
            onPairSuccess = { output ->
                val isWidarDevice = output.isWidarDevice
                val firstDeviceInfo = output.listPairedDevices.firstOrNull()
                val deviceUrn = output.listPairedDevices.firstOrNull()?.deviceUrn
                val devicePort =
                    if (firstDeviceInfo?.devicePort == 0) null else firstDeviceInfo?.devicePort
                val deviceHost =
                    if (firstDeviceInfo?.deviceHost.isNullOrEmpty()) null else firstDeviceInfo?.deviceHost
                val placeId = output.placeId
                val deviceName = output.deviceName
                if (isWidarDevice && deviceUrn != null) {
                    val widarRoute = NamiSDKUI.startPositioning(
                        context = navController.context, input = NamiPositioningInput(
                            deviceUrn = deviceUrn,
                            placeId = placeId,
                            devicePort = devicePort,
                            deviceIp = deviceHost,
                            deviceName = deviceName,
                            params = null
                        )
                    )
                    navController.navigate(widarRoute)
                } else {
                    val from = output.parameters?.get("from") ?: ""
                    if (from == "home") {
                        navController.navigate("device-screen")
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
                }
            },
            onExitPairing = { output ->
                NamiLog.e("debug_sample_nami","onExitPairing")
                if (output.errorCode == PairingErrorCode.DeviceMismatchQRCode) {
                    // todo: add device category selection, and back to that screen
                    navController.popBackStack("home", false)
                } else {
                    navController.popBackStack("home", false)
                }
            }
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
                        deviceCategory = null,
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
            NamiSDKUI.clear()
            navController.navigate("home") {
                // make sure that you do this step in  your project
                popUpTo(NamiPositioningSdkRoute)
            }
        })

    }
}