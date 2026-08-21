package ai.nami.demo_sdk_ui_extension

import ai.nami.demo_sdk_ui_extension.theme.DemoUIExtensionTheme
import ai.nami.sdk.NamiSDK
import ai.nami.sdk.registerNamiSDKEvent
import ai.nami.shared_sample.NamiLocalStorage
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import ai.nami.shared_sample.MainNavHost

class DemoUIExtensionActivity  : ComponentActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        NamiSDK.enableReleaseLog()
        registerNamiSDKEvent {
            onAccessTokenChanged { accessToken, refreshToken, expiresAt ->
                Log.e( "debug-session", "accessToken $accessToken refreshToken $refreshToken expiresAt: $expiresAt")
                NamiLocalStorage.getInstance(applicationContext) .saveCustomerAccessToken(
                    accessToken = accessToken,
                    refreshToken = refreshToken,
                    expiresAt = expiresAt
                )
            }
        }
        setContent {
            DemoUIExtensionTheme {
                MainNavHost(navController = rememberNavController())
            }
        }
    }
}