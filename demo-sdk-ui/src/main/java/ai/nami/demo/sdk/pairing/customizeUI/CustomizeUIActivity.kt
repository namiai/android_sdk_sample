package ai.nami.demo.sdk.pairing.customizeUI

import ai.nami.demo.sdk.pairing.shared.HostScreen
import ai.nami.demo.sdk.pairing.standard.CustomizePairingSuccessScreen
import ai.nami.demo.sdk.ui.theme.NamiSDKSampleTheme
import ai.nami.sdk.customizePairingLayout
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier

class CustomizeUIActivity: ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        customizePairingLayout {
            basePairingScreen { modifier, onBack, isShowToolbar, title, showBackConfirmation, defaultPadding, body ->
                SkyNetBasePairingScreen(
                    modifier = modifier,
                    onBack = onBack,
                    isShowToolbar = isShowToolbar,
                    title = title,
                    showBackConfirmation = showBackConfirmation,
                    defaultPadding = defaultPadding,
                    body = body
                )
            }

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