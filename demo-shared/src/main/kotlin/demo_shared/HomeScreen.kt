package demo_shared

import ai.nami.sdk_ui_extensions.config.NamiAppearance
import android.content.ClipData
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
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
    Settings("Settings"),

    SettingsPin("Pin Settings"),

    SettingsEntryExitDelays("Entry Exit Delays Setting"),

    SettingsSensitivity("Sensitivity Setting"),

    SystemTest("System Test"),

    Custom("Custom Entry point")
    ;
}

@Composable
fun HomeScreen(
    onPresentTemplate: (
        clientID: String,
        startingEntryPoint: TypeStartingEntryPoint,
        shouldCreateDefaultRoomForNewZone: Boolean,
        appearance: NamiAppearance,
        baseUrl: String,
        customRelativePath: String?,
        language: String,
        countryCode: String
    ) -> Unit,
    viewModel: HomeViewModel
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val clipboardManager = LocalClipboard.current
    val context = LocalContext.current

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

    var clientID by rememberSaveable {
        mutableStateOf("")
    }

    var lastSyncedClientID by remember {
        mutableStateOf<String?>(null)
    }

    var appearance by remember {
        mutableStateOf(NamiAppearance.Light)
    }

    // null represents the "Custom baseUrl" option
    val sdUIVersions = listOf<String?>("0.14.0","0.13.0", "0.12.0", "0.11.0", null)

    var sdUIVersion by rememberSaveable {
        mutableStateOf<String?>(sdUIVersions.first())
    }

    // Remembers the last explicitly selected version so we can restore it when leaving custom mode
    var lastSelectedSdUIVersion by rememberSaveable {
        mutableStateOf("0.13.0")
    }

    var customBaseUrl by rememberSaveable {
        mutableStateOf("")
    }

    val baseUrl by remember(sdUIVersion, customBaseUrl) {
        derivedStateOf {
            val version = sdUIVersion
            if (version == null) customBaseUrl
            else "https://mobile-screens.nami.surf/divkit/v$version/precompiled_layouts"
        }
    }

    var customRelativePath by remember {
        mutableStateOf<String?>("system-checkup/kit/alarm_com_falcon.json")
    }

    val canUseSavedSession by remember(uiState.isNeedASessionCode, uiState.placeID) {
        derivedStateOf { uiState.isNeedASessionCode == false && uiState.placeID != null }
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

    val isEnableButton by remember(canUseSavedSession, clientID, baseUrl, sessionCode) {
        derivedStateOf {
            if (clientID.isEmpty() || baseUrl.isEmpty()) {
                false
            } else if (canUseSavedSession) {
                true
            } else sessionCode.isNotBlank()
        }
    }

    val isShowCustomRelativePath by remember(selectedEntryPoint) {
        derivedStateOf { selectedEntryPoint == TypeStartingEntryPoint.Custom }
    }

    var language by remember {
        mutableStateOf("en")
    }

    var countryCode by remember {
        mutableStateOf("us")
    }

    LaunchedEffect(key1 = uiState.initSDKSuccess) {
        if (uiState.initSDKSuccess == true) {
            onPresentTemplate(
                clientID,
                selectedEntryPoint,
                shouldCreateDefaultRoomForNewZone,
                appearance,
                baseUrl,
                customRelativePath,
                language,
                countryCode
            )
            sendViewIntent(HomeViewIntent.OpenedSDK)
        }
    }

    LaunchedEffect(canUseSavedSession) {
        if (canUseSavedSession) {
            sessionCode = ""
        }
    }

    LaunchedEffect(uiState.clientID) {
        if (clientID.isEmpty() || clientID == lastSyncedClientID.orEmpty()) {
            clientID = uiState.clientID
        }
        lastSyncedClientID = uiState.clientID
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(
                WindowInsets.systemBars.union(WindowInsets.displayCutout).add(
                    WindowInsets.ime
                )
            )
            .background(
                color = MaterialTheme.colors.background
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            AnimatedVisibility(visible = isShowError) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = errorMessage ?: "",
                    style = MaterialTheme.typography.body1.copy(color = MaterialTheme.colors.error)
                )
                Spacer(modifier = Modifier.height(24.dp))
            }


            if (canUseSavedSession) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "You are logging in to place ${uiState.placeID}. Click Sign Out if you want to switch to another place or clear this session.",
                        style = MaterialTheme.typography.caption.copy(color = MaterialTheme.colors.onBackground),
                        modifier = Modifier
                            .weight(1f),
                    )

                    Button(
                        modifier = Modifier
                            .wrapContentWidth(),
                        onClick = { sendViewIntent(HomeViewIntent.SignOut) }
                    ) {
                        Text(
                            "Sign Out", style = MaterialTheme.typography.body1.copy(
                                color = MaterialTheme.colors.onPrimary
                            )
                        )
                    }
                }

            } else {
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

            Spacer(modifier = Modifier.height(24.dp))

            AnimatedVisibility(visible = sdUIVersion != null) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "Select a SDUI version",
                        style = MaterialTheme.typography.body1.copy(color = MaterialTheme.colors.onBackground)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    DropdownSelector(
                        selectedItem = sdUIVersion,
                        items = sdUIVersions,
                        titleForItem = { item -> item ?: "Custom baseUrl" }
                    ) { selected ->
                        if (selected != null) {
                            lastSelectedSdUIVersion = selected
                        }
                        sdUIVersion = selected
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Current base Url: $baseUrl",
                            style = MaterialTheme.typography.caption.copy(color = MaterialTheme.colors.onBackground),
                            modifier = Modifier.weight(1f),
                            maxLines = 5
                        )
                        Text(
                            text = "Copy",
                            style = MaterialTheme.typography.caption,
                            textDecoration = TextDecoration.Underline,
                            modifier = Modifier
                                .clickable {
                                    clipboardManager.nativeClipboard.setPrimaryClip(
                                        ClipData.newPlainText("baseUrl", baseUrl)
                                    )
//                                    clipboardManager.setText(AnnotatedString(baseUrl))
                                    Toast.makeText(
                                        context, "Copied", Toast.LENGTH_SHORT
                                    ).show()
                                }
                                .padding(vertical = 8.dp)
                        )
                    }

                }

            }
            AnimatedVisibility(visible = sdUIVersion == null) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Custom Base URL",
                            style = MaterialTheme.typography.body1.copy(color = MaterialTheme.colors.onBackground)
                        )
                        Text(
                            text = "Select a SDUI version",
                            style = MaterialTheme.typography.caption.copy(
                                color = MaterialTheme.colors.primary,
                                textDecoration = TextDecoration.Underline
                            ),
                            modifier = Modifier
                                .clickable { sdUIVersion = lastSelectedSdUIVersion }
                                .padding(vertical = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = customBaseUrl,
                        onValueChange = { customBaseUrl = it },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(textColor = MaterialTheme.colors.onBackground),
                        placeholder = {
                            Text(
                                text = "https://mobile-screens.nami.surf/divkit/v${lastSelectedSdUIVersion}/precompiled_layouts",
                                style = MaterialTheme.typography.caption
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text(
                "Select starting entrypoint",
                style = MaterialTheme.typography.body1.copy(color = MaterialTheme.colors.onBackground)
            )
            Spacer(modifier = Modifier.height(4.dp))

            DropdownSelector(
                selectedItem = selectedEntryPoint,
                items = TypeStartingEntryPoint.entries,
                titleForItem = { item -> item?.title ?: "" }
            ) {
                selectedEntryPoint = it
            }


            AnimatedVisibility(isShowCustomRelativePath) {
                Spacer(modifier = Modifier.height(24.dp))
                OutlinedTextField(
                    value = customRelativePath ?: "", onValueChange = {
                        customRelativePath = it
                    }, modifier = Modifier.fillMaxWidth(), label = {
                        Text(
                            text = "Custom Relative Path",
                            style = MaterialTheme.typography.body1.copy(color = MaterialTheme.colors.onBackground)
                        )
                    },
                    colors = TextFieldDefaults.outlinedTextFieldColors(textColor = MaterialTheme.colors.onBackground)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            OutlinedTextField(
                value = language, onValueChange = {
                    language = it
                }, modifier = Modifier.fillMaxWidth(), label = {
                    Text(
                        text = "Language",
                        style = MaterialTheme.typography.body1.copy(color = MaterialTheme.colors.onBackground)
                    )
                },
                colors = TextFieldDefaults.outlinedTextFieldColors(textColor = MaterialTheme.colors.onBackground)
            )
            Text(
                text = "en: English, fr: French, ja: Japanese",
                style = MaterialTheme.typography.caption.copy(color = MaterialTheme.colors.onBackground)
            )

            Spacer(modifier = Modifier.height(24.dp))
            OutlinedTextField(
                value = countryCode, onValueChange = {
                    countryCode = it
                }, modifier = Modifier.fillMaxWidth(), label = {
                    Text(
                        text = "Country code",
                        style = MaterialTheme.typography.body1.copy(color = MaterialTheme.colors.onBackground)
                    )
                },
                colors = TextFieldDefaults.outlinedTextFieldColors(textColor = MaterialTheme.colors.onBackground)
            )
            Text(
                text = "us: United State, jp: Japan,...",
                style = MaterialTheme.typography.caption.copy(color = MaterialTheme.colors.onBackground)
            )

            Spacer(modifier = Modifier.height(24.dp))
            Text(
                "Mode",
                style = MaterialTheme.typography.body1.copy(color = MaterialTheme.colors.onBackground)
            )
            Spacer(modifier = Modifier.height(4.dp))
            DropdownSelector(
                selectedItem = appearance,
                items = NamiAppearance.entries,
                titleForItem = {
                    it?.mode ?: ""
                }) {
                appearance = it
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text(
                "Create the default room for new zone",
                style = MaterialTheme.typography.body1.copy(color = MaterialTheme.colors.onBackground)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Switch(checked = shouldCreateDefaultRoomForNewZone, onCheckedChange = {
                shouldCreateDefaultRoomForNewZone = it
            })

            Spacer(modifier = Modifier.height(48.dp))
            Button(modifier = Modifier.fillMaxWidth(), onClick = {
                sendViewIntent(
                    HomeViewIntent.InitNamiSDK(
                        sessionCode = sessionCode.takeIf { it.isNotBlank() },
                        clientID = clientID
                    )
                )
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = { expanded = !expanded })
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = titleForItem(selectedItem),
                style = MaterialTheme.typography.body1.copy(color = MaterialTheme.colors.onBackground),
                modifier = Modifier.weight(1f)
            )

            Icon(
                imageVector = Icons.Filled.ArrowDropDown,
                modifier = if (expanded) Modifier.rotate(180f) else Modifier,
                contentDescription = if (expanded) "Collapse options" else "Expand options",
                tint = MaterialTheme.colors.onBackground
            )
        }
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