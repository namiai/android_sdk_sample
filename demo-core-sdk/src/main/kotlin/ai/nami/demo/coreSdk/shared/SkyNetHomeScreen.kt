package ai.nami.demo.coreSdk.shared

import ai.nami.demo.coreSdk.common.SkyNetButton
import ai.nami.demo.coreSdk.common.SkyNetScaffold
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fatherofapps.jnav.annotations.JNav

@JNav(
    baseRoute = "sky_net_home_route",
    destination = "sky_net_home_destination"
)
@Composable
fun SkyNetHomeRoute(
    onNext: (isPositioning: Boolean) -> Unit,
    onBack: () -> Unit
) {
    SkyNetScaffold(title = "Home") {
        Spacer(modifier = Modifier.height(48.dp))
        SkyNetButton(text = "Pairing", onClick = { onNext(false) }, enabled = true)
        Spacer(modifier = Modifier.height(24.dp))
        SkyNetButton(text = "Positioning", onClick = { onNext(true) }, enabled = true)
    }
    BackHandler {
        onBack()
    }
}