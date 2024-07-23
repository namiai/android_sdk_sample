package ai.nami.demo.coreSdk.pairing.success

import ai.nami.sdk.pairing.model.PairingDeviceInfo
import ai.nami.sdk.pairing.viewmodels.pairingsuccess.PairingSuccessViewIntent
import ai.nami.sdk.pairing.viewmodels.pairingsuccess.PairingSuccessViewModel
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
    baseRoute = "sky_net_success_route",
    destination = "sky_net_success_destination",
    name = "SkyNetSuccessNavigation",
    arguments = [
        JNavArg(
            name = "productId",
            type = Int::class,
            isNullable = false
        ),
        JNavArg(
            name = "deviceName",
            type = String::class,
            isNullable = false
        ),
        JNavArg(
            name = "zoneName",
            type = String::class,
            isNullable = false
        ),
        JNavArg(
            name = "isWidar",
            type = Boolean::class,
            isNullable = false
        ),
        JNavArg(
            name = "placeId",
            type = Int::class,
            isNullable = false
        )
    ]
)
@Composable
fun SkyNetSuccessRoute(
    viewModel: PairingSuccessViewModel,
    productId: Int,
    deviceName: String,
    zoneName: String,
    isWidar: Boolean,
    placeId: Int,
    onPairAnotherDevice: () -> Unit,
    onPairSuccess: (listPairedDevices: List<PairingDeviceInfo>) -> Unit
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val viewIntentChannel = remember {
        Channel<PairingSuccessViewIntent>(Channel.UNLIMITED)
    }

    LaunchedEffect(key1 = Unit) {
        withContext(Dispatchers.Main.immediate) {
            viewIntentChannel.consumeAsFlow().onEach(viewModel::handleViewIntent).collect()
        }
    }

    val sendViewIntent: (PairingSuccessViewIntent) -> Unit = remember {
        { viewIntent -> viewIntentChannel.trySend(viewIntent) }
    }
}

@Composable
private fun SkyNetSuccessScreen() {

}