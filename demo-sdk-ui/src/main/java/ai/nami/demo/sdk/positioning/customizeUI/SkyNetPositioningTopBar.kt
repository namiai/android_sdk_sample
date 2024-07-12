package ai.nami.demo.sdk.positioning.customizeUI

import ai.nami.demo.sdk.ui.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun SkyNetPositioningTopBar(
    modifier: Modifier = Modifier,
    isShowToolbar: Boolean = true,
    isShowBackButton: Boolean = true,
    titleScreen: String? = null,
    onNavigationBack: () -> Unit
) {
    if (isShowToolbar) {
        Surface(
            elevation = 10.dp
        ) {
            Row(
                modifier = modifier.fillMaxWidth().height(56.dp)
                    .background(color = Color.White)
                    .padding(horizontal = 24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                if (isShowBackButton) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_back),
                        contentDescription = "icon back",
                        Modifier
                            .size(24.dp)
                            .clickable { onNavigationBack() },
                        colorFilter = ColorFilter.tint(color = Color(0xff2D2D2D))
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                }
                titleScreen?.let {
                    Text(
                        text = titleScreen,
                        style = MaterialTheme.typography.h6.copy(color = Color(0xff2D2D2D))
                    )
                }
            }
        }
    }
}