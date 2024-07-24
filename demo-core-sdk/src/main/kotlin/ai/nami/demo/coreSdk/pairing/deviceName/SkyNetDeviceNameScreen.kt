package ai.nami.demo.coreSdk.pairing.deviceName

import ai.nami.demo.coreSdk.common.SkyNetButton
import ai.nami.demo.coreSdk.common.SkyNetScaffold
import ai.nami.sdk.common.NamiDeviceType
import ai.nami.sdk.pairing.model.PairingErrorCode
import ai.nami.sdk.pairing.viewmodels.renamedevice.RenameDeviceViewIntent
import ai.nami.sdk.pairing.viewmodels.renamedevice.RenameDeviceViewModel
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fatherofapps.jnav.annotations.JNav
import com.fatherofapps.jnav.annotations.JNavArg
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext


@JNav(
    baseRoute = "sky_net_device_name_route",
    destination = "sky_net_device_name_destination",
    name = "SkyNetDeviceNameNavigation",
    arguments = [
        JNavArg(
            name = "defaultName",
            type = String::class,
            isNullable = false
        ),
        JNavArg(
            name = "productId",
            type = Int::class,
            isNullable = false
        ),
        JNavArg(
            name = "zoneName",
            type = String::class,
            isNullable = false
        ),
    ]
)
@Composable
fun SkyNetDeviceNameRoute(
    viewModel: RenameDeviceViewModel,
    defaultName: String,
    productId: Int,
    onBack: () -> Unit,
    onNavigateSetupThreadBorderRouterScreen: (String) -> Unit,
    onNavigateSetupThreadEndDeviceScreen: (String) -> Unit,
    onNavigateConnectWifiScreen: (Boolean, String) -> Unit,
    onNavigateToErrorScreen: (isBluetoothDisconnected: Boolean, pairingErrorCode: PairingErrorCode?, errorMessage: String?) -> Unit
) {

    var deviceName by remember(defaultName) {
        mutableStateOf(defaultName)
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val viewIntentChannel = remember {
        Channel<RenameDeviceViewIntent>(Channel.UNLIMITED)
    }

    LaunchedEffect(key1 = Unit) {
        withContext(Dispatchers.Main.immediate) {
            viewIntentChannel.consumeAsFlow().onEach(viewModel::handleViewIntent).collect()
        }
    }

    val sendViewIntent: (RenameDeviceViewIntent) -> Unit = remember {
        { viewIntent -> viewIntentChannel.trySend(viewIntent) }
    }

    LaunchedEffect(key1 = uiState.isCanceledPairing) {
        if (uiState.isCanceledPairing) {
            onBack()
        }
    }

    LaunchedEffect(key1 = uiState.isBluetoothDisconnected) {
        if (uiState.isBluetoothDisconnected) {
            onNavigateToErrorScreen(true, null, null)
        }
    }

    LaunchedEffect(
        key1 = uiState.isLoading,
        key2 = uiState.errorMessage,
        key3 = uiState.pairingError
    ) {
        if (!uiState.isLoading && (!uiState.errorMessage.isNullOrBlank() || uiState.pairingError != null)) {
            if (uiState.pairingError?.code != PairingErrorCode.TooFarYourPlace) {
                onNavigateToErrorScreen(
                    false,
                    uiState.pairingError?.code ?: PairingErrorCode.Unknown,
                    uiState.pairingError?.errorMessage ?: uiState.errorMessage,
                )
            }
        }
    }

    LaunchedEffect(key1 = uiState.isLoading, key2 = uiState.namiDeviceType) {
        if (!uiState.isLoading && uiState.namiDeviceType != null) {
            when (uiState.namiDeviceType) {
                NamiDeviceType.Thread_End_Device -> {
                    onNavigateSetupThreadEndDeviceScreen(deviceName)
                }

                NamiDeviceType.Thread_Border_Router_Device -> {
                    onNavigateSetupThreadBorderRouterScreen(deviceName)
                }

                else -> onNavigateConnectWifiScreen(uiState.isFirstDevice, deviceName)
            }
        }
    }

    val errorMessage by remember(uiState.errorMessage, uiState.pairingError) {
        derivedStateOf {
            if (uiState.pairingError != null) uiState.pairingError?.errorMessage
                ?: "Pairing error with code ${uiState.pairingError?.code}" else uiState.errorMessage
        }
    }

    SkyNetDeviceNameScreen(
        deviceName = deviceName,
        onDeviceNameChanged = {
            deviceName = it
        },
        isLoading = uiState.isLoading,
        onRenameDevice = {
            sendViewIntent(RenameDeviceViewIntent.Rename(deviceName))
        },
        onBack = {
            sendViewIntent(RenameDeviceViewIntent.CancelPairing)
        },
        errorMessage = errorMessage
    )

}


@Composable
private fun SkyNetDeviceNameScreen(
    deviceName: String,
    onDeviceNameChanged: (String) -> Unit,
    isLoading: Boolean,
    onRenameDevice: () -> Unit,
    onBack: () -> Unit,
    errorMessage: String?
) {
    val enableButton by remember(deviceName) {
        derivedStateOf { deviceName.isNotEmpty() }
    }
    SkyNetScaffold(
        title = "Rename device",
        onBack = onBack,
        isLoading = isLoading,
        errorMessage = errorMessage
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        OutlinedTextField(
            value = deviceName,
            onValueChange = onDeviceNameChanged,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = "Device's name") }
        )
        Spacer(modifier = Modifier.height(24.dp))
        SkyNetButton(
            text = "Rename",
            onClick = onRenameDevice,
            enabled = enableButton,
            modifier = Modifier.fillMaxWidth(),
        )
    }

}