package demo_shared.home

import demo_shared.common.BaseScreen
import ai.nami.sdk.model.Device
import ai.nami.sdk.model.Zone
import ai.nami.sdk_ui_extensions.config.NamiAppearance
import android.content.ClipData
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.Button
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

enum class SelectEntityType(val title: String) {
    SelectZone("Select Zone"),
    SelectDevice("Select Device")
    ;
}

enum class TypeStartingEntryPoint(val title: String) {
    StartingSetupAKit("Start set up a kit"),
    StartingSetupASingleDevice("Start setup a single device"),
    Settings("Settings"),
    SettingsWithEntity("Settings with entity"),
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
        countryCode: String,
        entityId: String?,
    ) -> Unit,
    viewModel: HomeViewModel,
    onNavigateSessionCodeScreen: () -> Unit
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val clipboardManager = LocalClipboard.current

    val viewIntentChannel = remember {
        Channel<HomeViewIntent>(Channel.UNLIMITED)
    }

    LaunchedEffect(key1 = Unit) {
        withContext(Dispatchers.Main.immediate) {
            viewIntentChannel.consumeAsFlow().onEach(viewModel::handleViewIntent).collect {}
        }
    }

    LaunchedEffect(uiState.isNeedASessionCode) {
        if(uiState.isNeedASessionCode == true){
            onNavigateSessionCodeScreen()
        }
    }

    val sendViewIntent: (HomeViewIntent) -> Unit = remember {
        { viewIntent -> viewIntentChannel.trySend(viewIntent) }
    }

    var clientID by rememberSaveable {
        mutableStateOf("")
    }

    var lastSyncedClientID by remember {
        mutableStateOf<String?>(null)
    }

    LaunchedEffect(uiState.clientID) {
        if (clientID.isEmpty() || clientID == lastSyncedClientID.orEmpty()) {
            clientID = uiState.clientID
        }
        lastSyncedClientID = uiState.clientID
    }

    var appearance by remember {
        mutableStateOf(NamiAppearance.Light)
    }

    // null represents the "Custom baseUrl" option
    val sdUIVersions = listOf<String?>("0.14.0", "0.13.0", "0.12.0", "0.11.0", null)

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

    val canUseSavedSession by remember(uiState.isNeedASessionCode, uiState.place) {
        derivedStateOf { uiState.isNeedASessionCode == false && uiState.place?.id != null }
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

    var selectedEntityType by rememberSaveable {
        mutableStateOf(SelectEntityType.SelectZone)
    }

    var selectedZone by remember {
        mutableStateOf<Zone?>(null)
    }

    var selectedDevice by remember {
        mutableStateOf<Device?>(null)
    }

    val zones by remember(uiState.place) {
        derivedStateOf {
            uiState.place?.zones ?: emptyList()
        }
    }

    val listDevices = uiState.listDevices

    val isEnableButton by remember(
        clientID,
        baseUrl,
        selectedEntryPoint,
        selectedZone,
        selectedDevice,
        zones,
        listDevices
    ) {
        derivedStateOf {
            if (clientID.isEmpty() || baseUrl.isEmpty()) {
                false
            } else {
                // For SettingsWithEntity, ensure a zone or device is selected
                if (selectedEntryPoint == TypeStartingEntryPoint.SettingsWithEntity) {
                    (selectedZone != null && selectedEntityType == SelectEntityType.SelectZone) ||
                            (selectedDevice != null && selectedEntityType == SelectEntityType.SelectDevice)
                } else {
                    true
                }
            }
        }
    }

    val isShowCustomRelativePath by remember(selectedEntryPoint) {
        derivedStateOf { selectedEntryPoint == TypeStartingEntryPoint.Custom }
    }
    var entityId by remember { mutableStateOf("") }
    val isShowEntityIdField by remember(selectedEntryPoint, entityId) {
        derivedStateOf { selectedEntryPoint == TypeStartingEntryPoint.SettingsWithEntity && entityId.isNotBlank() }
    }

    val isShowSelectEntityTypeDropdown by remember(selectedEntryPoint) {
        derivedStateOf { selectedEntryPoint == TypeStartingEntryPoint.SettingsWithEntity }
    }

    val isShowZoneSelector by remember(selectedEntryPoint, selectedEntityType) {
        derivedStateOf {
            selectedEntryPoint == TypeStartingEntryPoint.SettingsWithEntity &&
                    selectedEntityType == SelectEntityType.SelectZone
        }
    }

    val isShowDeviceSelector by remember(selectedEntryPoint, selectedEntityType) {
        derivedStateOf {
            selectedEntryPoint == TypeStartingEntryPoint.SettingsWithEntity &&
                    selectedEntityType == SelectEntityType.SelectDevice
        }
    }

    val isZoneListEmpty by remember(zones) {
        derivedStateOf { zones.isEmpty() }
    }

    val isDeviceListEmpty by remember(listDevices) {
        derivedStateOf { listDevices.isEmpty() }
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
                countryCode,
                entityId.takeIf { it.isNotBlank() },
            )
            sendViewIntent(HomeViewIntent.OpenedSDK)
        }
    }

    LaunchedEffect(selectedEntryPoint, isShowDeviceSelector, isShowZoneSelector) {
        if (selectedEntryPoint != TypeStartingEntryPoint.SettingsWithEntity) {
            entityId = ""
            selectedZone = null
            selectedDevice = null
        }else {
            if(isShowDeviceSelector && selectedDevice == null){
                selectedDevice = listDevices.firstOrNull()
                selectedDevice?.urn?.let {
                    entityId = it
                }
            }
            else if(isShowZoneSelector && selectedZone == null){
                selectedZone = zones.firstOrNull()
                selectedZone?.urn?.let {
                    entityId = it
                }
            }
        }
    }


    BaseScreen(isLoading = isLoading) {

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
                    text = "You are logging in to place ${uiState.place?.name}. Click Sign Out if you want to switch to another place or clear this session.",
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
            items = TypeStartingEntryPoint.entries.toList(),
            titleForItem = { item -> item?.title ?: "" }
        ) {
            selectedEntryPoint = it
        }

        AnimatedVisibility(isShowSelectEntityTypeDropdown) {
            Spacer(modifier = Modifier.height(24.dp))
            Column {
                Text(
                    "Select Entity Type",
                    style = MaterialTheme.typography.body1.copy(color = MaterialTheme.colors.onBackground)
                )
                Spacer(modifier = Modifier.height(4.dp))
                DropdownSelector(
                    selectedItem = selectedEntityType,
                    items = SelectEntityType.entries.toList(),
                    titleForItem = { item -> item?.title ?: "" }
                ) {
                    selectedEntityType = it
                    selectedZone = null
                    selectedDevice = null
                    entityId = ""
                }
            }
        }

        AnimatedVisibility(isShowZoneSelector) {
            Spacer(modifier = Modifier.height(24.dp))
            if (isZoneListEmpty) {
                Text(
                    "Your place has no zones",
                    style = MaterialTheme.typography.body1.copy(color = MaterialTheme.colors.error)
                )
            } else {
                Column {
                    Text(
                        "Select a Zone",
                        style = MaterialTheme.typography.body1.copy(color = MaterialTheme.colors.onBackground)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    DropdownSelector(
                        selectedItem = selectedZone,
                        items = zones,
                        titleForItem = { item -> item?.name ?: "" }
                    ) { zone ->
                        selectedZone = zone
                        zone.urn?.let { zoneUrn ->
                            entityId = zoneUrn
                        }
                    }
                }
            }
        }

        AnimatedVisibility(isShowDeviceSelector) {
            Spacer(modifier = Modifier.height(24.dp))
            if (isDeviceListEmpty) {
                Text(
                    "There is no devices",
                    style = MaterialTheme.typography.body1.copy(color = MaterialTheme.colors.error)
                )
            } else {
                Column {
                    Text(
                        "Select a Device",
                        style = MaterialTheme.typography.body1.copy(color = MaterialTheme.colors.onBackground)
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    DropdownSelector(
                        selectedItem = selectedDevice,
                        items = listDevices,
                        titleForItem = { item -> item?.name ?: "" }
                    ) { device ->
                        selectedDevice = device
                        entityId = device.urn
                    }
                }
            }
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

        AnimatedVisibility(isShowEntityIdField) {
            Spacer(modifier = Modifier.height(4.dp))
            Text("Entity ID / URN: $entityId", style = MaterialTheme.typography.caption)
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
            items = NamiAppearance.entries.toList(),
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
            sendViewIntent(HomeViewIntent.InitNamiSDK(clientID  = clientID) )
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