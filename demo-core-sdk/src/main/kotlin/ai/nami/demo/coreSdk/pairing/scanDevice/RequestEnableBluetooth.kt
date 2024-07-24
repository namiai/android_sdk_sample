package ai.nami.demo.coreSdk.pairing.scanDevice

import ai.nami.demo.coreSdk.common.SkyNetDialog
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver

@Composable
fun RequestEnableBluetooth(
    onEnabled: () -> Unit
) {

    var isShowExplanationDialog by remember { mutableStateOf(false) }

    val registerForResult = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { activityResult ->
        if (activityResult.resultCode == Activity.RESULT_OK) {
            onEnabled()
        } else if (activityResult.resultCode == Activity.RESULT_CANCELED) {
            isShowExplanationDialog = true
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(key1 = lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                registerForResult.launch(enableBtIntent)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    var isOpenBluetoothSetting by remember {
        mutableStateOf(false)
    }
    if (isShowExplanationDialog) {
        SkyNetDialog(
            title = "Bluetooth is off",
            message = "To scan and connect to your devices, please go to the Settings and turn Bluetooth on.",
            positiveButtonText = "Go to Settings",
            onPositiveClicked = {
                isOpenBluetoothSetting = true
                isShowExplanationDialog = false
            },
            onDismissRequest = {
                isShowExplanationDialog = false
            },
            properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = false)
        )
    }

    if (isOpenBluetoothSetting) {
        BluetoothSetting {
            onEnabled()
        }
    }

}

@Composable
fun BluetoothSetting(onEnabled: () -> Unit) {
    val registerForResult = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { activityResult ->
        if (activityResult.resultCode == Activity.RESULT_OK) {
            onEnabled()
        }
    }

    SideEffect {
        val intent = Intent(Settings.ACTION_BLUETOOTH_SETTINGS)
        registerForResult.launch(intent)
    }
}