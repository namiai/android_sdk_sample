package ai.nami.demo.coreSdk.positioning.recommendations

import ai.nami.demo.coreSdk.common.SkyNetButton
import ai.nami.demo.coreSdk.common.SkyNetScaffold
import ai.nami.sdk.positioning.model.PositioningErrorCode
import ai.nami.sdk.positioning.viewmodels.positioning.WidarPositionIntentView
import ai.nami.sdk.positioning.viewmodels.recommendation.RecommendationIntentView
import ai.nami.sdk.positioning.viewmodels.recommendation.WidarRecommendationsViewModel
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
    baseRoute = "sky_net_widar_recommendation_route",
    destination = "sky_net_widar_recommendation_destination",
    name = "SkyNetWidarRecommendationNavigation",
    arguments = [
        JNavArg(
            name = "deviceUrn",
            type = String::class
        ),
        JNavArg(
            name = "deviceHost",
            type = String::class,
            isNullable = true
        ),
        JNavArg(
            name = "devicePort",
            type = Int::class
        ),
        JNavArg(
            name = "placeId",
            type = Int::class
        ),
    ]
)
@Composable
fun SkyNetWidarRecommendationRoute(
    deviceUrn: String,
    deviceHost: String?,
    devicePort: Int?,
    placeId: Int,
    onBack: () -> Unit,
    onNavigatePositioningScreen: () -> Unit,
    onNavigateErrorScreen: (PositioningErrorCode) -> Unit,
    viewModel: WidarRecommendationsViewModel
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val viewIntentChannel = remember {
        Channel<RecommendationIntentView>(Channel.UNLIMITED)
    }

    LaunchedEffect(key1 = Unit) {
        withContext(Dispatchers.Main.immediate) {
            viewIntentChannel.consumeAsFlow().onEach(viewModel::handleViewIntent).collect()
        }
    }

    val sendViewIntent: (RecommendationIntentView) -> Unit = remember {
        { viewIntent -> viewIntentChannel.trySend(viewIntent) }
    }

    LaunchedEffect(key1 = uiState.error, key2 = uiState.isStartSuccess) {

        if (uiState.hasError() || uiState.isStartSuccess == false) {
            onNavigateErrorScreen(uiState.positioningErrorCode)
        } else if (uiState.isStartSuccess == true) {
            onNavigatePositioningScreen()
        }
    }

    val isLoading by remember(uiState.isLoading) {
        derivedStateOf { uiState.isLoading }
    }

    SkyNetWidarRecommendationScreen(
        onStartPositioning = {
            // it seems a mistake of JNav library
            val host = if ( deviceHost == "null") null else deviceHost
            sendViewIntent(
                RecommendationIntentView.StartPositioningIntentView(
                    urn = deviceUrn, host = host, port = devicePort, placeId = placeId
                )
            )
        },
        onBack = { onBack() },
        isLoading = isLoading
    )
}

@Composable
private fun SkyNetWidarRecommendationScreen(
    onStartPositioning: () -> Unit,
    onBack: () -> Unit,
    isLoading: Boolean
) {
    SkyNetScaffold(
        title = "Position",
        isLoading = isLoading,
        onBack = onBack
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        Text(
            text = "How to position your device", modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.h5.copy(textAlign = TextAlign.Center)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(text = "Attach the base if device will be placed flat on a table")
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = "Place device in a corner of the room with the wire at the back")
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = "Keep the area in front of the device clear")
        Spacer(modifier = Modifier.height(12.dp))
        Spacer(modifier = Modifier.height(48.dp))
        SkyNetButton(
            text = "Start Positioning",
            onClick = onStartPositioning,
            enabled = !isLoading
        )
    }
}


@Preview
@Composable
fun SkyNetWidarRecommendationScreenPreview() {
    SkyNetWidarRecommendationScreen(
        onStartPositioning = { /*TODO*/ },
        onBack = { /*TODO*/ },
        isLoading = false
    )
}