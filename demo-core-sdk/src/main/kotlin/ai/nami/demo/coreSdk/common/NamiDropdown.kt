package ai.nami.demo.coreSdk.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun NamiDropdown(
    modifier: Modifier = Modifier,
    currentValue: String,
    listTitles: List<String>,
    onSelectItem: (index: Int) -> Unit
) {
    var expanded by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = modifier.border(
            width = 1.dp,
            color = Color.LightGray,
            shape = MaterialTheme.shapes.medium
        )
    ) {
        Text(
            text = currentValue,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = { expanded = true })
                .padding(16.dp)
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            listTitles.forEachIndexed { index, title ->
                DropdownMenuItem(onClick = {
                    onSelectItem(index)
                    expanded = false
                }) {
                    Text(text = title)
                }
            }
        }
    }

}


@Preview
@Composable
fun DropdownPreview() {
    val listTitle = listOf("Wifi sensor", "Contact sensor", "WiDar")
    var currentTitle by remember {
        mutableStateOf(listTitle.first())
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
            .padding(24.dp)
    ) {
        NamiDropdown(currentValue = currentTitle, listTitles = listTitle, onSelectItem = {
            currentTitle = listTitle[it]
        })
    }
}