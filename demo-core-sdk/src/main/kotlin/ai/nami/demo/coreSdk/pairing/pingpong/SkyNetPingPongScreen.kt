package ai.nami.demo.coreSdk.pairing.pingpong

import ai.nami.demo.coreSdk.common.SkyNetScaffold
import ai.nami.sdk.extension.isSessionExpired
import ai.nami.sdk.pairing.model.PairingDeviceInfo
import ai.nami.sdk.pairing.model.PairingError
import ai.nami.sdk.pairing.model.PairingErrorCode
import ai.nami.sdk.pairing.viewmodels.pingpong.PairingPingPongViewIntent
import ai.nami.sdk.pairing.viewmodels.pingpong.PairingPingPongViewModel
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
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
    onExitPairing: (PairingErrorCode?) -> Unit,
    onNavigatePairingSuccessScreen: (productId: Int, zoneName: String, isWidar: Boolean, placeId: Int, zoneId: Int, roomId: Int, pairingDeviceInfo: PairingDeviceInfo?) -> Unit,
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

    LaunchedEffect(key1 = uiState.pairingError) {
        if (uiState.pairingError?.isSessionExpired() == true) {
            onExitPairing(PairingErrorCode.SessionExpired)
        }
    }

    val sendViewIntent: (PairingPingPongViewIntent) -> Unit = remember {
        { viewIntent -> viewIntentChannel.trySend(viewIntent) }
    }

    LaunchedEffect(key1 = Unit) {
        val viewIntent = if (isJoinThreadNetwork) {
            PairingPingPongViewIntent.JoinThreadNetwork
        } else {
            PairingPingPongViewIntent.ConnectWifi
        }
        sendViewIntent(viewIntent)
    }

    LaunchedEffect(key1 = uiState.isCanceledPairing) {
        if (uiState.isCanceledPairing) {
            onBack()
        }
    }

    LaunchedEffect(key1 = uiState.isBluetoothDisconnected) {
        if (uiState.isBluetoothDisconnected) {
            onNavigateBluetoothDisconnectedScreen()
        }
    }

    LaunchedEffect(key1 = uiState.isLoading, key2 = uiState.isSuccess) {
        if (!uiState.isLoading) {
            if (uiState.isSuccess == true) {
                val productId = uiState.productId
                val zoneName = uiState.zoneName
                if (productId != null && zoneName != null) {
                    val pairingDeviceInfo = uiState.listPairedDevices.firstOrNull()
                    onNavigatePairingSuccessScreen(
                        productId,
                        zoneName,
                        uiState.isWidar,
                        uiState.placeId,
                        uiState.zoneId,
                        uiState.roomId,
                        pairingDeviceInfo
                    )
                }
            } else if (uiState.isSuccess == false) {
                if (uiState.isJoinedThreadNetworkFail == true) {
                    onNavigateJoinThreadNetworkFailScreen()
                } else if (uiState.isConnectWifiFail == true) {
                    onNavigateConnectWifiFailScreen(uiState.pairingError)
                }
            }
        }
    }

    SkyNetPingPongScreen()
}

@Composable
private fun SkyNetPingPongScreen() {

    val infiniteTransition = rememberInfiniteTransition(label = "infinite")

    val screenWidth = LocalConfiguration.current.screenWidthDp.toFloat()

    val offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = screenWidth,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )

    SkyNetScaffold(title = "PingPong") {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Canvas(modifier = Modifier.fillMaxWidth(), onDraw = {
                drawCircle(
                    color = Color.Red,
                    radius = 20f,
                    center = Offset(x = offset, y = 0f)
                )
            })
        }
    }
}

@Preview
@Composable
fun SkyNetPingPongScreenPreview() {
    SkyNetPingPongScreen()
}