package ai.nami.demo.sdk.pairing.customizeUI


import ai.nami.demo.sdk.ui.theme.NamiSDKSampleTheme
import ai.nami.sdk.customizePairingLayout
import ai.nami.sdk.routing.onPairingRouteChangeListener
import ai.nami.sdk.routing.onPositioningRouteChangeListener
import ai.nami.sdk.routing.pairing.ui.screens.scandevice.ScanDeviceNavigation
import ai.nami.sdk.routing.pairing.ui.screens.scanqrcode.ScanQRCodeNavigation
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier

class CustomizeUIActivity: ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        customizePairingLayout {
            basePairingScreen { modifier, onBack, isShowToolbar, title, showBackConfirmation, defaultPadding, body ->
                SkyNetBasePairingScreen(
                    modifier = modifier,
                    onBack = onBack,
                    isShowToolbar = isShowToolbar,
                    title = title,
                    showBackConfirmation = showBackConfirmation,
                    defaultPadding = defaultPadding,
                    body = body
                )
            }


            scanDeviceScreen { productId, deviceName, onBack, _ ->
                SkyNetScanDevice(productId = productId, deviceName = deviceName, onBack = onBack)
            }

            renamingDeviceScreen { productId, deviceName ->
                SkyNetRenamingDevice(productId = productId, deviceName = deviceName)
            }
        }

        onPairingRouteChangeListener { data, parameter ->
            if (data.currentDestination == ScanQRCodeNavigation.destination && data.nextDestination == ScanDeviceNavigation.destination) {
                val startedScreenName = parameter?.get("from")
                SkyNetPairingGuideNavigation.createRoute(startedScreenName)
            } else {
                null
            }
        }

        onPositioningRouteChangeListener { data, parameter ->
            null
        }

        setContent {
            NamiSDKSampleTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    SkyNetHostScreen()
                }
            }
        }


    }


}