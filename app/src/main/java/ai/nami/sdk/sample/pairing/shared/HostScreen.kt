package ai.nami.sdk.sample.pairing.shared

import ai.nami.sdk.pairing.ui.navigation.NamiPairingSdkNavigation
import ai.nami.sdk.pairing.ui.navigation.namiPairingSdkGraph
import ai.nami.sdk.sample.utils.formatDeviceUrn
import ai.nami.sdk.widar.core.WidarSdkSession
import ai.nami.sdk.widar.ui.navigation.NamiWidarSdkRoute
import ai.nami.sdk.widar.ui.navigation.WidarSdkNavigation
import ai.nami.sdk.widar.ui.navigation.namiWidarSDKGraph
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
            onPairSuccess = { listPairingDeviceInfo, parameters, output ->

                val isWidarDevice = output.isWidarDevice
                val placeId = output.placeId
                val deviceName = output.deviceName
                val deviceUrn = listPairingDeviceInfo.firstOrNull()?.deviceUrn

                if (parameters != null && isWidarDevice && deviceUrn != null && WidarSdkSession.initFromPairingParameter(
                        parameters
                    )
                ) {
                    val formatDeviceUrn = formatDeviceUrn(deviceUrn, isLowerCase = false)
                    val widarRoute = WidarSdkNavigation.createRoute(
                        deviceUrn = formatDeviceUrn,
                        placeId = placeId,
                        deviceName = deviceName
                    )
                    navController.navigate(widarRoute)
                } else {
                    navController.navigate("pair-success")
                }

            }
        )

        composable("pair-success") {
            PairingSuccessScreen()
        }

        namiWidarSDKGraph(navController = navController, onCancel = {
            navController.popBackStack(NamiWidarSdkRoute, true)
        }, onPositionDone = {
            navController.navigate("pair-success") {
                // make sure that you do this step in  your project
                popUpTo(NamiWidarSdkRoute)
            }
        })
    }
}