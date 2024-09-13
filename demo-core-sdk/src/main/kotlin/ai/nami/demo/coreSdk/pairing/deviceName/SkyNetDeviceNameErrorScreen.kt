package ai.nami.demo.coreSdk.pairing.deviceName

import ai.nami.demo.coreSdk.pairing.error.SkyNetPairingErrorScreen
import ai.nami.sdk.common.NamiDeviceType
import ai.nami.sdk.model.DeviceCategory
import ai.nami.sdk.pairing.model.PairingErrorCode
import ai.nami.sdk.pairing.viewmodels.renamedevice.RenameDeviceErrorViewIntent
import ai.nami.sdk.pairing.viewmodels.renamedevice.RenameDeviceErrorViewModel
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.fatherofapps.jnav.annotations.JNav
import com.fatherofapps.jnav.annotations.JNavArg

@JNav(
    baseRoute = "sky_net_device_name_error_route",
    destination = "sky_net_device_name_error_destination",
    name = "SkyNetDeviceNameErrorNavigation",
    arguments = [
        JNavArg(name = "errorCode", type = Int::class),
        JNavArg(name = "errorMessage", type = String::class, isNullable = true),
        JNavArg(name = "zoneName", type = String::class, isNullable = false),
        JNavArg(name = "deviceCategory", type = String::class, isNullable = false),
    ]
)

@Composable
fun SkyNetDeviceNameErrorRoute(
    viewModel: RenameDeviceErrorViewModel,
    pairingErrorCode: PairingErrorCode,
    errorMessage: String?,
    onNavigateToPingPongScreen: (deviceName: String) -> Unit,
    onNavigateConnectWifiScreen: (Boolean, String) -> Unit,
    onExitPairing: () -> Unit,
    deviceCategory: DeviceCategory,
    zoneName: String,
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

    LaunchedEffect(key1 = uiState.isLoading, key2 = uiState.namiDeviceType) {
        if (!uiState.isLoading && uiState.namiDeviceType != null) {
            if (uiState.namiDeviceType == NamiDeviceType.Thread_Border_Router_Device) {
                onNavigateConnectWifiScreen(uiState.isFirstDevice, uiState.deviceName)
            } else {
                onNavigateToPingPongScreen(uiState.deviceName)
            }
        }
    }

    val isContactSensor by remember(deviceCategory) {
        derivedStateOf { deviceCategory == DeviceCategory.CONTACT_SENSOR }
    }

    SkyNetDeviceNameErrorScreen(
        pairingErrorCode = pairingErrorCode,
        errorMessage = errorMessage,
        isContactSensor = isContactSensor,
        onBack = { sendViewIntent(RenameDeviceErrorViewIntent.CancelPairing) },
        onTryAgain = { sendViewIntent(RenameDeviceErrorViewIntent.ReTryScanningThreadNetwork) },
        zoneName = zoneName,
        isLoading = isLoading
    )

}

@Composable
private fun SkyNetDeviceNameErrorScreen(
    pairingErrorCode: PairingErrorCode,
    errorMessage: String?,
    isContactSensor: Boolean,
    onBack: () -> Unit,
    onTryAgain: () -> Unit,
    zoneName: String,
    isLoading: Boolean
) {
    val isAllowTryAgain by remember(pairingErrorCode) {
        derivedStateOf { (pairingErrorCode == PairingErrorCode.TooFarYourPlace) }
    }


    val errorHeader = pairingErrorCode.toErrorHeader()
    val errorMessages =
        pairingErrorCode.toErrorMessage(
            zoneName,
            commonErrorMessage = errorMessage,
            isContactSensor = isContactSensor
        )

    val primaryButtonText =
        if (isAllowTryAgain) "Try again" else "Exit pairing"
    val secondaryButtonText =
        if (isAllowTryAgain) "Exit pairing" else null
    val primaryButtonClick = if (isAllowTryAgain) onTryAgain else onBack

    SkyNetPairingErrorScreen(
        isLoading = isLoading,
        primaryButtonText = primaryButtonText,
        secondaryButtonText = secondaryButtonText,
        header = errorHeader,
        messages = errorMessages,
        onPrimaryButtonClick = primaryButtonClick,
        onSecondaryButtonClick = onBack
    )
}

private fun PairingErrorCode.toErrorMessage(
    zoneName: String,
    commonErrorMessage: String?,
    isContactSensor: Boolean
): List<String> {
    val defaultErrorMessage = "Pairing device error"
    return when (this) {
        PairingErrorCode.TooFarYourPlace, PairingErrorCode.NoThreadNetworkAvailable, PairingErrorCode.NoBorderRouterForContactSensor -> {
            if (isContactSensor) {
                listOf(
                    "Move one of your mesh sensors in $zoneName closer to the contact sensor.",
                    "Once done, press the top button on the contact sensor once and try again.",
                )
            } else {
                listOf("Place your device closer to other Thread devices in this zone: $zoneName")
            }
        }

        PairingErrorCode.PairSecondThreadDeviceOnAnotherPhone -> {
            listOf(
                "Check that you are using the same mobile phone used to set up devices previously.",
                "If you no longer have access to the mobile phone, reset all devices in this zone: $zoneName"
            )
        }

        PairingErrorCode.BleTimeOut -> {
            listOf("Timeout error")
        }

        PairingErrorCode.Common -> {
            listOf(commonErrorMessage ?: defaultErrorMessage)
        }

        else -> listOf(defaultErrorMessage)
    }
}

private fun PairingErrorCode.toErrorHeader(): String {
    val defaultErrorHeader = "Unexpected pairing state."
    return when (this) {
        PairingErrorCode.TooFarYourPlace, PairingErrorCode.NoThreadNetworkAvailable, PairingErrorCode.NoBorderRouterForContactSensor -> {
            "No Thread network found"
        }

        PairingErrorCode.PairSecondThreadDeviceOnAnotherPhone -> {
            "Missing Thread credentials"
        }

        else -> defaultErrorHeader
    }
}