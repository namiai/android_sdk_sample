package ai.nami.sdk.sample.pairing.standard

import ai.nami.sdk.customizePairingLayout
import ai.nami.sdk.pairing.NamiPairingUI
import ai.nami.sdk.pairing.common.Utils
import ai.nami.sdk.positioning.NamiPositioningUI
import ai.nami.sdk.registerNamiPairingEvent
import ai.nami.sdk.sample.pairing.shared.HostScreen
import ai.nami.sdk.sample.ui.theme.NamiSDKSampleTheme
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class StandardUIActivity: ComponentActivity() {

    private lateinit var preferredCredentialsLauncher: ActivityResultLauncher<IntentSenderRequest>
    private lateinit var deferred: CompletableDeferred<ActivityResult>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        preferredCredentialsLauncher = registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()
        ) { result: ActivityResult ->
            deferred.complete(result)
        }

        registerNamiPairingEvent {
            onRequestJoinThreadNetwork { request ->
                deferred = CompletableDeferred()
                withContext(Dispatchers.Main) {
                    preferredCredentialsLauncher.launch(request)
                }

                deferred.await()
            }
        }

        // important: you have to call this function
        NamiPairingUI.setup()
        NamiPositioningUI.setup()

        customizePairingLayout {
            pairingSuccessScreen { productId, zoneName, deviceName, onPairAnotherDevice, onDonePairing, isWidar, isShowLoading ->
                CustomizePairingSuccessScreen(
                    productId, zoneName, deviceName, onPairAnotherDevice, onDonePairing, isWidar, isShowLoading
                )
            }
        }

        setContent {
            NamiSDKSampleTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    HostScreen()
                }
            }
        }
    }
}


@Composable
fun CustomizePairingSuccessScreen(
    productId: Int,
    zoneName: String,
    deviceName: String,
    onPairAnotherDevice: () -> Unit,
    onDonePairing: () -> Unit,
    isWidar: Boolean,
    isShowLoading: Boolean
) {
    LaunchedEffect(key1 = isShowLoading, key2 = isWidar) {
        if (!isShowLoading && isWidar) {
            onDonePairing()
        }
    }

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

    val deviceIconId = Utils.findDeviceIconBy(productId = productId)
    Column(
        Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(text = "Custom Pairing Success Screen")
        Spacer(modifier = Modifier.height(24.dp))
        Image(
            painter = painterResource(id = deviceIconId),
            contentDescription = "Device icon",
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .aspectRatio(1f),
        )
        Spacer(modifier = Modifier.height(48.dp))
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { onDonePairing() },
            enabled = !isShowLoading
        ) {
            Text(text = mainButtonText)
        }
        Spacer(modifier = Modifier.height(24.dp))
        AnimatedVisibility(visible = isShowPairAnotherDevice) {
            Button(modifier = Modifier.fillMaxWidth(), onClick = { onPairAnotherDevice() }) {
                Text(text = "Pair another device")
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
    }

    BackHandler(onBack = {
        onDonePairing()
    })
}
