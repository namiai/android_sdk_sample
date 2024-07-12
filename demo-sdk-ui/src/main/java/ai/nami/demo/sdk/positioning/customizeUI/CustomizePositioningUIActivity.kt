package ai.nami.demo.sdk.positioning.customizeUI

import ai.nami.demo.sdk.positioning.shared.StandardPositioningHostScreen
import ai.nami.demo.sdk.ui.theme.NamiSDKSampleTheme
import ai.nami.sdk.customizePositioningLayout
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier

class CustomizePositioningUIActivity: ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        customizePositioningLayout {

            namiScreenLayout { modifier, isShowLoading, topBar, body, buttons ->
                SkyNetScreenLayout(modifier, isShowLoading, topBar, body, buttons)
            }

            oneButtonScreenLayout { isShowLoading, topBar, primaryButton, body ->
                SkyNetOneButtonLayout(
                    topBar = topBar,
                    primaryButton = primaryButton,
                    isShowLoading = isShowLoading,
                    body = body
                )
            }

            twoButtonScreenLayout { isShowLoading, topBar, primaryButton, tertiaryButton, body ->
                SkyNetTwoButtonScreenLayout(
                    topBar = topBar,
                    primaryButton = primaryButton,
                    tertiaryButton = tertiaryButton,
                    body = body,
                    isShowLoading = isShowLoading
                )
            }

            primarySecondaryScreenLayout { topBar, primaryButton, secondaryButton, body ->
                SkyNetPrimarySecondaryScreenLayout(
                    topBar = topBar,
                    primaryButton = primaryButton,
                    secondaryButton = secondaryButton,
                    body = body
                )
            }

            successScreen { deviceName, onDone ->
SkyNetPositioningSuccessScreen(deviceName = deviceName, onDone)
            }
        }

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