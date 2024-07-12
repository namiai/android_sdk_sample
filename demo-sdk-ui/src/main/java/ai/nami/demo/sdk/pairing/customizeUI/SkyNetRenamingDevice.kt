package ai.nami.demo.sdk.pairing.customizeUI

import ai.nami.demo.sdk.ui.R
import ai.nami.sdk.pairing.common.Utils
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition

@Composable
fun SkyNetRenamingDevice(
    productId: Int, deviceName: String
) {

    val deviceIconId = Utils.findDeviceIconBy(productId = productId)
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Image(
                    painter = painterResource(id = deviceIconId),
                    contentDescription = "Device Icon",
                    modifier = Modifier
                        .size(30.dp),
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Connecting to $deviceName",
                style = MaterialTheme.typography.body1
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(4 / 3f), contentAlignment = Alignment.Center
        ) {
            val animation by rememberLottieComposition(
                LottieCompositionSpec.RawRes(
                    R.raw.skynet_connecting
                )
            )
            LottieAnimation(animation, iterations = LottieConstants.IterateForever)
        }


    }
}