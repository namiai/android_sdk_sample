package ai.nami.demo.sdk.pairing.cusomize

import ai.nami.sdk.NamiSDKUI
import ai.nami.sdk.designsystem.theme.NamiThemeData
import ai.nami.sdk.pairing.NamiPairingUI
import ai.nami.sdk.positioning.NamiPositioningUI
import ai.nami.demo.sdk.pairing.cusomize.theme.customNamiSDKColors
import ai.nami.demo.sdk.pairing.cusomize.theme.customNamiSDKShapes
import ai.nami.demo.sdk.pairing.cusomize.theme.customNamiSDKTypography
import ai.nami.demo.sdk.pairing.shared.HostScreen
import ai.nami.demo.sdk.ui.theme.NamiSDKSampleTheme
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

        NamiSDKUI.customTheme(
            NamiThemeData(
                shapes = customNamiSDKShapes,
                colors = customNamiSDKColors,
                typography = customNamiSDKTypography
            )
        )


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

    override fun onDestroy() {
        super.onDestroy()
        NamiSDKUI.customTheme(NamiThemeData())
    }
}