package ai.nami.demo.sdk.pairing.standard

import ai.nami.demo.sdk.pairing.shared.HostScreen
import ai.nami.demo.sdk.ui.theme.NamiSDKSampleTheme
import ai.nami.sdk.customizePairingLayout
import ai.nami.sdk.model.NamiSavedThreadNetworkInfo
import ai.nami.sdk.pairing.common.Utils
import ai.nami.sdk.registerNamiPairingEvent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
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
import kotlinx.coroutines.flow.firstOrNull
import org.json.JSONObject
import java.util.Base64


fun NamiSavedThreadNetworkInfo.toStringFormat(): String {
    val credentialsBase64String = Base64.getEncoder().encodeToString(credentialsDataset)

    val json = JSONObject()
    json.put("name", name)
    json.put("panId", panId)
    json.put("credentials", credentialsBase64String)

    return json.toString()
}

fun String.toNamiSavedThreadNetworkInfo(): NamiSavedThreadNetworkInfo {
    val json = JSONObject(this)
    val name = json.optString("name")
    val panId = json.optInt("panId")
    val credentials = json.optString("credentials")
    val decodeCredentials = Base64.getDecoder().decode(credentials)
    return NamiSavedThreadNetworkInfo(
        name = name,
        panId = panId.toInt(),
        credentialsDataset = decodeCredentials
    )
}

class StandardUIActivity: ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val namiLocalStorage = ai.nami.demo.common.NamiLocalStorage.getInstance(this)


        registerNamiPairingEvent {
            onConnectThreadNetworkSuccess { threadNetworkCredentials, key ->
                namiLocalStorage.saveThreadNetworkCredential(
                    key,
                    threadNetworkCredentials.toStringFormat()
                )
            }

            onGetSavedThreadCredentials { key1, key2, key3 ->
                val list = namiLocalStorage.listThreadCredentials.firstOrNull()?.toList()
                list?.firstOrNull { it.first == key2 }?.second?.toNamiSavedThreadNetworkInfo()
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
