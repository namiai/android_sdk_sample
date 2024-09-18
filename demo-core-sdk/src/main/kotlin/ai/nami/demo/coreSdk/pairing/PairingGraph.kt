package ai.nami.demo.coreSdk.pairing

import ai.nami.demo.coreSdk.common.lifecycleIsResumed
import ai.nami.demo.coreSdk.pairing.connectWifi.SkyNetAddWifiNetworkNavigation
import ai.nami.demo.coreSdk.pairing.connectWifi.SkyNetAddWifiNetworkRoute
import ai.nami.demo.coreSdk.pairing.connectWifi.SkyNetEnterWifiPasswordNavigation
import ai.nami.demo.coreSdk.pairing.connectWifi.SkyNetEnterWifiPasswordRoute
import ai.nami.demo.coreSdk.pairing.connectWifi.SkyNetScanWifiNetworkNavigation
import ai.nami.demo.coreSdk.pairing.connectWifi.SkyNetScanWifiNetworkRoute
import ai.nami.demo.coreSdk.pairing.connectWifi.SkyNetWifiNetworkErrorNavigation
import ai.nami.demo.coreSdk.pairing.connectWifi.SkyNetWifiNetworkErrorRoute
import ai.nami.demo.coreSdk.pairing.deviceName.SkyNetDeviceNameErrorNavigation
import ai.nami.demo.coreSdk.pairing.deviceName.SkyNetDeviceNameErrorRoute
import ai.nami.demo.coreSdk.pairing.deviceName.SkyNetDeviceNameNavigation
import ai.nami.demo.coreSdk.pairing.deviceName.SkyNetDeviceNameRoute
import ai.nami.demo.coreSdk.pairing.error.SkyNetBluetoothDisconnectedNavigation
import ai.nami.demo.coreSdk.pairing.error.SkyNetBluetoothDisconnectedRoute
import ai.nami.demo.coreSdk.pairing.pingpong.SkyNetPingPongNavigation
import ai.nami.demo.coreSdk.pairing.pingpong.SkyNetPingPongRoute
import ai.nami.demo.coreSdk.pairing.qrCode.SkyNetQRCodeNavigation
import ai.nami.demo.coreSdk.pairing.qrCode.SkyNetQRCodeRoute
import ai.nami.demo.coreSdk.pairing.scanDevice.SkyNetScanDeviceNavigation
import ai.nami.demo.coreSdk.pairing.scanDevice.SkyNetScanDeviceRoute
import ai.nami.demo.coreSdk.pairing.success.SkyNetSuccessNavigation
import ai.nami.demo.coreSdk.pairing.success.SkyNetSuccessRoute
import ai.nami.demo.coreSdk.pairing.thread.SkyNetJoinThreadNetworkFailRoute
import ai.nami.demo.coreSdk.pairing.thread.SkyNetJoinThreadNetworkFailRouteNavigation
import ai.nami.demo.coreSdk.positioning.recommendations.SkyNetWidarRecommendationNavigation
import ai.nami.sdk.model.DeviceCategory
import ai.nami.sdk.pairing.model.PairingErrorCode
import ai.nami.sdk.pairing.viewmodels.di.NamiPairingViewModelModule
import ai.nami.sdk.positioning.viewmodels.di.NamiPositioningViewModelModule
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.fatherofapps.jnav.JNavigation

