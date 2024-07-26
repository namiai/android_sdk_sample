package ai.nami.sdk.sample.pairing.cusomize

import ai.nami.sdk.pairing.NamiPairingSdk
import ai.nami.sdk.pairing.ui.designsystem.theme.NamiThemeData
import ai.nami.sdk.sample.pairing.cusomize.theme.customNamiSDKColors
import ai.nami.sdk.sample.pairing.cusomize.theme.customNamiSDKColorsDarkMode
import ai.nami.sdk.sample.pairing.cusomize.theme.customNamiSDKShapes
import ai.nami.sdk.sample.pairing.cusomize.theme.customNamiSDKTypography
import ai.nami.sdk.sample.pairing.shared.HostScreen
import ai.nami.sdk.sample.ui.theme.NamiSDKSampleTheme
import android.content.res.Configuration
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
        val darkModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val isDarkMode = darkModeFlags == Configuration.UI_MODE_NIGHT_YES

        NamiPairingSdk.customizeTheme(
            NamiThemeData(
                shapes = customNamiSDKShapes,
                colors = if (isDarkMode) customNamiSDKColorsDarkMode else customNamiSDKColors,
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
        NamiPairingSdk.customizeTheme(NamiThemeData())
    }
}