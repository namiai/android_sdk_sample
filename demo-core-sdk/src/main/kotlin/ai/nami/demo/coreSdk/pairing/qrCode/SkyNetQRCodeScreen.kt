package ai.nami.demo.coreSdk.pairing.qrCode


import ai.nami.demo.coreSdk.common.SkyNetButton
import ai.nami.demo.coreSdk.common.SkyNetScaffold
import ai.nami.sdk.common.DEFAULT_PLACE_ID
import ai.nami.sdk.common.DEFAULT_ROOM_ID
import ai.nami.sdk.common.DEFAULT_ZONE_ID
import ai.nami.sdk.common.DEFAULT_ZONE_NAME
import ai.nami.sdk.model.DeviceCategory
import ai.nami.sdk.pairing.viewmodels.scanqrcode.ScanQRCodeViewIntent
import ai.nami.sdk.pairing.viewmodels.scanqrcode.ScanQRCodeViewModel
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
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
    baseRoute = "sky_net_qr_code_route",
    destination = "sky_net_qr_code_destination",
    name = "SkyNetQRCodeNavigation",
    arguments = [
        JNavArg(
            name = "deviceCategory",
            type = String::class,
            isNullable = true
        ),
        JNavArg(
            name = "placeId",
            type = Int::class,
            isNullable = false,
        ),
        JNavArg(
            name = "zoneId",
            type = Int::class,
            isNullable = false
        ),
        JNavArg(
            name = "roomId",
            type = Int::class,
            isNullable = false
        ),
        JNavArg(
            name = "zoneName",
            type = String::class,
            isNullable = false
        ),
    ]
)

@Composable
fun SkyNetQRCodeRoute(
    viewModel: ScanQRCodeViewModel,
    onNext: () -> Unit,
    onBack: () -> Unit,
    deviceCategory: DeviceCategory,
    placeId: Int,
    zoneId: Int,
    roomId: Int,
    zoneName: String
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val viewIntentChannel = remember {
        Channel<ScanQRCodeViewIntent>(Channel.UNLIMITED)
    }

    LaunchedEffect(key1 = Unit) {
        withContext(Dispatchers.Main.immediate) {
            viewIntentChannel.consumeAsFlow().onEach(viewModel::handleViewIntent).collect()
        }
    }

    val sendViewIntent: (ScanQRCodeViewIntent) -> Unit = remember {
        { viewIntent -> viewIntentChannel.trySend(viewIntent) }
    }

    LaunchedEffect(key1 = uiState.isValidateQRCodeSuccess) {
        if (uiState.isValidateQRCodeSuccess == true) {
            onNext()
        }
    }

    LaunchedEffect(key1 = uiState.isCanceledPairing) {
        if (uiState.isCanceledPairing) {
            onBack()
        }
    }

    val isShowLoading by remember(uiState.isLoading) {
        derivedStateOf { uiState.isLoading }
    }

    val errorMessage by remember(uiState.pairingError, uiState.exception) {
        derivedStateOf {
            if (uiState.pairingError != null) {
                uiState.pairingError?.errorMessage
                    ?: "Pairing Error with code: ${uiState.pairingError?.code}"
            } else {
                uiState.exception?.message
            }
        }
    }

    SkyNetQRCodeScreen(
        isShowLoading = isShowLoading,
        errorMessage = errorMessage,
        onValidateQRCode = { qrCode ->
            sendViewIntent(
                ScanQRCodeViewIntent.ValidateQRCode(
                    qrCodeString = qrCode,
                    roomId = roomId,
                    placeId = placeId,
                    zoneId = zoneId,
                    zoneName = zoneName,
                    deviceCategory = deviceCategory
                )
            )
        },
        onBack = {
            sendViewIntent(ScanQRCodeViewIntent.CancelPairing)
        }
    )

}


@Composable
private fun SkyNetQRCodeScreen(
    isShowLoading: Boolean,
    errorMessage: String?,
    onValidateQRCode: (qrCode: String) -> Unit,
    onBack: () -> Unit
) {
    var qrCode by remember {
        mutableStateOf("")
    }

    val enableButton by remember(qrCode) {
        derivedStateOf { qrCode.isNotEmpty() }
    }

    SkyNetScaffold(
        errorMessage = errorMessage,
        title = "QRCode",
        isLoading = isShowLoading,
        onBack = onBack
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        Text(
            text = "Enter device's QR Code",
            style = MaterialTheme.typography.subtitle1.copy(
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(
            value = qrCode,
            onValueChange = { qrCode = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(text = "NAMI:300:128182410")
            }
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = "You can use an application to scan the device's QRCode, then copy its value and paste it in here",
            style = MaterialTheme.typography.caption
        )
        Spacer(modifier = Modifier.height(24.dp))
        SkyNetButton(
            modifier = Modifier.fillMaxWidth(),
            text = "Next",
            onClick = {
                onValidateQRCode(qrCode)
            },
            enabled = enableButton,
        )
    }

}

@Preview
@Composable
private fun SkyNetQRCodeScreenPreview() {
    SkyNetQRCodeScreen(
        isShowLoading = false,
        errorMessage = "test error message",
        onValidateQRCode = {}
    ) {

    }
}

