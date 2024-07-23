package ai.nami.demo.coreSdk.pairing.deviceName

import ai.nami.sdk.pairing.model.PairingErrorCode
import ai.nami.sdk.pairing.viewmodels.renamedevice.RenameDeviceViewIntent
import ai.nami.sdk.pairing.viewmodels.renamedevice.RenameDeviceViewModel
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
        )
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
}