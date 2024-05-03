package ai.nami.sdk.sample.positioning.info

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle


@Composable
fun WidarNetworkInfoScreen(
    viewModel: WidarNetworkInfoViewModel,
    onConnect: (placeId: String, sessionCode: String, deviceUrn: String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isLoading by remember(uiState.isLoading) {
        derivedStateOf { uiState.isLoading }
    }
    Scaffold(
    ) { paddingValues ->
        WidarNetworkInfoBodyScreen(
            onConnect = onConnect,
            modifier = Modifier.padding(paddingValues),
            listPairedDeviceUrn = uiState.listPairedDeviceUrns,
            isLoading = isLoading
        )
    }
}

@Composable
fun WidarNetworkInfoBodyScreen(
    modifier: Modifier = Modifier,
    onConnect: (placeId: String, sessionCode: String, deviceUrn: String) -> Unit,
    listPairedDeviceUrn: List<String>, // should use ImmutableList for production app
    isLoading: Boolean
) {

    var placeId by remember {
        mutableStateOf("5563")
    }

    var sessionCode by remember {
        mutableStateOf("")
    }

    var selectedDeviceUrn by remember {
        mutableStateOf("")
    }

    val isShowWarningMessage by remember(isLoading, listPairedDeviceUrn) {
        derivedStateOf {
            !isLoading && listPairedDeviceUrn.isEmpty()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {

            Spacer(modifier = Modifier.height(24.dp))

            if (isShowWarningMessage) {
                Text(text = "There is no WiDar devices, please pair one to test this feature")
            } else {

                Text(text = "Session code")
                TextField(
                    value = sessionCode,
                    onValueChange = { sessionCode = it },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))

                Text(text = "Place Id")
                TextField(
                    value = placeId,
                    onValueChange = { placeId = it },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Note: It is a simple app for demo purpose, so we just show all devices that were paired by this app in here.\n" +
                        "If you pair WiDar device and other devices (Mesh sensor, Nami Plug) we have no way to distinguish them for you.\n"
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(listPairedDeviceUrn) { deviceUrn ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedDeviceUrn = deviceUrn
                                },
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = deviceUrn)
                            if (deviceUrn == selectedDeviceUrn) {
                                Icon(imageVector = Icons.Default.Check, contentDescription = "")
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Button(onClick = {
                    onConnect(placeId, sessionCode, selectedDeviceUrn)
                }, modifier = Modifier.fillMaxWidth(), enabled = selectedDeviceUrn.isNotEmpty()) {
                    Text("Start Positioning")
                }
            }

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    LinearProgressIndicator()
                }
            }
        }
    }

}

