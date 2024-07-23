package ai.nami.demo.coreSdk.pairing.scanDevice

import ai.nami.sdk.pairing.viewmodels.scandevice.ScanDeviceViewIntent
import ai.nami.sdk.pairing.viewmodels.scandevice.ScanDeviceViewModel
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fatherofapps.jnav.annotations.JNav
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext

@JNav(
    baseRoute = "sky_net_scan_device_route",
    destination = "sky_net_scan_device_destination",
    name = "SkyNetScanDeviceNavigation"
)
@Composable
fun SkyNetScanDeviceRoute(
    viewModel: ScanDeviceViewModel,
    onBack: () -> Unit,
    onScanDeviceSuccess: (productId: Int, deviceName: String, zoneName: String) -> Unit,
    onNavigateBluetoothDisconnectedScreen: () -> Unit
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val viewIntentChannel = remember {
        Channel<ScanDeviceViewIntent>(Channel.UNLIMITED)
    }

    LaunchedEffect(key1 = Unit) {
        withContext(Dispatchers.Main.immediate) {
            viewIntentChannel.consumeAsFlow().onEach(viewModel::handleViewIntent).collect()
        }
    }

    val sendViewIntent: (ScanDeviceViewIntent) -> Unit = remember {
        { viewIntent -> viewIntentChannel.trySend(viewIntent) }
    }
}


@Composable
private fun SkyNetScanDeviceScreen() {

}