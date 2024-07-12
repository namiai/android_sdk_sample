package ai.nami.demo.sdk.pairing.customizeUI

import ai.nami.demo.sdk.ui.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun SkyNetBasePairingScreen(
    modifier: Modifier,
    onBack: (() -> Unit)?,
    isShowToolbar: Boolean,
    title: String?,
    showBackConfirmation: Boolean,
    defaultPadding: Dp,
    body: @Composable ColumnScope.() -> Unit
) {
    Scaffold(modifier = modifier, topBar = {
        if (isShowToolbar) {
            SkyNetToolbar(
                onBack = onBack, title = title, modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            )
        }
    }) { paddingValues ->
        Column(
            modifier = Modifier.background(color = Color.White)
                .fillMaxSize()
                .padding(paddingValues)
                .padding(start = 24.dp, end = 24.dp, top = 24.dp)
        ) {
            body()
        }
    }

}

@Composable
fun SkyNetToolbar(
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)?,
    title: String?,
) {
    Surface(
        elevation = 10.dp
    ) {
        Row(
            modifier = modifier
                .background(color = Color.White)
                .padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            if (onBack != null) {
                Image(
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = "icon back",
                    Modifier
                        .size(24.dp)
                        .clickable { onBack() },
                    colorFilter = ColorFilter.tint(color = Color(0xff2D2D2D))
                )
                Spacer(modifier = Modifier.width(12.dp))
            }
            title?.let {
                Text(
                    text = title,
                    style = MaterialTheme.typography.h6.copy(color = Color(0xff2D2D2D))
                )
            }
        }
    }

}


@Preview
@Composable
private fun SkyNetBasePairingScreenPreviewWithTopBar() {
    SkyNetBasePairingScreen(
        modifier = Modifier.fillMaxSize(),
        onBack = { /*TODO*/ },
        isShowToolbar = true,
        title = "SkyNet",
        showBackConfirmation = true,
        defaultPadding = 16.dp
    ) {
        Text(text = "Body in here")
    }
}


@Preview
@Composable
private fun SkyNetBasePairingScreenPreviewWithTopBarNoNavigationBack() {
    SkyNetBasePairingScreen(
        modifier = Modifier.fillMaxSize(),
        onBack = null,
        isShowToolbar = true,
        title = "SkyNet",
        showBackConfirmation = true,
        defaultPadding = 16.dp
    ) {
        Text(text = "Body in here")
    }
}

@Preview
@Composable
fun SkyNetBasePairingScreenPreviewWithoutTopBar() {
    SkyNetBasePairingScreen(
        modifier = Modifier.fillMaxSize(),
        onBack = { /*TODO*/ },
        isShowToolbar = false,
        title = "SkyNet",
        showBackConfirmation = true,
        defaultPadding = 16.dp
    ) {
        Text(text = "Body in here")
    }
}

