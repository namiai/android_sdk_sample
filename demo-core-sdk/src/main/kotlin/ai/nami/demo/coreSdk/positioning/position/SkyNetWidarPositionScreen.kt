package ai.nami.demo.coreSdk.positioning.position

import ai.nami.demo.coreSdk.common.SkyNetButton
import ai.nami.demo.coreSdk.common.SkyNetScaffold
import ai.nami.sdk.positioning.model.NamiSignalQuality
import ai.nami.sdk.positioning.model.PositioningErrorCode
import ai.nami.sdk.positioning.viewmodels.positioning.WidarPositionIntentView
import ai.nami.sdk.positioning.viewmodels.positioning.WidarPositionViewModel
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.collect

@JNav(
    baseRoute = "sky_net_widar_position_route",
    destination = "sky_net_widar_position_destination",
    name = "SkyNetWidarPositionNavigation",
)
@Composable
fun SkyNetWidarPositionRoute(
    viewModel: WidarPositionViewModel,
    onExitPositioning: () -> Unit,
    onNavigatePositionSuccessScreen: () -> Unit,
    onNavigateErrorScreen: (PositioningErrorCode) -> Unit,
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val viewIntentChannel = remember {
        Channel<WidarPositionIntentView>(Channel.UNLIMITED)
    }

    LaunchedEffect(key1 = Unit) {
        withContext(Dispatchers.Main.immediate) {
            viewIntentChannel.consumeAsFlow().onEach(viewModel::handleViewIntent).collect()
        }
    }

    val sendViewIntent: (WidarPositionIntentView) -> Unit = remember {
        { viewIntent -> viewIntentChannel.trySend(viewIntent) }
    }

    LaunchedEffect(key1 = Unit) {
        sendViewIntent(
            WidarPositionIntentView.Init
        )
    }

    val signalQuality by remember(uiState.positionQuality) {
        derivedStateOf { uiState.positionQuality }
    }

    LaunchedEffect(key1 = uiState.isCompletedPosition) {
        if (uiState.isCompletedPosition) {
            onNavigatePositionSuccessScreen()
        }
    }

    LaunchedEffect(key1 = uiState.isCanceled) {
        if (uiState.isCanceled) {
            onExitPositioning()
        }
    }

    val isLoading by remember(uiState.isLoading) {
        derivedStateOf { uiState.isLoading }
    }

    LaunchedEffect(key1 = uiState.errorCode) {
        if (uiState.errorCode != null) {
            onNavigateErrorScreen(uiState.errorCode!!)
        }
    }

    SkyNetWidarPositionScreen(
        isLoading = isLoading,
        onCancelPosition = {
            sendViewIntent(
                WidarPositionIntentView.CancelPositioning
            )
        },
        onCompletePositioning = {
            sendViewIntent(
                WidarPositionIntentView.CompletePositioning
            )
        },
        signalQuality = signalQuality
    )
}

private fun NamiSignalQuality.toText(): String {
    return when (this) {
        NamiSignalQuality.GOOD -> "Optimized"
        NamiSignalQuality.POOR -> "Mispositioned"
        NamiSignalQuality.DEGRADED -> "Getting better"
        else -> "Checking"
    }
}

@Composable
private fun SkyNetWidarPositionScreen(
    isLoading: Boolean,
    onCancelPosition: () -> Unit,
    onCompletePositioning: () -> Unit,
    signalQuality: NamiSignalQuality
) {
    val isOptimal by remember(signalQuality) {
        derivedStateOf { signalQuality == NamiSignalQuality.GOOD }
    }

    SkyNetScaffold(
        title = "Position",
        onBack = onCancelPosition,
        isLoading = isLoading
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        Text(text = "Status: ${signalQuality.toText()}")
        Spacer(modifier = Modifier.height(48.dp))
        SkyNetButton(text = "Complete", onClick = onCompletePositioning, enabled = isOptimal)
        Spacer(modifier = Modifier.height(24.dp))
        SkyNetButton(text = "Cancel", onClick = onCancelPosition, enabled = !isOptimal)
    }
}