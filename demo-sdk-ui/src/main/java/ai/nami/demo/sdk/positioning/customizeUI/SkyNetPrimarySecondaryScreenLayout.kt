package ai.nami.demo.sdk.positioning.customizeUI

import ai.nami.sdk.designsystem.component.NamiPrimaryButton
import ai.nami.sdk.designsystem.component.NamiPrimaryButtonData
import ai.nami.sdk.designsystem.component.NamiTopBarData
import ai.nami.sdk.designsystem.component.primaryButtonData
import ai.nami.sdk.designsystem.component.topBarData
import ai.nami.sdk.designsystem.theme.NamiSdkTheme
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SkyNetPrimarySecondaryScreenLayout(
    topBar: NamiTopBarData.Builder.() -> Unit,
    primaryButton: NamiPrimaryButtonData.Builder.() -> Unit,
    secondaryButton: NamiPrimaryButtonData.Builder.() -> Unit,
    body: @Composable () -> Unit
) {
    val topBarData = topBarData(topBar)
    val primaryButtonData = primaryButtonData(primaryButton)
    val secondaryButtonData = primaryButtonData(secondaryButton)
    val primaryBorderColorButton =
        primaryButtonData.borderColor ?: NamiSdkTheme.colors.borderDefaultPrimary
    val secondaryBorderColorButton =
        secondaryButtonData.borderColor ?: NamiSdkTheme.colors.borderDefaultPrimary


    SkyNetScreenLayout(topBar = {
        SkyNetPositioningTopBar(
            modifier = topBarData.modifier,
            titleScreen = topBarData.titleScreen,
            isShowBackButton = topBarData.isShowBackButton,
            isShowToolbar = topBarData.isShowToolbar,
            onNavigationBack = { topBarData.onNavigationBack() }
        )
    }, body = body, buttons = {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NamiPrimaryButton(
                onClick = secondaryButtonData.onClick,
                text = secondaryButtonData.text,
                isEnabled = secondaryButtonData.isEnabled,
                shape = secondaryButtonData.shape ?: NamiSdkTheme.shapes.buttonShape,
                borderColor = secondaryBorderColorButton,
                border = secondaryButtonData.border ?: BorderStroke(
                    width = 1.dp,
                    color = secondaryBorderColorButton
                ),
                backgroundColor = secondaryButtonData.backgroundColor
                    ?: NamiSdkTheme.colors.backgroundDefaultPrimary,
                textColor = secondaryButtonData.textColor ?: NamiSdkTheme.colors.textDefaultPrimary,
                rippleColor = secondaryButtonData.rippleColor
                    ?: NamiSdkTheme.colors.textDefaultPrimary,
                interactionSource = secondaryButtonData.interactionSource
                    ?: remember { MutableInteractionSource() },
                modifier = secondaryButtonData.modifier.then(
                    Modifier.weight(1f),
                )
            )

            Spacer(modifier = Modifier.width(NamiSdkTheme.spaces.S8))
            NamiPrimaryButton(
                onClick = primaryButtonData.onClick,
                text = primaryButtonData.text,
                isEnabled = primaryButtonData.isEnabled,
                shape = primaryButtonData.shape ?: NamiSdkTheme.shapes.buttonShape,
                borderColor = primaryBorderColorButton,
                border = primaryButtonData.border ?: BorderStroke(
                    width = 1.dp,
                    color = primaryBorderColorButton
                ),
                backgroundColor = primaryButtonData.backgroundColor
                    ?: NamiSdkTheme.colors.backgroundDefaultPrimary,
                textColor = primaryButtonData.textColor ?: NamiSdkTheme.colors.textDefaultPrimary,
                rippleColor = primaryButtonData.rippleColor
                    ?: NamiSdkTheme.colors.textDefaultPrimary,
                interactionSource = primaryButtonData.interactionSource
                    ?: remember { MutableInteractionSource() },
                modifier = primaryButtonData.modifier.then(
                    Modifier.weight(1f),
                )
            )
        }
    })
}