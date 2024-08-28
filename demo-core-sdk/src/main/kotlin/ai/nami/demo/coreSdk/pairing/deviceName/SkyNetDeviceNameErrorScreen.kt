package ai.nami.demo.coreSdk.pairing.deviceName

import ai.nami.sdk.pairing.model.PairingErrorCode
import ai.nami.sdk.pairing.viewmodels.renamedevice.RenameDeviceErrorViewIntent
import ai.nami.sdk.pairing.viewmodels.renamedevice.RenameDeviceErrorViewModel
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember

@Composable
fun SkyNetDeviceNameErrorRoute(
    viewModel: RenameDeviceErrorViewModel,
    pairingErrorCode: PairingErrorCode,
    errorMessage: String?,
    onNavigateToPingPongScreen: (deviceName: String) -> Unit,
    onNavigateConnectWifiScreen: (Boolean, String) -> Unit,
    onExitPairing: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    val sendViewIntent: (RenameDeviceErrorViewIntent) -> Unit = {
        viewModel.handleViewIntent(it)
    }

    LaunchedEffect(key1 = uiState) {
        if (uiState.isCanceled) {
            onExitPairing()
        }
    }

    val isLoading by remember(uiState.isLoading) {
        derivedStateOf { uiState.isLoading }
    }

//    LaunchedEffect(key1 = uiState.isLoading, key2 = uiState.namiDeviceType) {
//        if (!uiState.isLoading && uiState.namiDeviceType != null) {
//            when (uiState.namiDeviceType) {
//                NamiDeviceType.Thread_End_Device -> {
//                    if (uiState.deviceCategory == DeviceCategory.CONTACT_SENSOR) {
//                        onNavigateConnectWifiScreen(uiState.isFirstDevice, uiState.deviceName)
//                    } else {
//                        onNavigateSetupThreadEndDeviceScreen(uiState.deviceName)
//                    }
//                    onNavigateSetupThreadEndDeviceScreen(uiState.deviceName)
//                }
//
//                NamiDeviceType.Thread_Border_Router_Device -> {
//                    onNavigateSetupThreadBorderRouterScreen(uiState.deviceName)
//                }
//
//                else -> onNavigateConnectWifiScreen(uiState.isFirstDevice, uiState.deviceName)
//            }
//        }
//    }

}

@Composable
private fun SkyNetDeviceNameErrorScreen() {

}