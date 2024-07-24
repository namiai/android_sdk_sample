package ai.nami.demo.coreSdk.common

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.AlertDialog
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.DialogProperties

@Composable
fun SkyNetDialog(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit = {},
    properties: DialogProperties = DialogProperties(),
    negativeButtonText: String? = null,
    positiveButtonText: String? = null,
    onNegativeClicked: () -> Unit = {},
    onPositiveClicked: () -> Unit = {},
    title: String,
    message: String,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = modifier.fillMaxWidth(),
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.h3,
                color = MaterialTheme.colors.primary
            )
        },
        text = {
            Text(
                text = message,
                style = MaterialTheme.typography.body1.copy(
                    color = MaterialTheme.colors.primary,
                )
            )
        },
        confirmButton = {
            positiveButtonText?.let {
                DialogButton(
                    text = positiveButtonText.uppercase(),
                    onClick = { onPositiveClicked() },
                )
            }
        },
        dismissButton = {
            negativeButtonText?.let {
                DialogButton(
                    text = negativeButtonText,
                    onClick = { onNegativeClicked() },
                )
            }
        },
        properties = properties
    )
}

@Composable
private fun DialogButton(
    text: String,
    onClick: () -> Unit,
) {
    TextButton(
        onClick = onClick,
        content = {
            Text(
                text = text.uppercase(),
                style = MaterialTheme.typography.body1.copy(
                    color = MaterialTheme.colors.primary,
                )
            )
        }
    )
}