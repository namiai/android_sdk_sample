package ai.nami.demo.coreSdk.pairing.connectWifi

import ai.nami.demo.coreSdk.common.SkyNetDialog
import ai.nami.demo.coreSdk.common.SkyNetScaffold
import ai.nami.sdk.pairing.model.PairingErrorCode
import ai.nami.sdk.pairing.model.PairingWifiInfo
import ai.nami.sdk.pairing.viewmodels.connectwifi.scanning.ScanWifiNetworkViewIntent
import ai.nami.sdk.pairing.viewmodels.connectwifi.scanning.ScanWifiNetworkViewModel
import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fatherofapps.jnav.annotations.JNav
import com.fatherofapps.jnav.annotations.JNavArg
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext

@JNav(
    baseRoute = "sky_net_scan_wifi_network_route",
    destination = "sky_net_scan_wifi_network_destination",
    name = "SkyNetScanWifiNetworkNavigation",
    arguments = [
        JNavArg(
            name = "deviceName",
            type = String::class,
            isNullable = false
        ),
        JNavArg(
            name = "isJoinThreadNetwork",
            type = Boolean::class,
            isNullable = false
        )
    ]
)

@Composable
fun SkyNetScanWifiNetworkRoute(
    viewModel: ScanWifiNetworkViewModel,
    onBack: () -> Unit,
    onNavigateEnterWifiPasswordScreen: (String) -> Unit,
    onNavigateAddAnotherWifiNetworkScreen: () -> Unit,
    onNavigateConnectWifiNetwork: () -> Unit,
    onNavigateWifiNetworkErrorScreen: (PairingErrorCode?) -> Unit,
    onNavigateBluetoothDisconnectedScreen: () -> Unit
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val viewIntentChannel = remember {
        Channel<ScanWifiNetworkViewIntent>(Channel.UNLIMITED)
    }

    LaunchedEffect(key1 = Unit) {
        withContext(Dispatchers.Main.immediate) {
            viewIntentChannel.consumeAsFlow().onEach(viewModel::handleViewIntent).collect()
        }
    }

    val sendViewIntent: (ScanWifiNetworkViewIntent) -> Unit = remember {
        { viewIntent -> viewIntentChannel.trySend(viewIntent) }
    }

    LaunchedEffect(key1 = Unit) {
        sendViewIntent(ScanWifiNetworkViewIntent.ScanWifiNetwork)
    }

    LaunchedEffect(uiState.isCanceledPairing) {
        if (uiState.isCanceledPairing) {
            onBack()
        }
    }

    LaunchedEffect(uiState.isBluetoothDisconnected) {
        if (uiState.isBluetoothDisconnected) {
            onNavigateBluetoothDisconnectedScreen()
        }
    }

    LaunchedEffect(key1 = uiState.isSavedSelectedWifiNetwork, key2 = uiState.isOpenWifiNetwork) {
        if (uiState.isSavedSelectedWifiNetwork && uiState.isOpenWifiNetwork) {
            onNavigateConnectWifiNetwork()
        }
    }

    LaunchedEffect(key1 = uiState.isEnterWifiPassword) {
        if (uiState.isEnterWifiPassword) {
            onNavigateEnterWifiPasswordScreen(uiState.wifiName)
        }
    }

    var isShowPasswordConfirmDialog by remember(uiState.isShowConfirmUseSavedWifiPassword) {
        mutableStateOf(uiState.isShowConfirmUseSavedWifiPassword)
    }

    val errorMessage by remember(uiState.errorMessage, uiState.pairingError) {
        derivedStateOf {
            if (uiState.pairingError != null) uiState.pairingError?.errorMessage
                ?: "Pairing error with code ${uiState.pairingError?.code}" else uiState.errorMessage
        }
    }

    SkyNetScanWifiNetworkScreen(
        isScanning = uiState.isScanning,
        isShowAddAnotherWifiNetworkButton = uiState.isShowAddAnotherNetworkButton,
        listWifiNetworks = uiState.listWifiInfo,
        onSelectWifiNetwork = { selectedPairingWifiInfo ->
            sendViewIntent(ScanWifiNetworkViewIntent.SaveWifiNetWork(selectedPairingWifiInfo))
        },
        onAddAnotherWifiNetwork = {
            onNavigateAddAnotherWifiNetworkScreen()
        },
        onBack = {
            sendViewIntent(ScanWifiNetworkViewIntent.CancelPairing)
        },
        errorMessage = errorMessage
    )

    if (isShowPasswordConfirmDialog) {
        SkyNetDialog(
            title = "Found saved password",
            message = "Would you like to use the saved password for the network ${uiState.wifiName}”?",
            positiveButtonText = "OK",
            onPositiveClicked = {
                isShowPasswordConfirmDialog = false
                onNavigateConnectWifiNetwork()
            },
            negativeButtonText = "No",
            onNegativeClicked = {
                isShowPasswordConfirmDialog = false
                onNavigateEnterWifiPasswordScreen(uiState.wifiName)
            }
        )
    }
}


@Composable
private fun SkyNetScanWifiNetworkScreen(
    isScanning: Boolean,
    isShowAddAnotherWifiNetworkButton: Boolean,
    listWifiNetworks: List<PairingWifiInfo>,
    onSelectWifiNetwork: (PairingWifiInfo) -> Unit,
    onAddAnotherWifiNetwork: () -> Unit,
    onBack: () -> Unit,
    errorMessage: String?
) {

    SideEffect {
        Log.e(
            "debug_core_sdk",
            "listWifiNetworks: ${listWifiNetworks.size} -- errorMessage: $errorMessage --- isScanning: $isScanning --- isShowAnother: $isShowAddAnotherWifiNetworkButton"
        )
    }

    SkyNetScaffold(title = "Scan Wifi Network", onBack = onBack, errorMessage = errorMessage) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = Color.LightGray,
                    shape = MaterialTheme.shapes.medium
                ),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            if (isScanning) {
                item {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    }
                }
            }

            items(listWifiNetworks, key = { it.ssid }) { pairingWifiInfo ->
                Text(
                    text = pairingWifiInfo.ssid,
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                        .clickable {
                            onSelectWifiNetwork(pairingWifiInfo)
                        }
                )
                Divider(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.LightGray,
                    thickness = 1.dp
                )
            }

            if (isShowAddAnotherWifiNetworkButton) {
                item {
                    Text(text = "Another wifi network",
                        style = MaterialTheme.typography.body1,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                            .clickable {
                                onAddAnotherWifiNetwork()
                            }
                    )
                }
            }

        }
    }


}