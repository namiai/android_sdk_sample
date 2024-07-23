package ai.nami.demo.coreSdk.common

import ai.nami.demo.core.sdk.R
import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.Snackbar
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun SkyNetScaffold(
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    errorMessage: String? = null,
    title: String,
    onBack: (() -> Unit)? = null,
    body: @Composable ColumnScope.() -> Unit
) {
    val snackBarHostState = remember {
        SnackbarHostState()
    }

    val defaultErrorMessage = stringResource(id = R.string.something_went_wrong)
    val actionLabel = stringResource(id = R.string.dismiss)
    LaunchedEffect(key1 = errorMessage) {
        errorMessage?.let {
            snackBarHostState.showSnackbar(
                message = errorMessage.ifEmpty {
                    defaultErrorMessage
                },
                actionLabel = actionLabel
            )
        }
    }

    // should use colors with Theme in production app
    Scaffold(modifier = modifier, topBar = { SkyNetTopBar(title = title) }, snackbarHost = {
        SnackbarHost(hostState = snackBarHostState) { data ->
            Snackbar(
                snackbarData = data,
                backgroundColor = Color.Red,
                contentColor = Color.White
            )
        }
    }) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top
        ) {
            body()
        }
    }
    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.White.copy(alpha = 0.8f))
                .clickable { },
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(36.dp),
                color = Color.Green
            )
        }
    }

    BackHandler {
        onBack?.invoke()
    }

}


@Composable
fun SkyNetTopBar(title: String) {
    Surface(elevation = 6.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(color = Color.White),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = title)
        }
    }
}

