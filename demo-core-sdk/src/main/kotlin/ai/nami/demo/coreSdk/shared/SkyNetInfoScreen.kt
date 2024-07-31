package ai.nami.demo.coreSdk.shared

import ai.nami.demo.coreSdk.common.NamiDropdown
import ai.nami.demo.coreSdk.common.SkyNetButton
import ai.nami.demo.coreSdk.common.SkyNetScaffold
import ai.nami.sdk.model.DeviceCategory
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.fatherofapps.jnav.annotations.JNav


@JNav(
    baseRoute = "sky_net_info_route",
    destination = "sky_net_info_destination",
    name = "SkyNetInfoNavigation"
)
@Composable
fun SkyNetInfoRoute(
    onNext: (sessionCode: String, roomId: String, DeviceCategory) -> Unit,
    onBack: () -> Unit
) {

    // you can implement ViewModel to get session code and room id, then pass it to SkyNetInfoScreen

    SkyeNetInfoScreen(onNext = onNext, onBack = onBack)
}


@Composable
private fun SkyeNetInfoScreen(
    onNext: (sessionCode: String, roomId: String, DeviceCategory) -> Unit,
    onBack: () -> Unit
) {
    var sessionCode by remember {
        mutableStateOf("")
    }

    var roomId by remember {
        mutableStateOf("e81c075e-f5c0-4104-b814-62d12cfaa368")
    }

    val isEnableButton by remember(sessionCode, roomId) {
        derivedStateOf {
            sessionCode.isNotEmpty() && roomId.isNotEmpty()
        }
    }

    val listDeviceCategories =
        DeviceCategory.values().toList().filter { it != DeviceCategory.OTHERS }

    var currentCategory by remember {
        mutableStateOf(listDeviceCategories.first())
    }

    SkyNetScaffold(title = "Info", onBack = onBack) {
        Spacer(modifier = Modifier.height(48.dp))
        OutlinedTextField(
            value = sessionCode,
            onValueChange = { sessionCode = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = "Session Code") },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            )
        )
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(
            value = roomId,
            onValueChange = { roomId = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = "Room UID") },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done
            )
        )

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

        SkyNetButton(
            modifier = Modifier.fillMaxWidth(),
            text = "Next",
            onClick = {
                onNext(sessionCode, roomId, currentCategory)
            },
            enabled = isEnableButton,
        )

    }
}