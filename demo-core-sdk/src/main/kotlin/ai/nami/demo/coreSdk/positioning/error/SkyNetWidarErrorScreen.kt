package ai.nami.demo.coreSdk.positioning.error

import ai.nami.demo.coreSdk.pairing.error.SkyNetPairingErrorScreen
import ai.nami.sdk.positioning.model.PositioningErrorCode
import androidx.compose.runtime.Composable
import com.fatherofapps.jnav.annotations.JNav

@JNav(
    baseRoute = "sky_net_widar_error_route",
    destination = "sky_net_widar_error_destination",
    name = "SkyNetWidarErrorNavigation"
)
@Composable
fun SkyNetWidarErrorRoute(
    errorCode: PositioningErrorCode,
    onTryAgain: () -> Unit,
    onExit: () -> Unit
) {

    SkyNetPairingErrorScreen(
        primaryButtonText = "Try Again",
        onPrimaryButtonClick = { onTryAgain() },
        header = "Error occurred",
        messages = listOf(errorCode.toErrorMessage()),
        secondaryButtonText = "Cancel",
        onSecondaryButtonClick = { onExit() }
    )

}

private fun PositioningErrorCode.toErrorMessage(): String {
    return when (this) {
        PositioningErrorCode.IncorrectStep -> "Wrong step"
        else -> "Please unplug the device and reconnect the device to power again."
    }
}