package ai.nami.sdk.sample

import ai.nami.sdk.pairing.registerNamiPairingEvent
import ai.nami.sdk.sample.data.NamiLocalStorage
import ai.nami.sdk.sample.pairing.cusomize.CustomizeUIActivity
import ai.nami.sdk.sample.pairing.standard.StandardUIActivity
import ai.nami.sdk.sample.positioning.customize.PositioningCustomizeActivity
import ai.nami.sdk.sample.positioning.standard.PositioningStandardActivity
import ai.nami.sdk.sample.ui.theme.NamiSDKSampleTheme
import ai.nami.sdk.sample.utils.formatDeviceUrn
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

class MainActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val namiLocalStorage = NamiLocalStorage.getInstance(this)

        registerNamiPairingEvent {
            onConnectWifiNetworkSuccess { ssid, password, bssid, key ->

            }

            onFinishPairing { listPairedDeviceInfo ->
                listPairedDeviceInfo.forEach { pairingDeviceInfo ->
                    val formattedUrn =
                        formatDeviceUrn(pairingDeviceInfo.deviceUrn, isLowerCase = true)
                    namiLocalStorage.saveDeviceUrn(formattedUrn)
                }
            }
        }




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
                        onOpenPositioningStandardUI = {
                            onOpenPositioningStandardUI()
                        },
                        onOpenPositioningCustomizeUI = {
                            onOpenPositioningCustomizeUI()
                        },
                        onClear = {
                            namiLocalStorage.clearListPairedDeviceUrn()
                        }
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

    private fun onOpenPositioningStandardUI() {
        val intent = Intent(this, PositioningStandardActivity::class.java)
        startActivity(intent)
    }

    private fun onOpenPositioningCustomizeUI() {
        val intent = Intent(this, PositioningCustomizeActivity::class.java)
        startActivity(intent)
    }

}

@Composable
fun MainScreen(
    onOpenPairingStandardUI: () -> Unit,
    onOpenPairingCustomizeUI: () -> Unit,
    onOpenPositioningStandardUI: () -> Unit,
    onOpenPositioningCustomizeUI: () -> Unit,
    onClear: suspend () -> Unit
) {

    var isClearingList by remember {
        mutableStateOf(false)
    }

    val coroutineScope = rememberCoroutineScope()
    val onClearList: () -> Unit = {
        coroutineScope.launch {
            isClearingList = true
            onClear()
            isClearingList = false
        }
    }

    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colors.background)
            .padding(24.dp)
            .verticalScroll(scrollState),
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
        Button(onClick = onOpenPositioningStandardUI) {
            Text("Standard UI Demo")
        }
        Spacer(modifier = Modifier.height(12.dp))
        Button(onClick = onOpenPositioningCustomizeUI) {
            Text("Customize UI Demo")
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "All urn of devices that are paired by this app will be saved in local (DataStore) for testing positioning purpose." +
                "Click the below button if you want to clear it.",
            style = MaterialTheme.typography.subtitle1
        )
        Spacer(modifier = Modifier.height(6.dp))
        Button(onClick = { onClearList() }, enabled = !isClearingList) {
            Text(text = if (isClearingList) "Clearing ..." else "Clear list of paired device's urn")
        }
        Spacer(modifier = Modifier.height(48.dp))
    }
}

@Preview
@Composable
fun MainScreenPreview() {
    NamiSDKSampleTheme {
        MainScreen(
            onOpenPairingStandardUI = { },
            onOpenPairingCustomizeUI = { },
            onOpenPositioningCustomizeUI = {},
            onOpenPositioningStandardUI = {},
            onClear = {}
        )
    }
}