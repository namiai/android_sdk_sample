package ai.nami.sdk.sample

import ai.nami.sdk.sample.androidview.AndroidViewActivity
import ai.nami.sdk.sample.cusomize.CustomizeUIActivity
import ai.nami.sdk.sample.standard.StandardUIActivity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import ai.nami.sdk.sample.ui.theme.NamiSDKSampleTheme

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp

class MainActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            NamiSDKSampleTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    MainScreen(
                        onOpenStandardUI = {
                            openStandardUI()
                        },
                        onOpenCustomizeUI = {
                            openCustomizeUI()
                        },
                        onOpenAndroidView = {
                            openAndroidView()
                        }
                    )
                }
            }
        }
    }

    private fun openStandardUI() {
        val intent = Intent(this, StandardUIActivity::class.java)
        startActivity(intent)
    }

    private fun openCustomizeUI() {
        val intent = Intent(this, CustomizeUIActivity::class.java)
        startActivity(intent)
    }

    private fun openAndroidView() {
        val intent = Intent(this, AndroidViewActivity::class.java)
        startActivity(intent)
    }
}

@Composable
fun MainScreen(
    onOpenStandardUI: () -> Unit,
    onOpenCustomizeUI: () -> Unit,
    onOpenAndroidView: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colors.background),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = onOpenStandardUI) {
            Text("Standard UI Demo")
        }
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onOpenCustomizeUI) {
            Text("Customize UI Demo")
        }
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onOpenAndroidView) {
            Text("Android View - XML Layout Demo")
        }
    }
}

@Preview
@Composable
fun MainScreenPreview() {
    NamiSDKSampleTheme {
        MainScreen(onOpenStandardUI = { /*TODO*/ }, onOpenCustomizeUI = { /*TODO*/ }) {

        }
    }
}