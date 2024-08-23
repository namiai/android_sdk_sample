package ai.nami.demo.coreSdk

import ai.nami.demo.coreSdk.common.AppState
import ai.nami.demo.coreSdk.common.lifecycleIsResumed
import ai.nami.demo.coreSdk.common.rememberAppState
import ai.nami.demo.coreSdk.pairing.connectWifi.SkyNetAddWifiNetworkNavigation
import ai.nami.demo.coreSdk.pairing.connectWifi.SkyNetAddWifiNetworkRoute
import ai.nami.demo.coreSdk.pairing.connectWifi.SkyNetEnterWifiPasswordNavigation
import ai.nami.demo.coreSdk.pairing.connectWifi.SkyNetEnterWifiPasswordRoute
import ai.nami.demo.coreSdk.pairing.connectWifi.SkyNetScanWifiNetworkNavigation
import ai.nami.demo.coreSdk.pairing.connectWifi.SkyNetScanWifiNetworkRoute
import ai.nami.demo.coreSdk.pairing.deviceName.SkyNetDeviceNameNavigation
import ai.nami.demo.coreSdk.pairing.deviceName.SkyNetDeviceNameRoute
import ai.nami.demo.coreSdk.pairing.fetchPairingInfo.SkyNetFetchPairingInfoNavigation
import ai.nami.demo.coreSdk.pairing.fetchPairingInfo.SkyNetFetchPairingInfoRoute
import ai.nami.demo.coreSdk.pairing.pingpong.SkyNetPingPongNavigation
import ai.nami.demo.coreSdk.pairing.pingpong.SkyNetPingPongRoute
import ai.nami.demo.coreSdk.pairing.qrCode.SkyNetQRCodeNavigation
import ai.nami.demo.coreSdk.pairing.qrCode.SkyNetQRCodeRoute
import ai.nami.demo.coreSdk.pairing.scanDevice.SkyNetScanDeviceNavigation
import ai.nami.demo.coreSdk.pairing.scanDevice.SkyNetScanDeviceRoute
import ai.nami.demo.coreSdk.pairing.success.SkyNetSuccessNavigation
import ai.nami.demo.coreSdk.pairing.success.SkyNetSuccessRoute
import ai.nami.demo.coreSdk.shared.SkyNetInfoNavigation
import ai.nami.demo.coreSdk.shared.SkyNetInfoRoute
import ai.nami.sdk.common.DEFAULT_PLACE_ID
import ai.nami.sdk.common.DEFAULT_ROOM_ID
import ai.nami.sdk.common.DEFAULT_ZONE_ID
import ai.nami.sdk.common.DEFAULT_ZONE_NAME
import ai.nami.sdk.model.DeviceCategory
import ai.nami.sdk.pairing.NamiPairingSdk
import ai.nami.sdk.pairing.viewmodels.di.NamiPairingViewModelModule
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.fatherofapps.jnav.JNavigation

class DemoCoreSDKActivity: ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NamiPairingViewModelModule.init(this)
        setContent {
            // you should replace MaterialTheme by your Theme
            MaterialTheme {
                SkyNetApp()
            }
        }
    }
}

@Composable
fun SkyNetApp(
    appState: AppState = rememberAppState()
) {
    SkyNetHostScreen(
        navHostController = appState.navController,
        onNavigateTo = appState::navigate,
        onBack = appState::back
    )
}

