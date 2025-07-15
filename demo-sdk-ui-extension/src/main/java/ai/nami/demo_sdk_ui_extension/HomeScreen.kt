package ai.nami.demo_sdk_ui_extension

import ai.nami.sdk.NamiSDK
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onPresentTemplate: (clientID: String) -> Unit,
    viewModel: HomeViewModel
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val viewIntentChannel = remember {
        Channel<HomeViewIntent>(Channel.UNLIMITED)
    }

    LaunchedEffect(key1 = Unit) {
        withContext(Dispatchers.Main.immediate) {
            viewIntentChannel.consumeAsFlow().onEach(viewModel::handleViewIntent).collect {}
        }
    }

    val sendViewIntent: (HomeViewIntent) -> Unit = remember {
        { viewIntent -> viewIntentChannel.trySend(viewIntent) }
    }

    var sessionCode by remember {
        mutableStateOf("")
    }

    var clientID by remember {
        mutableStateOf("alarm_com_security")
    }


    val isNeedASessionCode by remember {
        mutableStateOf(NamiSDK.shouldInit())
    }
    LaunchedEffect(key1 = uiState.initSDKSuccess) {
        if (uiState.initSDKSuccess == true) {
            onPresentTemplate(clientID)
        }
    }

    val isLoading by remember(uiState.isLoading) {
        derivedStateOf { uiState.isLoading }
    }

    val errorMessage by remember(uiState.errorMessage) {
        derivedStateOf { uiState.errorMessage }
    }

    val isShowError by remember(errorMessage) {
        derivedStateOf { !errorMessage.isNullOrEmpty() }
    }

    val isEnableButton by remember(isNeedASessionCode, clientID, sessionCode) {
        derivedStateOf {
            if (clientID.isEmpty()) {
                false
            } else if (!isNeedASessionCode) {
                true
            } else sessionCode.isNotEmpty()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
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
            OutlinedTextField(value = clientID, onValueChange = {
                clientID = it
            }, modifier = Modifier.fillMaxWidth(), label = {
                Text(text = "Client ID")
            })

            AnimatedVisibility(visible = isShowError) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = errorMessage ?: "",
                    style = MaterialTheme.typography.body1.copy(color = MaterialTheme.colors.error)
                )
            }

            Spacer(modifier = Modifier.height(48.dp))
            Button(onClick = {
                if (sessionCode.isNotEmpty()) {
                    sendViewIntent(HomeViewIntent.InitNamiSDK(sessionCode))
                } else if (!isNeedASessionCode) {
                    onPresentTemplate(clientID)
                }
            }, enabled = isEnableButton) {
                Text("Start Setup A Kit")
            }
        }

        AnimatedVisibility(visible = isLoading, enter = fadeIn(), exit = fadeOut()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable {

                    }, contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(modifier = Modifier.size(36.dp))
            }
        }
    }
}