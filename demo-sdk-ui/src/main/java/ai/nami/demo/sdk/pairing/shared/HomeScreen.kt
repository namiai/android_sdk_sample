package ai.nami.demo.sdk.pairing.shared


import ai.nami.demo.sdk.ui.components.NamiDropdown
import ai.nami.sdk.NamiSDK
import ai.nami.sdk.common.NamiLog
import ai.nami.sdk.extensions.getDeviceCategoryName
import ai.nami.sdk.model.DeviceCategory
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
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext


@Composable
fun HomeRoute(
    onPairNamiDevice: (roomUID: String, deviceCategory: DeviceCategory) -> Unit,
    viewModel: HomeViewModel
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val viewIntentChannel = remember {
        Channel<HomeViewIntent>(Channel.UNLIMITED)
    }

    LaunchedEffect(key1 = Unit) {
        withContext(Dispatchers.Main.immediate) {
            viewIntentChannel.consumeAsFlow().onEach(viewModel::handleViewIntent).collect()
        }
    }

    val sendViewIntent: (HomeViewIntent) -> Unit = remember {
        { viewIntent -> viewIntentChannel.trySend(viewIntent) }
    }

    var sessionCode by remember {
        mutableStateOf("")
    }

    val isNeedASessionCode by remember {
        mutableStateOf(NamiSDK.shouldInit())
    }

    // zone
    var roomId by remember {
        mutableStateOf("89648c39-6c8e-4b86-8ab1-f36f4532191f")
    }

    val listDeviceCategories =
        DeviceCategory.values().toList().filter { it != DeviceCategory.OTHERS }

    var currentCategory by remember {
        mutableStateOf(listDeviceCategories.first())
    }

    val context = LocalContext.current

    SideEffect {
        NamiLog.e(tag = "debug_sample_nami", message = "HomeRoute UIState: $uiState")
    }
    LaunchedEffect(key1 = uiState.initSDKSuccess) {
        if (uiState.initSDKSuccess == true) {
            NamiLog.e(tag = "debug_sample_nami", message = "HomeRoute calls onPairNamiDevice")
            onPairNamiDevice(roomId, currentCategory)
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
            AnimatedVisibility(visible = isShowError) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = errorMessage ?: "",
                    style = MaterialTheme.typography.body1.copy(color = MaterialTheme.colors.error)
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            OutlinedTextField(value = roomId, onValueChange = {
                roomId = it
            }, modifier = Modifier.fillMaxWidth(), label = {
                Text(text = "Room ID")
            })
            Spacer(modifier = Modifier.height(24.dp))
            NamiDropdown(
                currentValue = currentCategory.getDeviceCategoryName(context),
                listTitles = listDeviceCategories.map { it.getDeviceCategoryName(context) },
                onSelectItem = {
                    currentCategory = listDeviceCategories[it]
                },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(48.dp))
            Button(onClick = {
                if (sessionCode.isNotEmpty()) {
                    sendViewIntent(HomeViewIntent.InitNamiSDK(sessionCode))
                } else if (!isNeedASessionCode && roomId.isNotEmpty()) {
                    onPairNamiDevice(roomId, currentCategory)
                }
            }) {
                Text("Pair Device")
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