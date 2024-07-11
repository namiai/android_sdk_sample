package ai.nami.demo.sdk.positioning.customize

import ai.nami.demo.sdk.pairing.cusomizeTheme.theme.customNamiSDKColors
import ai.nami.demo.sdk.pairing.cusomizeTheme.theme.customNamiSDKShapes
import ai.nami.demo.sdk.pairing.cusomizeTheme.theme.customNamiSDKTypography
import ai.nami.sdk.NamiSDKUI
import ai.nami.sdk.designsystem.theme.NamiThemeData
import ai.nami.demo.sdk.positioning.shared.StandardPositioningHostScreen
import ai.nami.demo.sdk.ui.theme.NamiSDKSampleTheme
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

        NamiSDKUI.customTheme(
            NamiThemeData(
                shapes = customNamiSDKShapes,
                colors = customNamiSDKColors,
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

