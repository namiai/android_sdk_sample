package ai.nami.demo.coreSdk.common

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@Composable
fun SkyNetPermissionRequest(
    permissions: List<String>,
    titleDialog: String? = null,
    messageDialog: String? = null,
    textButtonDialog: String? = null,
    showDialogOnDeny: Boolean = true,
    onGrantedAllPermission: () -> Unit = {},
    onDeniedPermissions: () -> Unit = {}
) {
    val context = LocalContext.current
    var isShowDialog by remember { mutableStateOf(false) }

    val checkPermissionsNotGranted: (List<String>, Map<String, Boolean>) -> Boolean =
        { listPermissions, result ->
            listPermissions.any { result[it] == false }
        }

    PermissionsRequests(permissions = permissions) { result ->
        val isPermissionNotGranted = checkPermissionsNotGranted(permissions, result)
        if (isPermissionNotGranted) {
            if (showDialogOnDeny) {
                isShowDialog = true
            }
            onDeniedPermissions()
        } else {
            onGrantedAllPermission()
        }
    }

    if (isShowDialog) {
        SkyNetDialog(
            title = titleDialog ?: "",
            message = messageDialog ?: "",
            positiveButtonText = textButtonDialog,
            onPositiveClicked = {
                context.launchPermissionAppSetting()
                isShowDialog = false
            },
            onDismissRequest = {
                isShowDialog = false
            })
    }
}

fun Context.launchPermissionAppSetting() {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    intent.data = Uri.parse("package:$packageName")
    startActivity(intent)
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionsRequests(
    permissions: List<String>,
    onPermissionsResult: (Map<String, Boolean>) -> Unit = {}
) {
    val permissionStates =
        rememberMultiplePermissionsState(
            permissions = permissions,
            onPermissionsResult = onPermissionsResult
        )

    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(key1 = lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                permissionStates.launchMultiplePermissionRequest()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}