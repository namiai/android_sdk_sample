package ai.nami.demo.coreSdk.pairing.connectWifi

import ai.nami.demo.core.sdk.R
import ai.nami.demo.coreSdk.common.SkyNetButton
import ai.nami.demo.coreSdk.common.SkyNetScaffold
import ai.nami.sdk.pairing.viewmodels.connectwifi.enterwifipassword.EnterWifiPasswordViewIntent
import ai.nami.sdk.pairing.viewmodels.connectwifi.enterwifipassword.EnterWifiPasswordViewModel
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
    baseRoute = "sky_net_enter_wifi_password_route",
    destination = "sky_net_enter_wifi_password_destination",
    name = "SkyNetEnterWifiPasswordNavigation",
    arguments = [
        JNavArg(
            name = "wifiName",
            type = String::class,
            isNullable = false
        ),
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
fun SkyNetEnterWifiPasswordRoute(
    viewModel: EnterWifiPasswordViewModel,
    wifiName: String,
    onBack: () -> Unit,
    onNavigateConnectWifiNetwork: () -> Unit,
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val viewIntentChannel = remember {
        Channel<EnterWifiPasswordViewIntent>(Channel.UNLIMITED)
    }

    LaunchedEffect(key1 = Unit) {
        withContext(Dispatchers.Main.immediate) {
            viewIntentChannel.consumeAsFlow().onEach(viewModel::handleViewIntent).collect()
        }
    }

    LaunchedEffect(key1 = uiState.isSavedPassword) {
        if (uiState.isSavedPassword) {
            onNavigateConnectWifiNetwork()
        }
    }

    val sendViewIntent: (EnterWifiPasswordViewIntent) -> Unit = remember {
        { viewIntent -> viewIntentChannel.trySend(viewIntent) }
    }

    SkyNetEnterWifiPasswordScreen(
        wifiName = wifiName,
        onBack = onBack,
        onSavePassword = {
            sendViewIntent(EnterWifiPasswordViewIntent.SavePassword(it))
        },
        isLoading = uiState.isLoading
    )

}

@Composable
private fun SkyNetEnterWifiPasswordScreen(
    wifiName: String,
    onBack: () -> Unit,
    onSavePassword: (password: String) -> Unit,
    isLoading: Boolean
) {

    var wifiPassword by remember {
        mutableStateOf("")
    }

    val isEnableButton by remember(wifiPassword) {
        derivedStateOf { wifiPassword.isNotEmpty() }
    }

    var isShowPassword by remember {
        mutableStateOf(false)
    }
    val interactionSource = remember { MutableInteractionSource() }
    SkyNetScaffold(title = "Enter Wifi Password", onBack = onBack, isLoading = isLoading) {
        Spacer(modifier = Modifier.height(48.dp))
        OutlinedTextField(
            value = wifiPassword, onValueChange = {
                wifiPassword = it
            },
            label = {
                Text(text = "Enter password for $wifiName")
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
                onSavePassword(wifiPassword)
            },
            enabled = isEnableButton,
            modifier = Modifier.fillMaxWidth(),
        )
    }


}