package ai.nami.sdk.sample.pairing.fragments

import ai.nami.sdk.customizePairingLayout
import ai.nami.sdk.registerNamiPairingEvent
import ai.nami.sdk.sample.R
import ai.nami.sdk.sample.pairing.standard.CustomizePairingSuccessScreen
import ai.nami.sdk.sample.xzing.ZXingScanQRCode
import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DemoXmlActivity : AppCompatActivity()  {

    private lateinit var preferredCredentialsLauncher: ActivityResultLauncher<IntentSenderRequest>
    private lateinit var deferred: CompletableDeferred<ActivityResult>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        registerNamiPairingEvent {
            onRequestJoinThreadNetwork { request ->
                deferred = CompletableDeferred()
                withContext(Dispatchers.Main) {
                    preferredCredentialsLauncher.launch(request)
                }

                deferred.await()
            }
        }

        customizePairingLayout {
            pairingSuccessScreen { productId, zoneName, deviceName, onPairAnotherDevice, onDonePairing, isWidar, isShowLoading ->
                CustomizePairingSuccessScreen(
                    productId, zoneName, deviceName, onPairAnotherDevice, onDonePairing, isWidar, isShowLoading
                )
            }

            namiScanQRCodeCameraLayout { modifier, onScanQRCodeSuccess ->
                ZXingScanQRCode(onScanQRCodeSuccess = onScanQRCodeSuccess, modifier = modifier)
            }

        }
        setContentView(R.layout.activity_demo_xml)
    }
}