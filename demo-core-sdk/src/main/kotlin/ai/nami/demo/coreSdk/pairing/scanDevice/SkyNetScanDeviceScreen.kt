package ai.nami.demo.coreSdk.pairing.scanDevice

import ai.nami.demo.coreSdk.common.SkyNetButton
import ai.nami.demo.coreSdk.common.SkyNetPermissionRequest
import ai.nami.demo.coreSdk.common.SkyNetScaffold
import ai.nami.demo.coreSdk.common.launchPermissionAppSetting
import ai.nami.sdk.common.DEFAULT_PRODUCT_ID
import ai.nami.sdk.pairing.viewmodels.scandevice.ScanDeviceViewIntent
import ai.nami.sdk.pairing.viewmodels.scandevice.ScanDeviceViewModel
import android.Manifest
import android.os.Build
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fatherofapps.jnav.annotations.JNav
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext

@JNav(
    baseRoute = "sky_net_scan_device_route",
    destination = "sky_net_scan_device_destination",
    name = "SkyNetScanDeviceNavigation"
)
@Composable
fun SkyNetScanDeviceRoute(
    viewModel: ScanDeviceViewModel,
    onBack: () -> Unit,
    onScanDeviceSuccess: (productId: Int, deviceName: String, zoneName: String) -> Unit,
    onNavigateBluetoothDisconnectedScreen: () -> Unit
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val viewIntentChannel = remember {
        Channel<ScanDeviceViewIntent>(Channel.UNLIMITED)
    }

    LaunchedEffect(key1 = Unit) {
        withContext(Dispatchers.Main.immediate) {
            viewIntentChannel.consumeAsFlow().onEach(viewModel::handleViewIntent).collect()
        }
    }

    val sendViewIntent: (ScanDeviceViewIntent) -> Unit = remember {
        { viewIntent -> viewIntentChannel.trySend(viewIntent) }
    }

    val onAllPermissionGranted: () -> Unit = remember {
        {
            sendViewIntent(ScanDeviceViewIntent.ScanDevice)
        }
    }

    var isDenyScanDevicePermission by remember {
        mutableStateOf(false)
    }
    val context = LocalContext.current

    var isRequestPermissions by remember {
        mutableStateOf(false)
    }


    var isShouldRequestEnableBluetooth by remember {
        mutableStateOf(false)
    }

    var isShouldRequestLocationService by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(key1 = uiState.isCheckBluetoothComplete) {
        if (uiState.isCheckBluetoothComplete) {
            val isBluetoothEnabled = uiState.isEnabledBluetooth
            if (isBluetoothEnabled) {
                val shouldCheckLocationService = uiState.isShouldCheckLocationService
                if (!shouldCheckLocationService) {
                    onAllPermissionGranted()
                }
                isShouldRequestLocationService = shouldCheckLocationService

            } else {
                isShouldRequestEnableBluetooth = true
            }
        }
    }

    if (isShouldRequestEnableBluetooth) {
        RequestEnableBluetooth {
            if (!uiState.isShouldCheckLocationService) {
                onAllPermissionGranted()
            }
            isShouldRequestLocationService = uiState.isShouldCheckLocationService
        }
    }


    if (isShouldRequestLocationService) {
        RequestLocationService(onEnabled = { isSuccess ->
            if (isSuccess) {
                onAllPermissionGranted()
            } else {
                isDenyScanDevicePermission = true
            }
        })
    }

    LaunchedEffect(key1 = uiState.isCanceledPairing) {
        if (uiState.isCanceledPairing) {
            onBack()
        }
    }

    LaunchedEffect(key1 = uiState.isFoundDevice) {
        if (uiState.isFoundDevice) {
            uiState.productId?.let { productId ->
                onScanDeviceSuccess(productId, uiState.deviceName, uiState.zoneName)
            }
        }
    }

    LaunchedEffect(key1 = uiState.isBluetoothDisconnected) {
        if (uiState.isBluetoothDisconnected) {
            onNavigateBluetoothDisconnectedScreen()
        }
    }

    val errorMessage by remember(uiState.pairingError) {
        derivedStateOf {
            uiState.pairingError?.errorMessage ?: "Error code: ${uiState.pairingError?.code}"
        }
    }

    if (!isRequestPermissions) {
        SkyNetPermissionRequest(
            permissions = getRequirePermissions(),
            showDialogOnDeny = false,
            onGrantedAllPermission = {
                isRequestPermissions = true
                viewModel.handleViewIntent(ScanDeviceViewIntent.CheckBluetooth)
            }, onDeniedPermissions = {
                isRequestPermissions = true
                isDenyScanDevicePermission = true
            }
        )
    }

    SkyNetScanDeviceScreen(
        errorMessage = errorMessage,
        isDenyScanDevicePermission = isDenyScanDevicePermission,
        productId = uiState.productId,
        deviceName = uiState.deviceName,
        onBack = {
            sendViewIntent(ScanDeviceViewIntent.CancelPairing)
        },
        onOpenSettings = context::launchPermissionAppSetting
    )

}


@Composable
private fun SkyNetScanDeviceScreen(
    errorMessage: String?,
    isDenyScanDevicePermission: Boolean,
    productId: Int?,
    deviceName: String?,
    onBack: () -> Unit,
    onOpenSettings: () -> Unit
) {

    SkyNetScaffold(title = "Scanning Device", onBack = onBack, errorMessage = errorMessage) {
        AnimatedContent(targetState = isDenyScanDevicePermission, label = "") { isDeny ->
            if (isDeny) {
                MissingPermission {
                    onOpenSettings()
                }
            } else {
                ScanningDevice(productId = productId, deviceName = deviceName)
            }
        }
    }
}

@Composable
private fun MissingPermission(onOpenSettings: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        Text(text = "⚠\uFE0F Missing permissions. Can not scan the device")
        Spacer(modifier = Modifier.height(24.dp))
        SkyNetButton(text = "Open Settings", onClick = onOpenSettings, enabled = true)
    }
}


@Composable
fun ScanningDevice(
    productId: Int?,
    deviceName: String?,
) {
    val foundDevice by remember(productId, deviceName) {
        derivedStateOf { productId != null && productId != DEFAULT_PRODUCT_ID && deviceName?.isNotEmpty() == true }
    }

    val headerText by remember(foundDevice) {
        derivedStateOf {
            if (foundDevice) "Found $deviceName" else "Searching for device ..."
        }
    }

    val messageText by remember(foundDevice) {
        derivedStateOf {
            if (foundDevice) "Connecting to this device" else "Please hold"
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        Text(
            text = headerText,
            style = MaterialTheme.typography.h5
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = messageText, style = MaterialTheme.typography.body1)
    }
}


private fun getRequirePermissions(): List<String> {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        listOf(
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT
        )
    } else {
        listOf(
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }
}