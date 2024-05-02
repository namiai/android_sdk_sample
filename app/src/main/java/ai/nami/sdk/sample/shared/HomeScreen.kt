package ai.nami.sdk.sample.shared

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
fun HomeScreen(onPairNamiDevice: (String, String) -> Unit) {

    var sessionCode by remember {
        mutableStateOf("")
    }

    var roomId by remember {
        mutableStateOf("cba2eefc-f19a-4e5a-b154-d7f27cd4e6a2")
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
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(value = roomId, onValueChange = {
            roomId = it
        }, modifier = Modifier.fillMaxWidth(), label = {
            Text(text = "Room ID")
        })
        Spacer(modifier = Modifier.height(48.dp))
        Button(onClick = {
            onPairNamiDevice(sessionCode, roomId)
        }) {
            Text("Pair Device")
        }
    }
}