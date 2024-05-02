package ai.nami.sdk.sample.pairing.standard

import ai.nami.sdk.pairing.registerNamiPairingEvent
import ai.nami.sdk.sample.shared.HostScreen
import ai.nami.sdk.sample.pairing.ui.theme.NamiSDKSampleTheme
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier

class StandardUIActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        registerNamiPairingEvent {
            onConnectWifiNetworkSuccess{ssid, password, bssid, key ->

            }

            onFinishPairing { listPairedDeviceInfo ->

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