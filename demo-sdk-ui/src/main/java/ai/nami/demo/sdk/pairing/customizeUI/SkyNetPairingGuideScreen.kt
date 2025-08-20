package ai.nami.demo.sdk.pairing.customizeUI

import ai.nami.demo.sdk.ui.R
import ai.nami.sdk.designsystem.component.NamiPrimaryButton
import ai.nami.sdk.designsystem.component.NamiTertiaryButton
import ai.nami.sdk.designsystem.theme.NamiSdkTheme
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition

object SkyNetPairingGuideNavigation {
    const val startedScreenNameArg = "startedScreenNameArg"

    private const val baseRoute = "skyNet_pairing_guide_route"

    val route = "$baseRoute?$startedScreenNameArg={$startedScreenNameArg}"

    fun createRoute(screenName: String?): String {
        return "$baseRoute?$startedScreenNameArg=$screenName"
    }

    fun arguments() = listOf(navArgument(name = startedScreenNameArg) {
        type = NavType.StringType
        nullable = true
    })

    fun startedScreenName(navBackStackEntry: NavBackStackEntry) =
        navBackStackEntry.arguments?.getString(
            startedScreenNameArg
        )
}

@Composable
fun SkyNetPairingGuideRoute(startedScreenName: String?, onNext: () -> Unit, onCancel: () -> Unit) {

    val isShowAnimation by remember(startedScreenName) {
        derivedStateOf { startedScreenName == "home" }
    }

    SkyNetPairingGuideScreen(
        isShowAnimation = isShowAnimation,
        onCancel = onCancel,
        onNext = onNext
    )
}

@Composable
private fun SkyNetPairingGuideScreen(
    onNext: () -> Unit,
    onCancel: () -> Unit,
    isShowAnimation: Boolean
) {
    SkyNetBasePairingScreen(
        modifier = Modifier.fillMaxSize(),
        onBack = onCancel,
        isShowToolbar = true,
        title = "SkyNet",
        showBackConfirmation = true,
        defaultPadding = 16.dp
    ) {
        AnimatedVisibility(visible = isShowAnimation) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(4 / 3f),
                contentAlignment = Alignment.Center
            ) {
                val animation by rememberLottieComposition(
                    LottieCompositionSpec.RawRes(
                        R.raw.skynet_home_security
                    )
                )
                LottieAnimation(animation, iterations = LottieConstants.IterateForever)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Plug your device into outlet.",
            style = NamiSdkTheme.typography.h3 .copy(
                color = NamiSdkTheme.colors.textDefaultPrimary,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(48.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            NamiPrimaryButton(onClick = onNext, text = "Next", modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(12.dp))
            NamiTertiaryButton(text = "Cancel", onClick = onCancel, modifier = Modifier.fillMaxWidth())
        }
    }
}


@Preview
@Composable
private fun SkyNetPairingGuideScreenPreview() {
    SkyNetPairingGuideScreen(isShowAnimation = true, onNext = {}, onCancel = {})
}