package ai.nami.demo.coreSdk.pairing.success

import ai.nami.demo.coreSdk.common.SkyNetButton
import ai.nami.demo.coreSdk.common.SkyNetScaffold
import ai.nami.sdk.pairing.model.PairingDeviceInfo
import ai.nami.sdk.pairing.viewmodels.pairingsuccess.PairingSuccessViewIntent
import ai.nami.sdk.pairing.viewmodels.pairingsuccess.PairingSuccessViewModel
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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


    LaunchedEffect(key1 = uiState) {
        if (uiState.isFinishedPairing) {
//            val outPut = NamiPairingOutput(
//                deviceName = deviceName,
//                placeId = placeId,
//                isWidarDevice = isWidar
//            )
            onPairSuccess(uiState.listPairedDevices)
        } else if (uiState.isReset) {
            onPairAnotherDevice()
        }
    }


    SkyNetSuccessScreen(
        productId = productId,
        zoneName = zoneName,
        deviceName = deviceName,
        onPairAnotherDevice = { sendViewIntent(PairingSuccessViewIntent.PairAnotherDevice) },
        onDonePairing = { sendViewIntent(PairingSuccessViewIntent.FinishPairing) },
        isWidar = isWidar,
        isShowLoading = uiState.isLoading
    )

}

@Composable
private fun SkyNetSuccessScreen(
    productId: Int,
    zoneName: String,
    deviceName: String,
    onPairAnotherDevice: () -> Unit,
    onDonePairing: () -> Unit,
    isWidar: Boolean,
    isShowLoading: Boolean
) {
    val isShowPairAnotherDevice by remember(isShowLoading, isWidar) {
        derivedStateOf { !isShowLoading && !isWidar }
    }

    val mainButtonText by remember(isShowLoading, isWidar) {
        derivedStateOf {
            if (isShowLoading || isWidar) {
                "Next"
            } else {
                "Done"
            }
        }
    }

    SkyNetScaffold(title = "Success", onBack = onDonePairing) {
        Spacer(modifier = Modifier.height(48.dp))
        Text(text = "$deviceName is paired into $zoneName")
        Spacer(modifier = Modifier.height(24.dp))
        SkyNetButton(text = mainButtonText, onClick = onDonePairing, enabled = !isShowLoading)
        Spacer(modifier = Modifier.height(24.dp))
        AnimatedVisibility(visible = isShowPairAnotherDevice) {
            SkyNetButton(text = "Pair Another", onClick = onPairAnotherDevice, enabled = true)
        }
    }

}