package ai.nami.demo.coreSdk.common

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SkyNetButton(
    modifier: Modifier = Modifier,
    text:String,
    onClick: () -> Unit,
    enabled:Boolean
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        contentPadding = PaddingValues(vertical = 14.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.h6
        )
    }
}