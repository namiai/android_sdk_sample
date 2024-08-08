package ai.nami.demo.sdk.pairing.standard

import ai.nami.demo.sdk.pairing.shared.HostScreen
import ai.nami.demo.sdk.ui.theme.NamiSDKSampleTheme
import ai.nami.sdk.customizePairingLayout
import ai.nami.sdk.pairing.NamiPairingSdk
import ai.nami.sdk.pairing.common.Utils
import ai.nami.sdk.pairing.model.PairingSavedWifiInfo
import ai.nami.sdk.registerNamiPairingEvent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject


fun String.toPairingSavedWifiInfo(): PairingSavedWifiInfo {
    val json = JSONObject(this)
    val password = json.optString("password")
    val ssid = json.optString("ssid")
    return PairingSavedWifiInfo(password = password, ssid = ssid)
}


class StandardUIActivity: ComponentActivity() {

    private lateinit var preferredCredentialsLauncher: ActivityResultLauncher<IntentSenderRequest>
    private lateinit var deferred: CompletableDeferred<ActivityResult>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val namiLocalStorage = ai.nami.demo.common.NamiLocalStorage.getInstance(this)

        lifecycleScope.launch {
            val listSavedWifiInfo = namiLocalStorage.listSavedWifiNetwork.firstOrNull()
                ?.map { it.toPairingSavedWifiInfo() } ?: emptyList()
            Log.e("debug_nami_sample", "listSavedWifiInfo: $listSavedWifiInfo")
            NamiPairingSdk.addPairingSavedWifiInfo(listSavedWifiInfo)
        }

        preferredCredentialsLauncher = registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()
        ) { result: ActivityResult ->
            deferred.complete(result)
        }

        registerNamiPairingEvent {
            onRequestJoinThreadNetwork { request ->
                deferred = CompletableDeferred()
                withContext(Dispatchers.Main) {
                    preferredCredentialsLauncher.launch(request)
                }

                deferred.await()
            }

            onConnectWifiNetworkSuccess { ssid, password, bssid, key ->
                namiLocalStorage.saveWifiNetwork(ssid = ssid, password = password)
                bssid?.let {
                    namiLocalStorage.saveBSSIDWithKey(bssid = bssid, key = key)
                }
            }

            onGetSavedBSSID { _, key2, _ ->
                namiLocalStorage.getBSSID(key = key2)
            }
        }

        customizePairingLayout {
            pairingSuccessScreen { productId, zoneName, deviceName, onPairAnotherDevice, onDonePairing, isWidar, isShowLoading ->
                CustomizePairingSuccessScreen(
                    productId,
                    zoneName,
                    deviceName,
                    onPairAnotherDevice,
                    onDonePairing,
                    isWidar,
                    isShowLoading
                )
            }
        }

        setContent {
            NamiSDKSampleTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    HostScreen()
                }
            }
        }
    }
}


@Composable
fun CustomizePairingSuccessScreen(
    productId: Int,
    zoneName: String,
    deviceName: String,
    onPairAnotherDevice: () -> Unit,
    onDonePairing: () -> Unit,
    isWidar: Boolean,
    isShowLoading: Boolean
) {
    LaunchedEffect(key1 = isShowLoading, key2 = isWidar) {
        if (!isShowLoading && isWidar) {
            onDonePairing()
        }
    }

    val isShowPairAnotherDevice by remember(isShowLoading, isWidar) {
        derivedStateOf { !isShowLoading && !isWidar }
    }

    val mainButtonText by remember(isShowLoading, isWidar) {
        derivedStateOf {
            if (isShowLoading || isWidar) {
                "Next"
            } else {
                "Done"
            }
        }
    }

    val deviceIconId = Utils.findDeviceIconBy(productId = productId)
    Column(
        Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(text = "Custom Pairing Success Screen")
        Spacer(modifier = Modifier.height(24.dp))
        Image(
            painter = painterResource(id = deviceIconId),
            contentDescription = "Device icon",
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .aspectRatio(1f),
        )
        Spacer(modifier = Modifier.height(48.dp))
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { onDonePairing() },
            enabled = !isShowLoading
        ) {
            Text(text = mainButtonText)
        }
        Spacer(modifier = Modifier.height(24.dp))
        AnimatedVisibility(visible = isShowPairAnotherDevice) {
            Button(modifier = Modifier.fillMaxWidth(), onClick = { onPairAnotherDevice() }) {
                Text(text = "Pair another device")
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
    }

    BackHandler(onBack = {
        onDonePairing()
    })
}
