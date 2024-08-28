package ai.nami.demo.coreSdk.pairing.error

import ai.nami.demo.coreSdk.common.SkyNetButton
import ai.nami.demo.coreSdk.common.SkyNetScaffold
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun SkyNetPairingErrorScreen(
    isLoading: Boolean = false,
    header: String? = null,
    messages: List<String> = emptyList(),
    primaryButtonText: String? = null,
    secondaryButtonText: String? = null,
    onPrimaryButtonClick: (() -> Unit)? = null,
    onSecondaryButtonClick: (() -> Unit)? = null,
) {
    val showHeader by remember(header) {
        derivedStateOf { !header.isNullOrBlank() }
    }
    val showMessage by remember(messages) {
        derivedStateOf { messages.isNotEmpty() }
    }
    val showPrimaryButton by remember(primaryButtonText) {
        derivedStateOf { !primaryButtonText.isNullOrBlank() }
    }
    val showSecondaryButton by remember(secondaryButtonText) {
        derivedStateOf { !secondaryButtonText.isNullOrBlank() }
    }

    SkyNetScaffold(title = "Error", onBack = {}, isLoading = isLoading) {
        Spacer(modifier = Modifier.height(48.dp))
        if (showHeader) {
            Text(text = header ?: "")
            Spacer(modifier = Modifier.height(12.dp))
        }
        if (showMessage) {
            messages.map {
                Text(text = it)
                Spacer(modifier = Modifier.height(2.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
        if (showPrimaryButton) {
            SkyNetButton(
                text = primaryButtonText ?: "",
                onClick = { onPrimaryButtonClick?.invoke() },
                enabled = true
            )
            Spacer(modifier = Modifier.height(24.dp))
        }
        if (showSecondaryButton) {
            SkyNetButton(
                text = secondaryButtonText ?: "",
                onClick = { onSecondaryButtonClick?.invoke() },
                enabled = true
            )
            Spacer(modifier = Modifier.height(24.dp))
        }
    }


}