package ai.nami.sdk.sample.pairing.standard

import ai.nami.sdk.pairing.common.Utils
import ai.nami.sdk.pairing.customizeNamiPairingLayout
import ai.nami.sdk.pairing.registerNamiPairingEvent
import ai.nami.sdk.sample.pairing.shared.HostScreen
import ai.nami.sdk.sample.ui.theme.NamiSDKSampleTheme
import ai.nami.sdk.sample.xzing.ZXingScanQRCode
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
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


        registerNamiPairingEvent {


            onRequestJoinThreadNetwork { request ->
                Log.e("TAG", "StandardUIActivity registerOnRequestJoinThreadNetwork onRequest ")
                deferred = CompletableDeferred()
                withContext(Dispatchers.Main) {
                    Log.e(
                        "TAG",
                        "StandardUIActivity registerOnRequestJoinThreadNetwork launch request "
                    )
                    preferredCredentialsLauncher.launch(request)
                }

                deferred.await()
            }
        }

        customizeNamiPairingLayout {
            pairingSuccessLayout { productId: Int, deviceName: String, zoneName: String,
                                   onPairAnotherDevice: () -> Unit,
                                   onDonePairing: (extraData: Map<String, String>?) -> Unit,
                                   id: Long, isWidarDevice: Boolean, isShowLoading: Boolean ->
                CustomPairingSuccessScreen(
                    productId,
                    deviceName,
                    zoneName,
                    onPairAnotherDevice,
                    onDonePairing,
                    id,
                    isWidarDevice,
                    isShowLoading
                )
            }

            namiScanQRCodeLayout { modifier, onScanQRCodeSuccess ->
                ZXingScanQRCode(onScanQRCodeSuccess = onScanQRCodeSuccess, modifier = modifier)
            }
        }

        setContent {
            NamiSDKSampleTheme {
                // A surface container using the 'background' color from the theme
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
fun CustomPairingSuccessScreen(
    productId: Int, deviceName: String, zoneName: String,
    onPairAnotherDevice: () -> Unit,
    onDonePairing: (extraData: Map<String, String>?) -> Unit,
    id: Long, isWidarDevice: Boolean,
    isShowLoading: Boolean
) {

    LaunchedEffect(key1 = isShowLoading, key2 = isWidarDevice) {
        if (!isShowLoading && isWidarDevice) {
            onDonePairing(null)
        }
    }

    val isShowPairAnotherDevice by remember(isShowLoading, isWidarDevice) {
        derivedStateOf { !isShowLoading && !isWidarDevice }
    }

    val mainButtonText by remember(isShowLoading, isWidarDevice) {
        derivedStateOf {
            if (isShowLoading || isWidarDevice) {
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
            onClick = { onDonePairing(null) },
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
        onDonePairing(null)
    })
}