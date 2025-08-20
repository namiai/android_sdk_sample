package ai.nami.demo_sdk_ui_extension

import ai.nami.sdk.NamiSDK
import ai.nami.sdk_ui_extensions.config.NamiAppearance
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext

enum class TypeStartingEntryPoint(val title: String) {
    StartingSetupAKit("Start set up a kit"),
    StartingSetupASingleDevice("Start setup a single device"),
    Settings("Settings");
}

@Composable
fun HomeScreen(
    onPresentTemplate: (
        clientID: String,
        startingEntryPoint: TypeStartingEntryPoint,
        shouldCreateDefaultRoomForNewZone: Boolean,
        appearance: NamiAppearance,
        baseUrl: String,
    ) -> Unit,
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

    var appearance by remember {
        mutableStateOf(NamiAppearance.Light)
    }

    // https://mobile-screens.nami.surf/divkit/v0.2.0
    var baseUrl by remember {
        mutableStateOf("https://mobile-screens.nami.surf/divkit/v0.3.0/precompiled_layouts")
    }

    val isNeedASessionCode by remember {
        mutableStateOf(NamiSDK.shouldInit())
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

    var selectedEntryPoint by remember {
        mutableStateOf(TypeStartingEntryPoint.StartingSetupAKit)
    }

    var shouldCreateDefaultRoomForNewZone by remember {
        mutableStateOf(false)
    }

    val isEnableButton by remember(isNeedASessionCode, clientID, sessionCode) {
        derivedStateOf {
            if (clientID.isEmpty() || baseUrl.isEmpty()) {
                false
            } else if (!isNeedASessionCode) {
                true
            } else sessionCode.isNotEmpty()
        }
    }

    LaunchedEffect(key1 = uiState.initSDKSuccess) {
        if (uiState.initSDKSuccess == true) {
            onPresentTemplate(
                clientID,
                selectedEntryPoint,
                shouldCreateDefaultRoomForNewZone,
                appearance,
                baseUrl
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = MaterialTheme.colors.background
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            Spacer(modifier = Modifier.height(48.dp))
            AnimatedVisibility(visible = isShowError) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = errorMessage ?: "",
                    style = MaterialTheme.typography.body1.copy(color = MaterialTheme.colors.error)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            OutlinedTextField(
                value = sessionCode, onValueChange = {
                    sessionCode = it
                }, modifier = Modifier.fillMaxWidth(), label = {
                    Text(
                        text = "Session code",
                        style = MaterialTheme.typography.body1.copy(color = MaterialTheme.colors.onBackground)
                    )
                },
                colors = TextFieldDefaults.outlinedTextFieldColors(textColor = MaterialTheme.colors.onBackground)
            )
            if (!isNeedASessionCode) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "No need a new session code. You can leave this field empty",
                    style = MaterialTheme.typography.body1.copy(color = MaterialTheme.colors.onBackground)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            OutlinedTextField(
                value = baseUrl, onValueChange = {
                    baseUrl = it
                }, modifier = Modifier.fillMaxWidth(), label = {
                    Text(
                        text = "Base Url",
                        style = MaterialTheme.typography.body1.copy(color = MaterialTheme.colors.onBackground)
                    )
                },
                colors = TextFieldDefaults.outlinedTextFieldColors(textColor = MaterialTheme.colors.onBackground)
            )

            Spacer(modifier = Modifier.height(24.dp))
            Text(
                "Select starting entrypoint",
                style = MaterialTheme.typography.body1.copy(color = MaterialTheme.colors.onBackground)
            )
            Spacer(modifier = Modifier.height(4.dp))

            DropdownSelector(
                selectedItem = selectedEntryPoint,
                items = TypeStartingEntryPoint.values().toList(),
                titleForItem = { item -> item?.title ?: "" }
            ) {
                selectedEntryPoint = it
            }


            Spacer(modifier = Modifier.height(24.dp))
            Text(
                "Mode",
                style = MaterialTheme.typography.body1.copy(color = MaterialTheme.colors.onBackground)
            )
            Spacer(modifier = Modifier.height(4.dp))
            DropdownSelector(
                selectedItem = appearance,
                items = NamiAppearance.values().toList(),
                titleForItem = {
                    it?.mode ?: ""
                }) {
                appearance = it
            }

            Spacer(modifier = Modifier.height(24.dp))
            OutlinedTextField(
                value = clientID, onValueChange = {
                    clientID = it
                }, modifier = Modifier.fillMaxWidth(), label = {
                    Text(
                        text = "Client ID",
                        style = MaterialTheme.typography.body1.copy(color = MaterialTheme.colors.onBackground)
                    )
                },
                colors = TextFieldDefaults.outlinedTextFieldColors(textColor = MaterialTheme.colors.onBackground)
            )


            Spacer(modifier = Modifier.height(48.dp))
            Button(modifier = Modifier.fillMaxWidth(), onClick = {
                if (sessionCode.isNotEmpty()) {
                    sendViewIntent(HomeViewIntent.InitNamiSDK(sessionCode))
                } else if (!isNeedASessionCode) {
                    onPresentTemplate(
                        clientID,
                        selectedEntryPoint,
                        shouldCreateDefaultRoomForNewZone,
                        appearance,
                        baseUrl
                    )
                }
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

        AnimatedVisibility(visible = isLoading, enter = fadeIn(), exit = fadeOut()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable {

                    }, contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(36.dp),
                    color = MaterialTheme.colors.onBackground
                )
            }
        }
    }
}


@Composable
fun <T> DropdownSelector(
    modifier: Modifier = Modifier,
    selectedItem: T?,
    items: List<T>,
    titleForItem: (T?) -> String,
    onSelected: (T) -> Unit
) {
    var expanded by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = modifier.border(
            width = 1.dp,
            color = MaterialTheme.colors.onBackground,
            shape = MaterialTheme.shapes.medium
        )
    ) {
        Text(
            text = titleForItem(selectedItem),
            style = MaterialTheme.typography.body1.copy(color = MaterialTheme.colors.onBackground),
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = { expanded = true })
                .padding(16.dp)
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            items.forEach { item ->
                DropdownMenuItem(onClick = {
                    onSelected(item)
                    expanded = false
                }) {
                    Text(text = titleForItem(item))
                }
            }
        }
    }
}