package ai.nami.demo.coreSdk.pairing.connectWifi

import ai.nami.demo.core.sdk.R
import ai.nami.demo.coreSdk.common.SkyNetButton
import ai.nami.demo.coreSdk.common.SkyNetScaffold
import ai.nami.sdk.pairing.viewmodels.connectwifi.addanotherwifi.AddAnotherWifiNetworkViewIntent
import ai.nami.sdk.pairing.viewmodels.connectwifi.addanotherwifi.AddAnotherWifiNetworkViewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
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
    baseRoute = "sky_net_add_wifi_network_route",
    destination = "sky_net_add_wifi_network_destination",
    name = "SkyNetAddWifiNetworkNavigation",
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
fun SkyNetAddWifiNetworkRoute(
    viewModel: AddAnotherWifiNetworkViewModel,
    onBack: () -> Unit,
    onNavigateConnectWifiNetwork: () -> Unit,
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val viewIntentChannel = remember {
        Channel<AddAnotherWifiNetworkViewIntent>(Channel.UNLIMITED)
    }

    LaunchedEffect(key1 = Unit) {
        withContext(Dispatchers.Main.immediate) {
            viewIntentChannel.consumeAsFlow().onEach(viewModel::handleViewIntent).collect()
        }
    }

    val sendViewIntent: (AddAnotherWifiNetworkViewIntent) -> Unit = remember {
        { viewIntent -> viewIntentChannel.trySend(viewIntent) }
    }

    LaunchedEffect(key1 = uiState.isSavedWifiNetwork) {
        if (uiState.isSavedWifiNetwork) {
            onNavigateConnectWifiNetwork()
        }
    }

    SkyNetAddWifiNetworkScreen(
        onBack = onBack,
        onSaveWifiNetwork = { wifiName, wifiPassword ->
            sendViewIntent(
                AddAnotherWifiNetworkViewIntent.SaveWifiNetwork(
                    wifiName = wifiName,
                    wifiPassword = wifiPassword
                )
            )
        },
        isLoading = uiState.isLoading
    )

}

@Composable
private fun SkyNetAddWifiNetworkScreen(
    onBack: () -> Unit,
    onSaveWifiNetwork: (name: String, password: String) -> Unit,
    isLoading: Boolean
) {

    var wifiPassword by remember {
        mutableStateOf("")
    }

    var wifiName by remember {
        mutableStateOf("")
    }

    val isEnableButton by remember(wifiName, wifiPassword) {
        derivedStateOf { wifiName.isNotEmpty() && wifiPassword.isNotEmpty() }
    }

    var isShowPassword by remember {
        mutableStateOf(false)
    }
    val interactionSource = remember { MutableInteractionSource() }
    SkyNetScaffold(title = "Enter Wifi Password", onBack = onBack, isLoading = isLoading) {
        Spacer(modifier = Modifier.height(48.dp))
        OutlinedTextField(
            value = wifiName,
            onValueChange = {
                wifiName = it
            },
            label = {
                Text(text = "Wifi Network's name")
            },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            ),
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = wifiPassword, onValueChange = {
                wifiPassword = it
            },
            label = {
                Text(text = "Wifi Network's password")
            },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (isShowPassword) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            trailingIcon = {
                Image(painter = painterResource(id = R.drawable.ic_eye), contentDescription = "",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable(interactionSource, null) {
                            isShowPassword = !isShowPassword
                        }
                )
            }
        )
        Spacer(modifier = Modifier.height(12.dp))
        SkyNetButton(
            text = "Connect",
            onClick = {
                onSaveWifiNetwork(wifiName, wifiPassword)
            },
            enabled = isEnableButton,
            modifier = Modifier.fillMaxWidth(),
        )
    }

}