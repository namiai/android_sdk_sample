package ai.nami.sdk.sample.pairing.standard

import ai.nami.sdk.pairing.registerNamiPairingEvent
import ai.nami.sdk.sample.shared.HostScreen
import ai.nami.sdk.sample.pairing.ui.theme.NamiSDKSampleTheme
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class StandardUIActivity: ComponentActivity() {

    private lateinit var preferredCredentialsLauncher: ActivityResultLauncher<IntentSenderRequest>
    private lateinit var deferred: CompletableDeferred<ActivityResult>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        registerNamiPairingEvent {
            onConnectWifiNetworkSuccess{ssid, password, bssid, key ->

            }

            onFinishPairing { listPairedDeviceInfo ->

            }

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