package ai.nami.demo.sdk.pairing.standard

import ai.nami.demo.sdk.pairing.shared.HostScreen
import ai.nami.demo.sdk.ui.theme.NamiSDKSampleTheme
import ai.nami.sdk.NamiSDK
import ai.nami.sdk.model.NamiSavedThreadNetworkInfo
import ai.nami.sdk.pairing.NamiPairingSdk
import ai.nami.sdk.pairing.model.PairingSavedWifiInfo
import ai.nami.sdk.registerNamiPairingEvent
import ai.nami.sdk.ui.BuildConfig
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import org.json.JSONObject

import java.util.Base64


fun NamiSavedThreadNetworkInfo.toStringFormat(): String {
    val credentialsBase64String = Base64.getEncoder().encodeToString(credentialsDataset)

    val json = JSONObject()
    json.put("name", name)
    json.put("panId", panId)
    json.put("credentials", credentialsBase64String)

    return json.toString().also {
        Log.e("debug_sample_nami", "NamiSavedThreadNetworkInfo.toStringFormat: $it")
    }
}

fun String.toNamiSavedThreadNetworkInfo(): NamiSavedThreadNetworkInfo {
    val json = JSONObject(this)
    val name = json.optString("name")
    val panId = json.optInt("panId")
    val credentials = json.optString("credentials")
    val decodeCredentials = Base64.getDecoder().decode(credentials)
    Log.e("debug_sample_nami", "String.toNamiSavedThreadNetworkInfo: name - $name ")
    Log.e("debug_sample_nami", "String.toNamiSavedThreadNetworkInfo:  panId - $panId")
    Log.e("debug_sample_nami", "String.toNamiSavedThreadNetworkInfo: credential -  $credentials")
    Log.e(
        "debug_sample_nami",
        "String.toNamiSavedThreadNetworkInfo: credentials - $decodeCredentials "
    )
    return NamiSavedThreadNetworkInfo(
        name = name,
        panId = panId.toInt(),
        credentialsDataset = decodeCredentials
    )
}

fun String.toPairingSavedWifiInfo(): PairingSavedWifiInfo {
    val json = JSONObject(this)
    val password = json.optString("password")
    val ssid = json.optString("ssid")
    return PairingSavedWifiInfo(password = password, ssid = ssid)
}


class StandardUIActivity: ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        NamiSDK.enableReleaseLog()

        val namiLocalStorage = ai.nami.demo.common.NamiLocalStorage.getInstance(this)

        lifecycleScope.launch {
            val listSavedWifiInfo = namiLocalStorage.listSavedWifiNetwork.firstOrNull()
                ?.map { it.toPairingSavedWifiInfo() } ?: emptyList()
            Log.e("debug_nami_sample", "listSavedWifiInfo: $listSavedWifiInfo")
            NamiPairingSdk.addPairingSavedWifiInfo(listSavedWifiInfo)
        }



        registerNamiPairingEvent {
            onConnectThreadNetworkSuccess { threadNetworkCredentials, key ->
                namiLocalStorage.saveThreadNetworkCredential(
                    key,
                    threadNetworkCredentials.toStringFormat()
                )
            }

            onGetSavedThreadCredentials { key1, key2, key3 ->
                val list = namiLocalStorage.listThreadCredentials.firstOrNull()?.toList().also {
                    Log.e("debug_nami_sample", "list ThreadCredentials $it")
                }
                list?.firstOrNull { it.first == key2 }?.second?.toNamiSavedThreadNetworkInfo()
                    ?.also {
                        Log.e("debug_nami_sample", "savedThreadCredentials $it")
                    }

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

