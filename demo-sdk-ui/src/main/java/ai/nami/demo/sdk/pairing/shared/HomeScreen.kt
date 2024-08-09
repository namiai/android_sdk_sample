package ai.nami.demo.sdk.pairing.shared

import ai.nami.demo.sdk.ui.components.NamiDropdown
import ai.nami.sdk.common.NamiSdkSession
import ai.nami.sdk.model.DeviceCategory
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(onPairNamiDevice: (String?, String, DeviceCategory) -> Unit) {

    var sessionCode by remember {
        mutableStateOf("")
    }
    val isNeedASessionCode by remember {
        mutableStateOf(!NamiSdkSession.isAuthorized())
    }
    var roomId by remember {
        mutableStateOf("e81c075e-f5c0-4104-b814-62d12cfaa368")
    }

    val listDeviceCategories =
        DeviceCategory.values().toList().filter { it != DeviceCategory.OTHERS }

    var currentCategory by remember {
        mutableStateOf(listDeviceCategories.first())
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        OutlinedTextField(value = sessionCode, onValueChange = {
            sessionCode = it
        }, modifier = Modifier.fillMaxWidth(), label = {
            Text(text = "Session code")
        })
        if (!isNeedASessionCode) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = "No need a new session code. You can leave this field empty")
        }
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(value = roomId, onValueChange = {
            roomId = it
        }, modifier = Modifier.fillMaxWidth(), label = {
            Text(text = "Room ID")
        })
        Spacer(modifier = Modifier.height(24.dp))
        NamiDropdown(
            currentValue = currentCategory.categoryName,
            listTitles = listDeviceCategories.map { it.categoryName },
            onSelectItem = {
                currentCategory = listDeviceCategories[it]
            },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(48.dp))
        Button(onClick = {
            onPairNamiDevice(sessionCode, roomId, currentCategory)
        }) {
            Text("Pair Device")
        }
    }
}