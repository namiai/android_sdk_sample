package ai.nami.demo.coreSdk.pairing.thread

import ai.nami.demo.coreSdk.pairing.error.SkyNetPairingErrorScreen
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.collect

@JNav(
    baseRoute = "sky_net_join_thread_network_route",
    destination = "sky_net_join_thread_network_destination",
)
@Composable
fun SkyNetJoinThreadNetworkFailRoute(
    viewModel: CancelPairingViewModel,
    onExitPairing: () -> Unit
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

    LaunchedEffect(key1 = uiState) {
        if (uiState.isCanceled) {
            onExitPairing()
        }
    }

    val isCancelling by remember(uiState.isCanceling) {
        derivedStateOf {
            uiState.isCanceling
        }
    }

    val cancelPairing: () -> Unit = remember {
        {
            sendViewIntent(CancelPairingViewIntent.CancelPairing)
        }
    }

    val header = "Failed to join Thread network"
    val message =
        listOf("Check that you are using the same mobile phone used to set up devices previously.If you no longer have access to the mobile phone, reset all devices in this zone.")

    SkyNetPairingErrorScreen(
        isLoading = isCancelling,
        primaryButtonText = "Exit setup",
        header = header,
        messages = message,
        onPrimaryButtonClick = cancelPairing,
    )

    BackHandler {
        cancelPairing()
    }


}