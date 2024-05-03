package ai.nami.sdk.sample.positioning.standard

import ai.nami.sdk.sample.positioning.shared.StandardPositioningHostScreen
import ai.nami.sdk.sample.ui.theme.NamiSDKSampleTheme
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier

class PositioningStandardActivity: ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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