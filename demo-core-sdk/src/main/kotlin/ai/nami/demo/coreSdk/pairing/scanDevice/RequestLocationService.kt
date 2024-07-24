package ai.nami.demo.coreSdk.pairing.scanDevice


import android.app.Activity
import android.content.Context
import android.content.IntentSender
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.Priority
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.tasks.Task

@Composable
fun RequestLocationService(
    onEnabled: (Boolean) -> Unit
) {

    val settingResultRequest = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { activityResult ->
        if (activityResult.resultCode == Activity.RESULT_OK) {
            onEnabled(true)
        } else {
            onEnabled(false)
        }
    }

    LocalContext.current.checkLocationServiceFromGoogleLocationService(onEnabled = {
        onEnabled(true)
    }, onDisable = { resolvableApiException ->
        resolvableApiException?.let {
            try {
                val intentSenderRequest = IntentSenderRequest
                    .Builder(resolvableApiException.resolution)
                    .build()
                settingResultRequest.launch(intentSenderRequest)
            } catch (sendEx: IntentSender.SendIntentException) {
                onEnabled(false)
            }
        } ?: run {
            onEnabled(false)
        }
    })
}

private fun Context.checkLocationServiceFromGoogleLocationService(
    onEnabled: () -> Unit,
    onDisable: (ResolvableApiException?) -> Unit
) {

    val locationRequest = LocationRequest.Builder(
        Priority.PRIORITY_HIGH_ACCURACY, 1000
    ).build()

    val client: SettingsClient = LocationServices.getSettingsClient(this)
    val builder: LocationSettingsRequest.Builder = LocationSettingsRequest
        .Builder()
        .addLocationRequest(locationRequest)

    val gpsSettingTask: Task<LocationSettingsResponse> =
        client.checkLocationSettings(builder.build())

    gpsSettingTask.addOnSuccessListener {
        onEnabled()
    }.addOnFailureListener { exception ->
        if (exception is ResolvableApiException) {
            onDisable(exception)
        } else {
            onDisable(null)
        }
    }
}