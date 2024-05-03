package ai.nami.sdk.sample.data

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException


class NamiLocalStorage private constructor(private val context: Context) {

    companion object {
        private val namiPreferenceName = "demo_nami_sdk_sample_data_store_preference"

        val Context.dataStore by preferencesDataStore(
            name = namiPreferenceName
        )


        val LIST_PAIRED_DEVICES = stringSetPreferencesKey("demo_nami_sdk_list_paired_devices")

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


}