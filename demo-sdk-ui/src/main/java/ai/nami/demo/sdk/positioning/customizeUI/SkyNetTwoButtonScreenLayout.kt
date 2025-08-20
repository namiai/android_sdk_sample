package ai.nami.demo.sdk.positioning.customizeUI

import ai.nami.sdk.designsystem.component.NamiPrimaryButton
import ai.nami.sdk.designsystem.component.NamiPrimaryButtonData
import ai.nami.sdk.designsystem.component.NamiTertiaryButton
import ai.nami.sdk.designsystem.component.NamiTertiaryButtonData
import ai.nami.sdk.designsystem.component.NamiTopBarData
import ai.nami.sdk.designsystem.component.primaryButtonData
import ai.nami.sdk.designsystem.component.tertiaryButtonData
import ai.nami.sdk.designsystem.component.topBarData
import ai.nami.sdk.designsystem.theme.NamiSdkTheme
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SkyNetTwoButtonScreenLayout(
    isShowLoading: Boolean = false,
    topBar: NamiTopBarData.Builder.() -> Unit,
    primaryButton: NamiPrimaryButtonData.Builder.() -> Unit,
    tertiaryButton: NamiTertiaryButtonData.Builder.() -> Unit,
    body: @Composable () -> Unit
) {
    val topBarData = topBarData(topBar)
    val primaryButtonData = primaryButtonData(primaryButton)
    val borderColorButton =
        primaryButtonData.borderColor ?: NamiSdkTheme.colors.borderDefaultPrimary
    val tertiaryButtonData = tertiaryButtonData(tertiaryButton)

    SkyNetScreenLayout(isShowLoading = isShowLoading, topBar = {
        SkyNetPositioningTopBar(
            modifier = topBarData.modifier,
            titleScreen = topBarData.titleScreen,
            isShowBackButton = topBarData.isShowBackButton,
            isShowToolbar = topBarData.isShowToolbar,
            onNavigationBack = { topBarData.onNavigationBack() }
        )
    }, body = body, buttons = { Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        NamiPrimaryButton(
            onClick = primaryButtonData.onClick,
            text = primaryButtonData.text,
            isEnabled = primaryButtonData.isEnabled,
            shape = primaryButtonData.shape ?: NamiSdkTheme.shapes.buttonShape,
            borderColor = borderColorButton,
            border = primaryButtonData.border ?: BorderStroke(
                width = 1.dp,
                color = borderColorButton
            ),
            backgroundColor = primaryButtonData.backgroundColor
                ?: NamiSdkTheme.colors.backgroundDefaultPrimary,
            textColor = primaryButtonData.textColor ?: NamiSdkTheme.colors.textDefaultPrimary,
            rippleColor = primaryButtonData.rippleColor ?: NamiSdkTheme.colors.textDefaultPrimary,
            interactionSource = primaryButtonData.interactionSource
                ?: remember { MutableInteractionSource() },
            modifier = primaryButtonData.modifier.then(
                Modifier.fillMaxWidth(),
            )
        )
        Spacer(modifier = Modifier.height(NamiSdkTheme.spaces.S24))
        NamiTertiaryButton(
            text = tertiaryButtonData.text,
            onClick = tertiaryButtonData.onClick,
            modifier = tertiaryButtonData.modifier,
            isEnable = tertiaryButtonData.isEnable
        )
    }
    })

}