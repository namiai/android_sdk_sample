package ai.nami.demo.coreSdk.shared

import ai.nami.demo.coreSdk.common.NamiDropdown
import ai.nami.demo.coreSdk.common.SkyNetButton
import ai.nami.demo.coreSdk.common.SkyNetScaffold
import ai.nami.sdk.NamiSDK
import ai.nami.sdk.common.NamiLog
import ai.nami.sdk.model.DeviceCategory
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
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
    baseRoute = "sky_net_info_route",
    destination = "sky_net_info_destination",
    name = "SkyNetInfoNavigation",
    arguments = [
        JNavArg(
            name = "isOpenForPositioning",
            type = Boolean::class,
        )
    ]
)
@Composable
fun SkyNetInfoRoute(
    isOpenForPositioning: Boolean,
    onNext: (roomId: String, DeviceCategory, deviceUrn: String?) -> Unit,
    onBack: () -> Unit,
    viewModel: SkyNetInfoViewModel
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val viewIntentChannel = remember {
        Channel<SkyNetInfoViewIntent>(Channel.UNLIMITED)
    }

    LaunchedEffect(key1 = Unit) {
        withContext(Dispatchers.Main.immediate) {
            viewIntentChannel.consumeAsFlow().onEach(viewModel::handleViewIntent).collect()
        }
    }

    val sendViewIntent: (SkyNetInfoViewIntent) -> Unit = remember {
        { viewIntent -> viewIntentChannel.trySend(viewIntent) }
    }


    val isLoading by remember(uiState.isLoading) {
        derivedStateOf { uiState.isLoading }
    }

    val errorMessage by remember(uiState.errorMessage) {
        derivedStateOf { uiState.errorMessage }
    }

    SkyeNetInfoScreen(
        onNext = onNext,
        onBack = onBack,
        sendViewIntent = sendViewIntent,
        initSDKSuccess = uiState.initSDKSuccess,
        errorMessage = errorMessage,
        isLoading = isLoading,
        isOpenForPositioning = isOpenForPositioning
    )
}


@Composable
private fun SkyeNetInfoScreen(
    onNext: (roomId: String, DeviceCategory, String?) -> Unit,
    onBack: () -> Unit,
    sendViewIntent: (SkyNetInfoViewIntent) -> Unit,
    initSDKSuccess: Boolean?,
    errorMessage: String?,
    isLoading: Boolean,
    isOpenForPositioning: Boolean,
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

    val isNeedASessionCode by remember {
        mutableStateOf(NamiSDK.shouldInit())
    }

    var deviceUrn by remember {
        mutableStateOf("")
    }

    LaunchedEffect(key1 = initSDKSuccess) {
        if (initSDKSuccess == true) {
            onNext(roomId, currentCategory, deviceUrn)
        }
    }

    val isShowError by remember(errorMessage) {
        derivedStateOf { !errorMessage.isNullOrEmpty() }
    }

    SkyNetScaffold(title = "Info", onBack = onBack, isLoading = isLoading) {
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
        if (!isNeedASessionCode) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = "No need a new session code. You can leave this field empty")
        }
        AnimatedVisibility(visible = isShowError) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = errorMessage!!,
                style = MaterialTheme.typography.body1.copy(color = MaterialTheme.colors.error)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(
            value = roomId,
            onValueChange = { roomId = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = "Room UID") },
            keyboardOptions = KeyboardOptions(
                imeAction = if (isOpenForPositioning) ImeAction.Next else ImeAction.Done
            )
        )

        Spacer(modifier = Modifier.height(24.dp))
        if (isOpenForPositioning) {
            OutlinedTextField(
                value = deviceUrn,
                onValueChange = { deviceUrn = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = "Device's urn") },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done
                )
            )
        } else {
            NamiDropdown(
                currentValue = currentCategory.categoryName,
                listTitles = listDeviceCategories.map { it.categoryName },
                onSelectItem = {
                    currentCategory = listDeviceCategories[it]
                },
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        SkyNetButton(
            modifier = Modifier.fillMaxWidth(),
            text = "Next",
            onClick = {
                sendViewIntent(SkyNetInfoViewIntent.InitNamiSDK(sessionCode))
            },
            enabled = isEnableButton,
        )

    }
}