package demo_shared.session_code

import demo_shared.common.BaseScreen
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext

@Composable
fun SessionCodeScreen(
    modifier: Modifier = Modifier,
    viewModel: SessionCodeViewModel,
    onNavigateToHomeScreen: () -> Unit
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val viewIntentChannel = remember {
        Channel<SessionCodeViewIntent>(Channel.UNLIMITED)
    }

    LaunchedEffect(key1 = Unit) {
        withContext(Dispatchers.Main.immediate) {
            viewIntentChannel.consumeAsFlow().onEach(viewModel::handleViewIntent).collect {}
        }
    }

    val sendViewIntent: (SessionCodeViewIntent) -> Unit = remember {
        { viewIntent -> viewIntentChannel.trySend(viewIntent) }
    }

    var sessionCode by remember {
        mutableStateOf("")
    }

    val isEnableButton by remember(uiState, sessionCode) {
        derivedStateOf {
            sessionCode.isNotBlank() && !uiState.isLoading
        }
    }

    LaunchedEffect(uiState) {
        if (uiState.initSDKSuccess == true || uiState.isNeedASessionCode == false) {
            onNavigateToHomeScreen()
        }
    }

    BaseScreen(modifier = modifier,isLoading = uiState.isLoading) {

        OutlinedTextField(
            value = sessionCode, onValueChange = {
                sessionCode = it
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text(
                    text = "Session code",
                    style = MaterialTheme.typography.body1.copy(color = MaterialTheme.colors.onBackground)
                )
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(textColor = MaterialTheme.colors.onBackground)
        )

        Box(modifier = Modifier.weight(1f))
        Button(modifier = Modifier.fillMaxWidth(), onClick = {
            sendViewIntent(SessionCodeViewIntent.InitNamiSDK(sessionCode))
        }, enabled = isEnableButton) {
            Text(
                "Go",
                style = MaterialTheme.typography.body1.copy(
                    color =
                        if (isEnableButton)
                            MaterialTheme.colors.onPrimary else MaterialTheme.colors.onSurface
                )
            )
        }

    }


}