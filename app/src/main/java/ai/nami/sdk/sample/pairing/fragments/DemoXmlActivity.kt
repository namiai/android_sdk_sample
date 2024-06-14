package ai.nami.sdk.sample.pairing.fragments

import ai.nami.sdk.pairing.customizeNamiPairingLayout
import ai.nami.sdk.pairing.registerNamiPairingEvent
import ai.nami.sdk.sample.R
import ai.nami.sdk.sample.pairing.standard.CustomPairingSuccessScreen
import ai.nami.sdk.sample.xzing.ZXingScanQRCode
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.activity.ComponentActivity
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
        setContentView(R.layout.activity_demo_xml)
    }
}