const val PairingGraphRoute = "sky_net_pairing_graph_route"
fun NavGraphBuilder.pairingGraph(
    onNavigateTo: (navigation: JNavigation, route: String?) -> Unit,
    onBack: (navigation: JNavigation?, inclusive: Boolean) -> Unit,
    onExitPairing: () -> Unit,
    navHostController: NavHostController
) {
    navigation(route = PairingGraphRoute, startDestination = SkyNetQRCodeNavigation.route) {

        composable(
            route = SkyNetQRCodeNavigation.route,
            arguments = SkyNetQRCodeNavigation.arguments()
        ) {
            if (it.lifecycleIsResumed()) {
                val viewModel = NamiPairingViewModelModule.provideScanQRCodeViewModel()
                val categoryName = SkyNetQRCodeNavigation.deviceCategory(it)
                val placeId = SkyNetQRCodeNavigation.placeId(it)
                val zoneId = SkyNetQRCodeNavigation.zoneId(it)
                val roomId = SkyNetQRCodeNavigation.roomId(it)
                val zoneName = SkyNetQRCodeNavigation.zoneName(it)
                SkyNetQRCodeRoute(
                    viewModel = viewModel,
                    onNext = {
                        onNavigateTo(
                            SkyNetScanDeviceNavigation,
                            SkyNetScanDeviceNavigation.createRoute()
                        )
                    },
                    onBack = {
                        onExitPairing()
                    },
                    deviceCategory = DeviceCategory.from(categoryName),
                    placeId = placeId,
                    zoneId = zoneId,
                    roomId = roomId,
                    zoneName = zoneName
                )
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
                    onNavigateBluetoothDisconnectedScreen = {
                        onNavigateTo(
                            SkyNetBluetoothDisconnectedNavigation,
                            SkyNetBluetoothDisconnectedNavigation.createRoute()
                        )
                    }
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
                val zoneName = SkyNetDeviceNameNavigation.zoneName(it)
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
                    onNavigateToErrorScreen = { isBluetoothDisconnected, pairingErrorCode, errorMessage, deviceCategory ->
                        if (isBluetoothDisconnected) {
                            onNavigateTo(
                                SkyNetBluetoothDisconnectedNavigation,
                                SkyNetBluetoothDisconnectedNavigation.createRoute()
                            )
                        } else {
                            onNavigateTo(
                                SkyNetDeviceNameErrorNavigation,
                                SkyNetDeviceNameErrorNavigation.createRoute(
                                    errorCode = pairingErrorCode?.code
                                        ?: PairingErrorCode.Common.code,
                                    errorMessage = errorMessage,
                                    zoneName = zoneName,
                                    deviceCategory = deviceCategory.id
                                )
                            )
                        }
                    }
                )
            }
        }

        composable(
            route = SkyNetDeviceNameErrorNavigation.route,
            arguments = SkyNetDeviceNameErrorNavigation.arguments()
        ) {
            if (it.lifecycleIsResumed()) {
                val viewModel = NamiPairingViewModelModule.provideRenameDeviceErrorViewModel()
                val code = SkyNetDeviceNameErrorNavigation.errorCode(it)

                SkyNetDeviceNameErrorRoute(
                    viewModel = viewModel,
                    pairingErrorCode = PairingErrorCode.from(code),
                    errorMessage = SkyNetDeviceNameErrorNavigation.errorMessage(it),
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
                    onExitPairing = { onExitPairing() },
                    deviceCategory = DeviceCategory.from(
                        SkyNetDeviceNameErrorNavigation.deviceCategory(
                            it
                        )
                    ),
                    zoneName = SkyNetDeviceNameErrorNavigation.zoneName(it)
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
                    onNavigateWifiNetworkErrorScreen = { pairingErrorCode ->
                        onNavigateTo(
                            SkyNetWifiNetworkErrorNavigation,
                            SkyNetWifiNetworkErrorNavigation.createRoute(
                                errorCode = pairingErrorCode?.code
                                    ?: PairingErrorCode.Common.code,
                                isFromPingPong = false
                            )
                        )
                    },
                    onNavigateBluetoothDisconnectedScreen = {
                        onNavigateTo(
                            SkyNetBluetoothDisconnectedNavigation,
                            SkyNetBluetoothDisconnectedNavigation.createRoute()
                        )
                    }
                )
            }
        }

        composable(
            route = SkyNetWifiNetworkErrorNavigation.route,
            arguments = SkyNetWifiNetworkErrorNavigation.arguments()
        ) {
            if (it.lifecycleIsResumed()) {
                val viewModel = NamiPairingViewModelModule.provideCancelPairingViewModel()
                val code = SkyNetWifiNetworkErrorNavigation.errorCode(it)
                val isFromPingPong = SkyNetWifiNetworkErrorNavigation.isFromPingPong(it)
                SkyNetWifiNetworkErrorRoute(
                    viewModel = viewModel,
                    pairingError = PairingErrorCode.from(code),
                    onExitPairing = { onExitPairing() },
                    onRetry = {
                        if (isFromPingPong) {
                            onBack(SkyNetPingPongNavigation, true)
                        } else {
                            onBack(null, false)
                        }
                    })
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
                    onNavigatePairingSuccessScreen = { productId: Int, zoneName: String, isWidar: Boolean, placeId: Int, zoneId: Int, roomId: Int, pairingDeviceInfo ->

                        if (isWidar && null != pairingDeviceInfo) {
                            NamiPositioningViewModelModule.init(navHostController.context)
                            val route = SkyNetWidarRecommendationNavigation.createRoute(
                                deviceUrn = pairingDeviceInfo.deviceUrn,
                                placeId = placeId,
                                devicePort = pairingDeviceInfo.devicePort,
                                deviceHost = pairingDeviceInfo.deviceHost
                            )
                            onNavigateTo(SkyNetWidarRecommendationNavigation, route)
                        } else {
                            onNavigateTo(
                                SkyNetSuccessNavigation, SkyNetSuccessNavigation.createRoute(
                                    productId = productId,
                                    zoneName = zoneName,
                                    isWidar = false,
                                    placeId = placeId,
                                    deviceName = deviceName,
                                    zoneId = zoneId,
                                    roomId = roomId
                                )
                            )
                        }


                    },
                    onNavigateConnectWifiFailScreen = { pairingErrorCode ->
                        onNavigateTo(
                            SkyNetWifiNetworkErrorNavigation,
                            SkyNetWifiNetworkErrorNavigation.createRoute(
                                pairingErrorCode?.code?.code ?: PairingErrorCode.Common.code,
                                true
                            )
                        )
                    },
                    onNavigateJoinThreadNetworkFailScreen = {
                        onNavigateTo(
                            SkyNetJoinThreadNetworkFailRouteNavigation,
                            SkyNetJoinThreadNetworkFailRouteNavigation.createRoute()
                        )
                    },
                    onNavigateBluetoothDisconnectedScreen = {
                        onNavigateTo(
                            SkyNetBluetoothDisconnectedNavigation,
                            SkyNetBluetoothDisconnectedNavigation.createRoute()
                        )
                    }
                )
            }
        }

        composable(route = SkyNetJoinThreadNetworkFailRouteNavigation.route) {
            if (it.lifecycleIsResumed()) {
                val viewModel = NamiPairingViewModelModule.provideCancelPairingViewModel()
                SkyNetJoinThreadNetworkFailRoute(viewModel = viewModel) {
                    onExitPairing()
                }
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
                val isWidar = SkyNetSuccessNavigation.isWidar(it)
                val deviceName = SkyNetSuccessNavigation.deviceName(it)
                SkyNetSuccessRoute(
                    productId = SkyNetSuccessNavigation.productId(it),
                    deviceName = deviceName,
                    zoneName = zoneName,
                    isWidar = isWidar,
                    placeId = placeId,
                    onPairAnotherDevice = {
                        onBack(SkyNetQRCodeNavigation, true)
                        onNavigateTo(
                            SkyNetQRCodeNavigation,
                            SkyNetQRCodeNavigation.createRoute(
                                deviceCategory = DeviceCategory.OPTIONAL.id,
                                placeId = placeId,
                                zoneId = zoneId,
                                roomId = roomId,
                                zoneName = zoneName
                            )
                        )
                    },
                    onPairSuccess = {
                        onExitPairing()
                    }
                )
            }
        }

        composable(route = SkyNetBluetoothDisconnectedNavigation.route) {
            if (it.lifecycleIsResumed()) {
                val viewModel = NamiPairingViewModelModule.provideCancelPairingViewModel()
                SkyNetBluetoothDisconnectedRoute(viewModel = viewModel) {
                    onExitPairing()
                }
            }
        }


    }
}