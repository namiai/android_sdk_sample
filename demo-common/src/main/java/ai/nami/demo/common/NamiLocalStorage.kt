package ai.nami.demo.common

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import org.json.JSONObject
import java.io.IOException
import java.util.Base64


class NamiLocalStorage private constructor(private val context: Context) {

    companion object {
        private val namiPreferenceName = "nami_sdk_sample_data_store_preference"

        val Context.dataStore by preferencesDataStore(
            name = namiPreferenceName
        )


        val LIST_PAIRED_DEVICES = stringSetPreferencesKey("demo_nami_sdk_list_paired_devices")

        val LIST_THREAD_NETWORK_CREDENTIALS =
            stringSetPreferencesKey("demo_nami_sdk_list_thread_network_credentials")

        val LIST_SAVED_WIFI_NETWORK =
            stringSetPreferencesKey("demo_nami_sdk_list_saved_wifi_network")
        val LIST_BSSID_WIFI_NETWORK =
            stringSetPreferencesKey("demo_nami_sdk_list_bssid_wifi_network")


        @SuppressLint("StaticFieldLeak")
        // just for demo purpose
        var instance: NamiLocalStorage? = null
            private set

        fun getInstance(context: Context): NamiLocalStorage {

            if (instance == null) {
                Log.e("nami-widar-sample", "create new instance for namilocalstorage")
                instance = NamiLocalStorage(context.applicationContext)
            }
            return instance!!
        }
    }

    suspend fun clearListPairedDeviceUrn() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    suspend fun saveDeviceUrn(deviceUrn: String) {
        context.dataStore.edit { preferences ->
            val currentSet = preferences[LIST_PAIRED_DEVICES]?.toMutableSet() ?: mutableSetOf()
            if (!currentSet.contains(deviceUrn)) {
                currentSet.add(deviceUrn)
                preferences[LIST_PAIRED_DEVICES] = currentSet.toSet()
            }
        }
    }

    val listPairedDeviceUrn: Flow<Set<String>> = context.dataStore.data.catch { exception ->
        if (exception is IOException) {
            emit(emptyPreferences())
        } else {
            throw exception
        }
    }.map { preferences ->
        preferences[LIST_PAIRED_DEVICES] ?: emptySet()
    }


    suspend fun saveThreadNetworkCredential(key: Int, credentials: String) {
        context.dataStore.edit { preferences ->
            val json = JSONObject()
            json.put("key", key)
            json.put("data", credentials)
            val currentSet =
                preferences[LIST_THREAD_NETWORK_CREDENTIALS]?.toMutableSet() ?: mutableSetOf()
            currentSet.add(json.toString())
            preferences[LIST_THREAD_NETWORK_CREDENTIALS] = currentSet.toSet()
        }
    }

    val listThreadCredentials: Flow<Set<Pair<Int, String>>> =
        context.dataStore.data.catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            val data = preferences[LIST_THREAD_NETWORK_CREDENTIALS] ?: emptySet()
            data.map {
                val json = JSONObject(it)
                val key = json.optInt("key")
                val credentials = json.optString("data")
                Log.e("debug_sample_nami", "NamiLocalStorage key: $key")
                Pair(key, credentials)
            }.toSet()
        }

    suspend fun saveWifiNetwork(ssid: String, password: String) {
        val json = JSONObject()
        json.put("ssid", ssid)
        json.put("password", password)
        val wifiInfo = json.toString()
        context.dataStore.edit { preferences ->
            val currentSet = preferences[LIST_SAVED_WIFI_NETWORK]?.toMutableSet() ?: mutableSetOf()
            if (!currentSet.contains(wifiInfo)) {
                currentSet.add(wifiInfo)
                preferences[LIST_SAVED_WIFI_NETWORK] = currentSet.toSet()
            }
        }
    }

    val listSavedWifiNetwork: Flow<Set<String>> = context.dataStore.data.catch { exception ->
        if (exception is IOException) {
            emit(emptyPreferences())
        } else {
            throw exception
        }
    }.map { preferences ->
        preferences[LIST_SAVED_WIFI_NETWORK] ?: emptySet()
    }

    suspend fun saveBSSIDWithKey(bssid: ByteArray, key: Int) {
        val bssidInString = Base64.getEncoder().encodeToString(bssid)
        val jsonObject = JSONObject()
        jsonObject.put("bssid", bssidInString)
        jsonObject.put("key", key)
        val data = jsonObject.toString()
        context.dataStore.edit { preferences ->
            val currentSet = preferences[LIST_BSSID_WIFI_NETWORK]?.toMutableSet() ?: mutableSetOf()
            if (!currentSet.contains(data)) {
                currentSet.add(data)
                preferences[LIST_BSSID_WIFI_NETWORK] = currentSet.toSet()
            }
        }
    }

    suspend fun getBSSID(key: Int): ByteArray? {
        val list = context.dataStore.data.catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            preferences[LIST_BSSID_WIFI_NETWORK] ?: emptySet()
        }.firstOrNull()?.map {
            Log.e("debug_nami_sample", "bssid-key: $it")
            val json = JSONObject(it)
            val bssidInString = json.optString("bssid")
            val savedKey = json.optInt("key")
            val bssid = Base64.getDecoder().decode(bssidInString)
            Pair(savedKey, bssid)
        }

        return list?.firstOrNull { it.first == key }?.second

    }

}


