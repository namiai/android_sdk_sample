package ai.nami.demo.coreSdk.pairing.fetchPairingInfo

import ai.nami.demo.core.sdk.R
import ai.nami.demo.coreSdk.common.SkyNetScaffold
import ai.nami.sdk.pairing.viewmodels.fetchpairingplace.FetchPairingPlaceViewIntent
import ai.nami.sdk.pairing.viewmodels.fetchpairingplace.FetchPairingPlaceViewModel
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
        ),
        JNavArg(
            name = "deviceCategory",
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

    SideEffect {
        Log.e("name_sample_app", "SkyNetFetchPairingInfoRoute uiState: $uiState")
    }

    val viewIntentChannel = remember {
        Channel<FetchPairingPlaceViewIntent>(Channel.UNLIMITED)
    }

    LaunchedEffect(key1 = Unit) {
        withContext(Dispatchers.Main.immediate) {
            viewIntentChannel.consumeAsFlow().onEach(viewModel::handleViewIntent).collect {}
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
            var message = if (uiState.pairingError != null) {
                uiState.pairingError?.errorMessage
                    ?: "Pairing Error with code ${uiState.pairingError?.code}"
            } else uiState.errorMessage
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
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "Fetching Pairing Information ...")
        }
    }
}