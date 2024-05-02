package ai.nami.sdk.sample.cusomize

import ai.nami.sdk.pairing.NamiPairingSdk
import ai.nami.sdk.pairing.ui.designsystem.theme.NamiThemeData
import ai.nami.sdk.sample.cusomize.theme.customNamiSDKColors
import ai.nami.sdk.sample.cusomize.theme.customNamiSDKShapes
import ai.nami.sdk.sample.shared.HostScreen
import ai.nami.sdk.sample.ui.theme.NamiSDKSampleTheme
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

        NamiPairingSdk.customizeTheme(
            NamiThemeData(
                shapes = customNamiSDKShapes,
                colors = customNamiSDKColors
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
        NamiPairingSdk.customizeTheme(NamiThemeData())
    }
}