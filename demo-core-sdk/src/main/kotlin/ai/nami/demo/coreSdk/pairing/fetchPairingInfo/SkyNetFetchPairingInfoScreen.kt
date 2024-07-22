package ai.nami.demo.coreSdk.pairing.fetchPairingInfo

import ai.nami.demo.core.sdk.R
import ai.nami.demo.coreSdk.common.SkyNetScaffold
import ai.nami.sdk.pairing.viewmodels.fetchpairingplace.FetchPairingPlaceViewIntent
import ai.nami.sdk.pairing.viewmodels.fetchpairingplace.FetchPairingPlaceViewModel
import ai.nami.sdk.pairing.viewmodels.fetchpairingplace.FetchPairingPlaceViewModelImpl
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fatherofapps.jnav.annotations.JNav
import com.fatherofapps.jnav.annotations.JNavArg
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext

@JNav(
    destination = "sky_net_fetch_pairing_info_destination",
    baseRoute = "sky_net_fetch_pairing_info_route",
    name = "SkyNetFetchPairingInfoNavigation",
    arguments = [
        JNavArg(
            name = "sessionCode",
            type = String::class,
            isNullable = false
        ),
        JNavArg(
            name = "roomId",
            type = String::class,
            isNullable = false
        )
    ]
)
@Composable
fun SkyNetFetchPairingInfoRoute(
    sessionCode: String,
    roomId: String,
    viewModel: FetchPairingPlaceViewModel,
    onNext: () -> Unit,
    onBack: () -> Unit
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val viewIntentChannel = remember {
        Channel<FetchPairingPlaceViewIntent>(Channel.UNLIMITED)
    }

    LaunchedEffect(key1 = Unit) {
        withContext(Dispatchers.Main.immediate) {
            viewIntentChannel.consumeAsFlow().onEach(viewModel::handleViewIntent).collect{}
        }
    }

    val sendViewIntent: (FetchPairingPlaceViewIntent) -> Unit = remember {
        { viewIntent -> viewIntentChannel.trySend(viewIntent) }
    }



    LaunchedEffect(key1 = sessionCode, key2 = roomId) {

        sendViewIntent(
            FetchPairingPlaceViewIntent.Fetch(
                sessionCode = sessionCode,
                roomId = roomId
            )
        )

    }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess == true) {
            onNext()
        }
    }


    val defaultErrorMessage = stringResource(id = R.string.something_went_wrong)
    val errorMessage by remember(uiState.errorMessage, uiState.pairingError, uiState.isSuccess) {
        derivedStateOf {
            var message = uiState.pairingError?.errorMessage ?: uiState.errorMessage
            if (uiState.isSuccess == false && message == null || message == "") {
                message = defaultErrorMessage
            }
            message
        }
    }

    val isLoading by remember(uiState.isLoading) {
        derivedStateOf { uiState.isLoading }
    }

    SkyNetFetchPairingInfoScreen(isLoading = isLoading, errorMessage = errorMessage, onBack = {
        if (!uiState.isLoading && uiState.isSuccess != true) {
            onBack()
        }
    })

}


@Composable
fun SkyNetFetchPairingInfoScreen(
    isLoading: Boolean,
    errorMessage: String?,
    onBack: () -> Unit
) {
    SkyNetScaffold(
        errorMessage = errorMessage,
        title = "Fetch Pairing Info",
        isLoading = isLoading,
        onBack = onBack
    ) {

    }
}