package ai.nami.demo.coreSdk.pairing.connectWifi

import ai.nami.sdk.pairing.model.PairingErrorCode
import ai.nami.sdk.pairing.viewmodels.connectwifi.scanning.ScanWifiNetworkViewIntent
import ai.nami.sdk.pairing.viewmodels.connectwifi.scanning.ScanWifiNetworkViewModel
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
    baseRoute = "sky_net_scan_wifi_network_route",
    destination = "sky_net_scan_wifi_network_destination",
    name = "SkyNetScanWifiNetworkNavigation",
    arguments = [
        JNavArg(
            name = "deviceName",
            type = String::class,
            isNullable = false
        )
    ]
)

@Composable
fun SkyNetScanWifiNetworkRoute(
    viewModel: ScanWifiNetworkViewModel,
    onBack: () -> Unit,
    onNavigateEnterWifiPasswordScreen: (String) -> Unit,
    onNavigateAddAnotherWifiNetworkScreen: () -> Unit,
    onNavigateConnectWifiNetwork: (isJoinThreadNetwork: Boolean) -> Unit,
    onNavigateWifiNetworkErrorScreen: (PairingErrorCode?) -> Unit,
    onNavigateBluetoothDisconnectedScreen: () -> Unit
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val viewIntentChannel = remember {
        Channel<ScanWifiNetworkViewIntent>(Channel.UNLIMITED)
    }

    LaunchedEffect(key1 = Unit) {
        withContext(Dispatchers.Main.immediate) {
            viewIntentChannel.consumeAsFlow().onEach(viewModel::handleViewIntent).collect()
        }
    }

    val sendViewIntent: (ScanWifiNetworkViewIntent) -> Unit = remember {
        { viewIntent -> viewIntentChannel.trySend(viewIntent) }
    }
}