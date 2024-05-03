package ai.nami.sdk.sample

import ai.nami.sdk.sample.pairing.cusomize.CustomizeUIActivity
import ai.nami.sdk.sample.pairing.standard.StandardUIActivity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import ai.nami.sdk.sample.pairing.ui.theme.NamiSDKSampleTheme

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Divider
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
                        onOpenPairingStandardUI = {
                            onOpenPairingStandardUI()
                        },
                        onOpenPairingCustomizeUI = {
                            onOpenPairingCustomizeUI()
                        },
                    )
                }
            }
        }
    }

    private fun onOpenPairingStandardUI() {
        val intent = Intent(this, StandardUIActivity::class.java)
        startActivity(intent)
    }

    private fun onOpenPairingCustomizeUI() {
        val intent = Intent(this, CustomizeUIActivity::class.java)
        startActivity(intent)
    }

}

@Composable
fun MainScreen(
    onOpenPairingStandardUI: () -> Unit,
    onOpenPairingCustomizeUI: () -> Unit,
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colors.background)
            .padding(24.dp).verticalScroll(scrollState),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        Text(text = "Pairing", style = MaterialTheme.typography.h5)
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onOpenPairingStandardUI) {
            Text("Standard UI Demo")
        }
        Spacer(modifier = Modifier.height(12.dp))
        Button(onClick = onOpenPairingCustomizeUI) {
            Text("Customize UI Demo")
        }
        Spacer(modifier = Modifier.height(48.dp))
        Divider()
        Spacer(modifier = Modifier.height(48.dp))
        Text(text = "Positioning", style = MaterialTheme.typography.h5)
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onOpenPairingStandardUI) {
            Text("Standard UI Demo")
        }
        Spacer(modifier = Modifier.height(12.dp))
        Button(onClick = onOpenPairingCustomizeUI) {
            Text("Customize UI Demo")
        }
        Spacer(modifier = Modifier.height(48.dp))
    }
}

@Preview
@Composable
fun MainScreenPreview() {
    NamiSDKSampleTheme {
        MainScreen(onOpenPairingStandardUI = { /*TODO*/ }, onOpenPairingCustomizeUI = { /*TODO*/ })
    }
}