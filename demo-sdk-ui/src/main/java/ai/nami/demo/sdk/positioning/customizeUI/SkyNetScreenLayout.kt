package ai.nami.demo.sdk.positioning.customizeUI

import ai.nami.sdk.designsystem.component.NamiFullLoadingScreen
import ai.nami.sdk.designsystem.theme.NamiSdkTheme
import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun SkyNetScreenLayout(
    modifier: Modifier = Modifier,
    isShowLoading: Boolean = false,
    topBar: @Composable () -> Unit,
    body: @Composable () -> Unit,
    buttons: @Composable () -> Unit,
) {
    val scrollState = rememberScrollState()
    Box(modifier = modifier.fillMaxSize()) {
        Scaffold(modifier = Modifier.fillMaxSize(), topBar = topBar) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.White)
                    .padding(horizontal = 24.dp)
                    .verticalScroll(state = scrollState)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    body.invoke()
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                ) {
                    buttons.invoke()
                }
                Spacer(modifier = Modifier.height(56.dp))
            }
        }
        if (isShowLoading) {
            NamiFullLoadingScreen(modifier = Modifier.fillMaxSize())
        }
    }
}