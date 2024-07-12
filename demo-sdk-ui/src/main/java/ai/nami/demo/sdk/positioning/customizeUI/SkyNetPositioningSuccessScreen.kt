package ai.nami.demo.sdk.positioning.customizeUI

import ai.nami.sdk.designsystem.component.NamiPageTitle
import ai.nami.sdk.designsystem.theme.NamiSdkTheme
import ai.nami.sdk.positioning.ui.success.DefaultWidarPositionSuccessScreen
import ai.nami.sdk.ui.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition

@Composable
fun SkyNetPositioningSuccessScreen(
    deviceName: String,
    onDone: () -> Unit
) {
    SkyNetOneButtonLayout(topBar = {
        titleScreen = "Positioning device"
        isShowBackButton = false
    }, primaryButton = {
        text = "Finish"
        isEnabled = true
        onClick = onDone
    }) {

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Image(
                painter = painterResource(id = ai.nami.demo.sdk.ui.R.drawable.ic_finish_positioning),
                contentDescription = "",
                colorFilter = ColorFilter.tint(color = Color.Green),
                modifier = Modifier.size(80.dp),
            )
            Spacer(modifier = Modifier.height(NamiSdkTheme.spaces.S16))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(4 / 3f)
            ) {
                val animation by rememberLottieComposition(
                    LottieCompositionSpec.RawRes(
                        R.raw.widar_animation_done
                    )
                )
                LottieAnimation(animation, iterations = LottieConstants.IterateForever)
            }
            Spacer(modifier = Modifier.height(NamiSdkTheme.spaces.S16))
            NamiPageTitle(title = "Position Complete")
            Spacer(modifier = Modifier.height(NamiSdkTheme.spaces.S8))
            Text(
                text = "Sensing is enable for $deviceName",
                style = NamiSdkTheme.typography.p1.copy(color = NamiSdkTheme.colors.primaryText)
            )
        }
    }
}

@Preview
@Composable
fun PreviewWidarPositionSuccessScreen() {
    SkyNetPositioningSuccessScreen(deviceName = "Widar Demo") {

    }
}