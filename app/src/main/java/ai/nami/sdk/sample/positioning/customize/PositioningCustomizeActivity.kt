package ai.nami.sdk.sample.positioning.customize

import ai.nami.sdk.NamiSDKUI
import ai.nami.sdk.designsystem.theme.NamiThemeData
import ai.nami.sdk.pairing.NamiPairingUI
import ai.nami.sdk.positioning.NamiPositioningUI
import ai.nami.sdk.sample.pairing.cusomize.theme.customNamiSDKTypography
import ai.nami.sdk.sample.positioning.shared.StandardPositioningHostScreen
import ai.nami.sdk.sample.ui.theme.NamiSDKSampleTheme
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier


class PositioningCustomizeActivity: ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NamiPairingUI.setup()
        NamiPositioningUI.setup()

        NamiSDKUI.customTheme(
            NamiThemeData(
                shapes = ai.nami.sdk.sample.pairing.cusomize.theme.customNamiSDKShapes,
                colors = ai.nami.sdk.sample.pairing.cusomize.theme.customNamiSDKColors,
                typography = customNamiSDKTypography
            )
        )

        setContent {
            NamiSDKSampleTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    StandardPositioningHostScreen()
                }
            }
        }
    }
}

