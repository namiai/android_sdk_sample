package ai.nami.demo.coreSdk.pairing.connectWifi

import ai.nami.sdk.pairing.viewmodels.connectwifi.addanotherwifi.AddAnotherWifiNetworkViewIntent
import ai.nami.sdk.pairing.viewmodels.connectwifi.addanotherwifi.AddAnotherWifiNetworkViewModel
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
    baseRoute = "sky_net_add_wifi_network_route",
    destination = "sky_net_add_wifi_network_destination",
    name = "SkyNetAddWifiNetworkNavigation",
    arguments = [
        JNavArg(
            name = "deviceName",
            type = String::class,
            isNullable = false
        )
    ]
)
@Composable
fun SkyNetAddWifiNetworkRoute(
    viewModel: AddAnotherWifiNetworkViewModel,
    onBack: () -> Unit,
    onNavigateConnectWifiNetwork: (isJoinThread: Boolean) -> Unit,
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val viewIntentChannel = remember {
        Channel<AddAnotherWifiNetworkViewIntent>(Channel.UNLIMITED)
    }

    LaunchedEffect(key1 = Unit) {
        withContext(Dispatchers.Main.immediate) {
            viewIntentChannel.consumeAsFlow().onEach(viewModel::handleViewIntent).collect()
        }
    }

    val sendViewIntent: (AddAnotherWifiNetworkViewIntent) -> Unit = remember {
        { viewIntent -> viewIntentChannel.trySend(viewIntent) }
    }
}

@Composable
private fun SkyNetAddWifiNetworkScreen() {

}