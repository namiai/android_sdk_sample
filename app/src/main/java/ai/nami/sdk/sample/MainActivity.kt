package ai.nami.sdk.sample

import ai.nami.demo_sdk_ui_extension.DemoUIExtensionActivity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class MainActivity: ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(color = MaterialTheme.colors.background)
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.Start
                    ) {
//                        Spacer(modifier = Modifier.height(48.dp))
//                        Button(onClick = {
//                            openDemoUIActivity(DemoUIMainActivity::class.java)
//                        }) {
//                            Text("Demo SDK-UI")
//                        }
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(onClick = {
                            openDemoUIActivity(DemoUIExtensionActivity::class.java)
                        }) {
                            Text("Demo SDK-UI Extension")
                        }
//                        Spacer(modifier = Modifier.height(24.dp))
//                        Button(onClick = {
//                            openDemoUIActivity(DemoSdkFragmentActivity::class.java)
//                        }) {
//                            Text("Demo SDK with Fragment")
//                        }

//                        Spacer(modifier = Modifier.height(24.dp))
//                        Button(onClick = {
//                            openDemoUIActivity(DemoCoreSDKActivity::class.java)
//                        }) {
//                            Text("Demo Core-SDK")
//                        }
                    }
                }
            }
        }
    }

    private fun openDemoUIActivity(cl: Class<*>) {
        val intent = Intent(this, cl)
        startActivity(intent)
    }

}