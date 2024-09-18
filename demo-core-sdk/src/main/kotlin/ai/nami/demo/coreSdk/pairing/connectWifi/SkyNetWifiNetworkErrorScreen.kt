package ai.nami.demo.coreSdk.pairing.connectWifi

import ai.nami.demo.coreSdk.pairing.error.SkyNetPairingErrorScreen
import ai.nami.sdk.pairing.model.PairingErrorCode
import ai.nami.sdk.pairing.viewmodels.cancelpairing.CancelPairingViewIntent
import ai.nami.sdk.pairing.viewmodels.cancelpairing.CancelPairingViewModel
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fatherofapps.jnav.annotations.JNav
import com.fatherofapps.jnav.annotations.JNavArg
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.collect


@JNav(
    baseRoute = "sky_net_wifi_network_error_route",
    destination = "sky_net_wifi_network_error_destination",
    name = "SkyNetWifiNetworkErrorNavigation",
    arguments = [
        JNavArg(name = "errorCode", type = Int::class),
        JNavArg(name = "isFromPingPong", type = Boolean::class)
    ]
)
@Composable
fun SkyNetWifiNetworkErrorRoute(
    viewModel: CancelPairingViewModel,
    pairingError: PairingErrorCode,
    onExitPairing: () -> Unit,
    onRetry: () -> Unit
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val viewIntentChannel = remember {
        Channel<CancelPairingViewIntent>(Channel.UNLIMITED)
    }

    LaunchedEffect(key1 = Unit) {
        withContext(Dispatchers.Main.immediate) {
            viewIntentChannel.consumeAsFlow().onEach(viewModel::handleViewIntent).collect()
        }
    }

    val sendViewIntent: (CancelPairingViewIntent) -> Unit = remember {
        { viewIntent -> viewIntentChannel.trySend(viewIntent) }
    }

    LaunchedEffect(key1 = uiState.isCanceled) {
        if (uiState.isCanceled) {
            onExitPairing()
        }
    }

    val (errorHeader, errorMessage) = when (pairingError) {
        PairingErrorCode.IncorrectWifiCredential -> {
            Pair(
                "Incorrect Wi-Fi password",
                "Check that the password you entered for the selected network is correct."
            )
        }

        PairingErrorCode.BleTimeOut -> {
            Pair(
                "Error occurred",
                "Ensure the Wi-Fi credentials are correct and the device is placed close enough to the router."
            )
        }

        else -> {
            Pair(
                "Error occurred",
                "Error occurred"
            )
        }
    }

    val isAllowRetry =
        (pairingError != PairingErrorCode.ExceededMaximumNumberOfRetries && pairingError != PairingErrorCode.BluetoothDisconnected)

    val isLoading by remember(uiState.isCanceling) {
        derivedStateOf { uiState.isCanceling }
    }

    val onBack: () -> Unit = remember {
        {
            sendViewIntent(CancelPairingViewIntent.CancelPairing)
        }
    }

    val exitPairing: () -> Unit = remember {
        {
            sendViewIntent(CancelPairingViewIntent.CancelPairing)
        }
    }

    val (primaryButtonText, secondaryButtonText) = if (isAllowRetry) listOf(
        "Try again",
        "Exit setup"
    ) else listOf("Exit setup", null)


    SkyNetPairingErrorScreen(
        isLoading = isLoading,
        primaryButtonText = primaryButtonText,
        secondaryButtonText = secondaryButtonText,
        header = errorHeader,
        messages = listOf(errorMessage),
        onPrimaryButtonClick = if (isAllowRetry) onRetry else exitPairing,
        onSecondaryButtonClick = if (isAllowRetry) exitPairing else null
    )

    BackHandler {
        onBack()
    }
}