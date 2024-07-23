package ai.nami.demo.coreSdk.pairing.pingpong

import ai.nami.sdk.pairing.model.PairingError
import ai.nami.sdk.pairing.viewmodels.pingpong.PairingPingPongViewIntent
import ai.nami.sdk.pairing.viewmodels.pingpong.PairingPingPongViewModel
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
    baseRoute = "sky_net_ping_pong_route",
    destination = "sky_net_ping_pong_destination",
    name = "SkyNetPingPongNavigation",
    arguments = [
        JNavArg(
            name = "isJoinThreadNetwork",
            type = Boolean::class,
            isNullable = false
        ),
        JNavArg(
            name = "deviceName",
            type = String::class,
            isNullable = false
        )
    ]
)
@Composable
fun SkyNetPingPongRoute(
    viewModel: PairingPingPongViewModel,
    isJoinThreadNetwork: Boolean,
    onBack: () -> Unit,
    onNavigatePairingSuccessScreen: (productId: Int, zoneName: String, isWidar: Boolean, placeId: Int) -> Unit,
    onNavigateConnectWifiFailScreen: (errorCode: PairingError?) -> Unit,
    onNavigateJoinThreadNetworkFailScreen: () -> Unit,
    onNavigateBluetoothDisconnectedScreen: () -> Unit
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val viewIntentChannel = remember {
        Channel<PairingPingPongViewIntent>(Channel.UNLIMITED)
    }

    LaunchedEffect(key1 = Unit) {
        withContext(Dispatchers.Main.immediate) {
            viewIntentChannel.consumeAsFlow().onEach(viewModel::handleViewIntent).collect()
        }
    }

    val sendViewIntent: (PairingPingPongViewIntent) -> Unit = remember {
        { viewIntent -> viewIntentChannel.trySend(viewIntent) }
    }
}

@Composable
private fun SkyNetPingPongScreen() {

}