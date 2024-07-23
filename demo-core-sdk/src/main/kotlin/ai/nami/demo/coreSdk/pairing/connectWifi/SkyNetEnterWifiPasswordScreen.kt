package ai.nami.demo.coreSdk.pairing.connectWifi

import ai.nami.sdk.pairing.viewmodels.connectwifi.enterwifipassword.EnterWifiPasswordViewIntent
import ai.nami.sdk.pairing.viewmodels.connectwifi.enterwifipassword.EnterWifiPasswordViewModel
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fatherofapps.jnav.annotations.JNav
import com.fatherofapps.jnav.annotations.JNavArg
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.collect

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
        )
    ]
)
@Composable
fun SkyNetEnterWifiPasswordRoute(
    viewModel: EnterWifiPasswordViewModel,
    wifiName: String,
    onBack: () -> Unit,
    onNavigateConnectWifiNetwork: (isJoinThread: Boolean) -> Unit,
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

    val sendViewIntent: (EnterWifiPasswordViewIntent) -> Unit = remember {
        { viewIntent -> viewIntentChannel.trySend(viewIntent) }
    }
}

@Composable
private fun SkyNetEnterWifiPasswordScreen() {

}