@Composable
fun SkyNetHostScreen(
    navHostController: NavHostController,
    onNavigateTo: (navigation: JNavigation, route: String?) -> Unit,
    onBack: (navigation: JNavigation?, inclusive: Boolean) -> Unit
) {

    val onExitPairing: () -> Unit = {
        NamiPairingSdk.clear()
        onBack(SkyNetInfoNavigation, true)
    }
    NavHost(navController = navHostController, startDestination = SkyNetInfoNavigation.route) {

        composable(route = SkyNetInfoNavigation.route) {
            SkyNetInfoRoute(onNext = { sessionCode, roomId, deviceCategory ->
                val route = SkyNetFetchPairingInfoNavigation.createRoute(
                    sessionCode,
                    roomId,
                    deviceCategory = deviceCategory.categoryName
                )
                onNavigateTo(SkyNetFetchPairingInfoNavigation, route)
            }, onBack = {
                onBack(null, false)
            })

        }

        composable(
            route = SkyNetFetchPairingInfoNavigation.route,
            arguments = SkyNetFetchPairingInfoNavigation.arguments()
        ) {
            if (it.lifecycleIsResumed()) {
                val sessionCode = SkyNetFetchPairingInfoNavigation.sessionCode(it)
                val roomId = SkyNetFetchPairingInfoNavigation.roomId(it)
                val viewModel = NamiPairingViewModelModule.provideFetchPairingPlaceViewModel()
                val categoryName = SkyNetFetchPairingInfoNavigation.deviceCategory(it)
                SkyNetFetchPairingInfoRoute(
                    sessionCode = sessionCode,
                    roomId = roomId,
                    viewModel = viewModel,
                    onNext = {
                        onNavigateTo(
                            SkyNetQRCodeNavigation,
                            SkyNetQRCodeNavigation.createRoute(
                                deviceCategory = categoryName,
                                placeId = DEFAULT_PLACE_ID,
                                zoneId = DEFAULT_ZONE_ID,
                                roomId = DEFAULT_ROOM_ID,
                                zoneName = DEFAULT_ZONE_NAME
                            )
                        )
                    }, onBack = {
                        onBack(null, false)
                    })
            }
        }

        composable(route = SkyNetQRCodeNavigation.route) {
            if (it.lifecycleIsResumed()) {
                val viewModel = NamiPairingViewModelModule.provideScanQRCodeViewModel()
                val categoryName = SkyNetQRCodeNavigation.deviceCategory(it)
                SkyNetQRCodeRoute(viewModel = viewModel, onNext = {
                    onNavigateTo(
                        SkyNetScanDeviceNavigation,
                        SkyNetScanDeviceNavigation.createRoute()
                    )
                }, onBack = {
                    onExitPairing()
                }, deviceCategory = DeviceCategory.from(categoryName))
            }
        }

        composable(route = SkyNetScanDeviceNavigation.route) {
            if (it.lifecycleIsResumed()) {
                val viewModel = NamiPairingViewModelModule.provideScanDeviceViewModel()
                SkyNetScanDeviceRoute(
                    viewModel = viewModel,
                    onBack = { onExitPairing() },
                    onScanDeviceSuccess = { productId, deviceName, zoneName ->
                        onNavigateTo(
                            SkyNetDeviceNameNavigation,
                            SkyNetDeviceNameNavigation.createRoute(
                                defaultName = deviceName,
                                productId = productId,
                                zoneName = zoneName
                            )
                        )
                    },
                    onNavigateBluetoothDisconnectedScreen = {}
                )
            }
        }

        composable(
            route = SkyNetDeviceNameNavigation.route,
            arguments = SkyNetDeviceNameNavigation.arguments()
        ) {
            if (it.lifecycleIsResumed()) {
                val defaultName = SkyNetDeviceNameNavigation.defaultName(it)
                val productId = SkyNetDeviceNameNavigation.productId(it)
                val viewModel = NamiPairingViewModelModule.provideRenameDeviceViewModel()
                SkyNetDeviceNameRoute(
                    viewModel = viewModel,
                    defaultName = defaultName,
                    productId = productId,
                    onBack = {
                        onExitPairing()
                    },
                    onNavigateToPingPongScreen = { deviceName ->
                        onNavigateTo(
                            SkyNetPingPongNavigation,
                            SkyNetPingPongNavigation.createRoute(true, deviceName)
                        )
                    },
                    onNavigateConnectWifiScreen = { isFirstDevice, deviceName ->
                        onNavigateTo(
                            SkyNetScanWifiNetworkNavigation,
                            SkyNetScanWifiNetworkNavigation.createRoute(
                                deviceName,
                                isJoinThreadNetwork = false
                            )
                        )
                    },
                    onNavigateToErrorScreen = { isBluetoothDisconnected, pairingErrorCode, errorMessage -> }
                )
            }
        }

        composable(
            route = SkyNetScanWifiNetworkNavigation.route,
            arguments = SkyNetScanWifiNetworkNavigation.arguments()
        ) {
            if (it.lifecycleIsResumed()) {
                val viewModel = NamiPairingViewModelModule.provideScanWifiNetworkViewModel()
                val deviceName = SkyNetScanWifiNetworkNavigation.deviceName(it)
                val isJoinThreadNetwork = SkyNetScanWifiNetworkNavigation.isJoinThreadNetwork(it)
                SkyNetScanWifiNetworkRoute(
                    viewModel = viewModel,
                    onBack = { onExitPairing() },
                    onNavigateEnterWifiPasswordScreen = { wifiName ->
                        onNavigateTo(
                            SkyNetEnterWifiPasswordNavigation,
                            SkyNetEnterWifiPasswordNavigation.createRoute(
                                wifiName,
                                deviceName,
                                isJoinThreadNetwork
                            )
                        )
                    },
                    onNavigateAddAnotherWifiNetworkScreen = {
                        onNavigateTo(
                            SkyNetAddWifiNetworkNavigation,
                            SkyNetAddWifiNetworkNavigation.createRoute(
                                deviceName,
                                isJoinThreadNetwork
                            )
                        )
                    },
                    onNavigateConnectWifiNetwork = {
                        onNavigateTo(
                            SkyNetPingPongNavigation,
                            SkyNetPingPongNavigation.createRoute(isJoinThreadNetwork, deviceName)
                        )
                    },
                    onNavigateWifiNetworkErrorScreen = {

                    },
                    onNavigateBluetoothDisconnectedScreen = {}
                )
            }
        }


        composable(
            route = SkyNetEnterWifiPasswordNavigation.route,
            arguments = SkyNetEnterWifiPasswordNavigation.arguments()
        ) {
            if (it.lifecycleIsResumed()) {
                val wifiName = SkyNetEnterWifiPasswordNavigation.wifiName(it)
                val viewModel = NamiPairingViewModelModule.provideEnterWifiPasswordViewModel()
                val deviceName = SkyNetEnterWifiPasswordNavigation.deviceName(it)
                val isJoinThread = SkyNetEnterWifiPasswordNavigation.isJoinThreadNetwork(it)
                SkyNetEnterWifiPasswordRoute(
                    viewModel = viewModel,
                    wifiName = wifiName,
                    onBack = {
                        onBack(null, false)
                    },
                    onNavigateConnectWifiNetwork = {
                        onNavigateTo(
                            SkyNetPingPongNavigation,
                            SkyNetPingPongNavigation.createRoute(isJoinThread, deviceName)
                        )
                    }
                )
            }
        }

        composable(
            route = SkyNetAddWifiNetworkNavigation.route,
            arguments = SkyNetAddWifiNetworkNavigation.arguments()
        ) {
            if (it.lifecycleIsResumed()) {
                val viewModel = NamiPairingViewModelModule.provideAddAnotherWifiNetworkViewModel()
                val deviceName = SkyNetAddWifiNetworkNavigation.deviceName(it)
                val isJoinThread = SkyNetAddWifiNetworkNavigation.isJoinThreadNetwork(it)
                SkyNetAddWifiNetworkRoute(
                    viewModel = viewModel,
                    onBack = { onBack(null, false) },
                    onNavigateConnectWifiNetwork = {
                        onNavigateTo(
                            SkyNetPingPongNavigation,
                            SkyNetPingPongNavigation.createRoute(isJoinThread, deviceName)
                        )
                    })
            }
        }

        composable(
            route = SkyNetPingPongNavigation.route,
            arguments = SkyNetPingPongNavigation.arguments()
        ) {
            if (it.lifecycleIsResumed()) {
                val viewModel = NamiPairingViewModelModule.providePingPongViewModel()
                val isJoinThreadNetwork = SkyNetPingPongNavigation.isJoinThreadNetwork(it)
                val deviceName = SkyNetPingPongNavigation.deviceName(it)
                SkyNetPingPongRoute(
                    viewModel = viewModel,
                    isJoinThreadNetwork = isJoinThreadNetwork,
                    onBack = {
                        onExitPairing()
                    },
                    onNavigatePairingSuccessScreen = { productId: Int, zoneName: String, isWidar: Boolean, placeId: Int, zoneId: Int, roomId: Int ->
                        onNavigateTo(
                            SkyNetSuccessNavigation, SkyNetSuccessNavigation.createRoute(
                                productId = productId,
                                zoneName = zoneName,
                                isWidar = isWidar,
                                placeId = placeId,
                                deviceName = deviceName,
                                zoneId = zoneId,
                                roomId = roomId
                            )
                        )
                    },
                    onNavigateConnectWifiFailScreen = {},
                    onNavigateJoinThreadNetworkFailScreen = { /*TODO*/ },
                    onNavigateBluetoothDisconnectedScreen = {}
                )
            }
        }


        composable(
            route = SkyNetSuccessNavigation.route,
            arguments = SkyNetSuccessNavigation.arguments()
        ) {
            if (it.lifecycleIsResumed()) {
                val placeId = SkyNetSuccessNavigation.placeId(it)
                val zoneId = SkyNetSuccessNavigation.zoneId(it)
                val roomId = SkyNetSuccessNavigation.roomId(it)
                val zoneName = SkyNetSuccessNavigation.zoneName(it)
                SkyNetSuccessRoute(
                    productId = SkyNetSuccessNavigation.productId(it),
                    deviceName = SkyNetSuccessNavigation.deviceName(it),
                    zoneName = zoneName,
                    isWidar = SkyNetSuccessNavigation.isWidar(it),
                    placeId = placeId,
                    onPairAnotherDevice = {
                        onBack(SkyNetQRCodeNavigation, true)
                        onNavigateTo(
                            SkyNetQRCodeNavigation,
                            SkyNetQRCodeNavigation.createRoute(
                                deviceCategory = DeviceCategory.UN_SPECIFIED.categoryName,
                                placeId = placeId,
                                zoneId = zoneId,
                                roomId = roomId,
                                zoneName = zoneName
                            )
                        )
                    },
                    onPairSuccess = {
                        // isWidar : navigate to positioning flow
                        onExitPairing()
                    }
                )
            }
        }

    }
}