package ai.nami.demo_sdk_ui_extension

import ai.nami.demo_sdk_ui_extension.theme.DemoUIExtensionTheme
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController

class DemoUIExtensionActivity  : ComponentActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DemoUIExtensionTheme {
                MainNavHost(navController = rememberNavController())
            }
        }
    }
}