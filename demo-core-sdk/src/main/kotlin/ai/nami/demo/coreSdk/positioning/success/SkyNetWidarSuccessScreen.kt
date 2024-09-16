package ai.nami.demo.coreSdk.positioning.success

import ai.nami.demo.coreSdk.common.SkyNetButton
import ai.nami.demo.coreSdk.common.SkyNetScaffold
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fatherofapps.jnav.annotations.JNav

@JNav(
    baseRoute = "sky_net_widar_success_route",
    destination = "sky_net_widar_success_destination",
    name = "SkyNetWidarSuccessNavigation"
)
@Composable
fun SkyNetWidarSuccessRoute(
    deviceName: String,
    onDone: () -> Unit
) {
    SkyNetScaffold(title = "Position", onBack = onDone) {
        Spacer(modifier = Modifier.height(48.dp))
        Text(text = "Positioning Complete")
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = "Sensing is enabled for $deviceName")
        Spacer(modifier = Modifier.height(48.dp))
        SkyNetButton(text = "Done", onClick = onDone, enabled = true)
    }
}