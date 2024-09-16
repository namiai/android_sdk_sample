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
import ai.nami.demo.coreSdk.shared.SkyNetInfoNavigation
import ai.nami.demo.coreSdk.shared.SkyNetInfoRoute
import ai.nami.demo.coreSdk.shared.SkyNetInfoViewModel
import ai.nami.sdk.NamiSDK
import ai.nami.sdk.model.DeviceCategory
import ai.nami.sdk.pairing.NamiPairingSdk
import ai.nami.sdk.pairing.model.PairingErrorCode
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
        NamiPairingSdk.reset()
        onBack(SkyNetInfoNavigation, true)
    }
    NavHost(navController = navHostController, startDestination = SkyNetInfoNavigation.route) {

        composable(route = SkyNetInfoNavigation.route) {
            if (it.lifecycleIsResumed()) {
                SkyNetInfoRoute(onNext = { roomId, deviceCategory ->
                    val placeInfo = NamiSDK.getPlaceInfo(roomId)
                    val route = SkyNetQRCodeNavigation.createRoute(
                        deviceCategory = deviceCategory.categoryName,
                        roomId = placeInfo.roomId,
                        placeId = placeInfo.placeId,
                        zoneId = placeInfo.zoneId,
                        zoneName = placeInfo.zoneName
                    )

                    onNavigateTo(SkyNetQRCodeNavigation, route)
                }, onBack = {
                    onBack(null, false)
                }, viewModel = SkyNetInfoViewModel())
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
                                    deviceCategory = deviceCategory.categoryName
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
                            navHostController.popBackStack(SkyNetPingPongNavigation.route, true)
                        } else {
                            navHostController.popBackStack()
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

        composable(route = SkyNetBluetoothDisconnectedNavigation.route){
            if(it.lifecycleIsResumed()){
                val viewModel = NamiPairingViewModelModule.provideCancelPairingViewModel()
                SkyNetBluetoothDisconnectedRoute(viewModel = viewModel) {
                    onExitPairing()
                }
            }
        }

    }
}