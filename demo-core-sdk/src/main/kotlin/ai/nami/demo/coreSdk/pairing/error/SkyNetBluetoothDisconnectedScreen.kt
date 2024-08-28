package ai.nami.demo.coreSdk.pairing.error

import ai.nami.sdk.pairing.viewmodels.cancelpairing.CancelPairingViewIntent
import ai.nami.sdk.pairing.viewmodels.cancelpairing.CancelPairingViewModel
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.fatherofapps.jnav.annotations.JNav

@JNav(
    baseRoute = "sky_net_bluetooth_disconnected_route",
    destination = "sky_net_bluetooth_disconnected_destination",
    name = "SkyNetBluetoothDisconnectedNavigation"
)
@Composable
fun SkyNetBluetoothDisconnectedRoute(
    viewModel: CancelPairingViewModel,
    onExitPairing: () -> Unit
) {
    val sendViewIntent: (CancelPairingViewIntent) -> Unit = {
        viewModel.handleViewIntent(it)
    }

    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(key1 = uiState.isCanceled) {
        if (uiState.isCanceled) {
            onExitPairing()
        }
    }

    val isLoading by remember(uiState.isCanceling) {
        derivedStateOf { uiState.isCanceling }
    }

    SkyNetPairingErrorScreen(
        isLoading = isLoading,
        primaryButtonText = "Cancel",
        header = "Error occurred",
        messages = listOf(
            "The bluetooth connection between mobile app and device is interrupted.",
            "Please cancel the session and try again"
        ),
        onPrimaryButtonClick = {
            sendViewIntent(CancelPairingViewIntent.CancelPairing)
        }
    )
}