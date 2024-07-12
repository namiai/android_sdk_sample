package ai.nami.demo.sdk.pairing.customizeUI

import ai.nami.demo.sdk.ui.R
import ai.nami.sdk.common.DEFAULT_PRODUCT_ID
import ai.nami.sdk.pairing.common.Utils
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition

@Composable
fun SkyNetScanDevice(productId: Int?, deviceName: String?, onBack: () -> Unit) {

    SkyNetBasePairingScreen(
        modifier = Modifier.fillMaxSize(),
        onBack = onBack,
        isShowToolbar = true,
        title = "Device setup",
        showBackConfirmation = true,
        defaultPadding = 16.dp
    ) {

        val foundDevice by remember(productId, deviceName) {
            derivedStateOf { productId != null && productId != DEFAULT_PRODUCT_ID && deviceName?.isNotEmpty() == true }
        }

        AnimatedContent(targetState = foundDevice, label = "") { isFoundDevice ->
            if (isFoundDevice) {
                SkyNetFoundDevice(
                    productId = productId!!,
                    deviceName = deviceName!!,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                SkyNetScanningDevice(modifier = Modifier.fillMaxSize())
            }
        }

    }
}

@Composable
private fun SkyNetScanningDevice(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {

        Text(
            text = "Searching for device",
            style = MaterialTheme.typography.body1
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Please hold ...",
            style = MaterialTheme.typography.body1
        )
        Spacer(modifier = Modifier.height(24.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(4 / 3f), contentAlignment = Alignment.Center
        ) {
            val animation by rememberLottieComposition(
                LottieCompositionSpec.RawRes(
                    R.raw.skynet_scanning_device
                )
            )
            LottieAnimation(animation, iterations = LottieConstants.IterateForever)
        }
    }
}


@Preview
@Composable
private fun SkyNetScanningDevicePreview() {
    SkyNetScanningDevice(modifier = Modifier.fillMaxSize())
}


@Composable
fun SkyNetFoundDevice(
    modifier: Modifier = Modifier,
    productId: Int,
    deviceName: String
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "$deviceName found!",
            style = MaterialTheme.typography.body1
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Box(
                modifier = Modifier
                    .size(30.dp), contentAlignment = Alignment.Center
            ) {
                val animation by rememberLottieComposition(
                    LottieCompositionSpec.RawRes(
                        R.raw.skynet_setup
                    )
                )
                LottieAnimation(animation, iterations = LottieConstants.IterateForever)
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Setting up this device ...",
                style = MaterialTheme.typography.body1
            )
        }
        Spacer(modifier = Modifier.height(24.dp))

        val deviceIconId = Utils.findDeviceIconBy(productId = productId)
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Image(
                painter = painterResource(id = deviceIconId),
                contentDescription = "Device Icon",
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .aspectRatio(1f),
            )
        }
    }
}

@Preview
@Composable
private fun SkyNetFoundDevicePreview() {
    SkyNetFoundDevice(
        modifier = Modifier.fillMaxSize(),
        productId = 30,
        deviceName = "Nami WIDAR Sensor"
    )
}