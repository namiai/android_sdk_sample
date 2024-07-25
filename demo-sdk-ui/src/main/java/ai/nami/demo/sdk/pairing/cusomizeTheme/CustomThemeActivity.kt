package ai.nami.demo.sdk.pairing.cusomizeTheme

import ai.nami.demo.sdk.pairing.cusomizeTheme.theme.customNamiSDKColors
import ai.nami.demo.sdk.pairing.cusomizeTheme.theme.customNamiSDKColorsDarkMode
import ai.nami.demo.sdk.pairing.cusomizeTheme.theme.customNamiSDKShapes
import ai.nami.demo.sdk.pairing.cusomizeTheme.theme.customNamiSDKTypography
import ai.nami.demo.sdk.pairing.shared.HostScreen
import ai.nami.demo.sdk.ui.theme.NamiSDKSampleTheme
import ai.nami.sdk.NamiSDKUI
import ai.nami.sdk.designsystem.theme.NamiThemeData
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier

class CustomThemeActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val darkModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK

        val isDarkMode = darkModeFlags == Configuration.UI_MODE_NIGHT_YES

        NamiSDKUI.customTheme(
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
        NamiSDKUI.customTheme(NamiThemeData())
    }
}