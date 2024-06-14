package ai.nami.sdk.sample.xzing

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.journeyapps.barcodescanner.CaptureManager
import com.journeyapps.barcodescanner.CompoundBarcodeView

@Composable
fun ZXingScanQRCode(
    modifier: Modifier = Modifier,
    onScanQRCodeSuccess: (result: String) -> Unit,
) {
    var scanFlag by remember { mutableStateOf(false) }

    AndroidView(factory = { context ->
        val preview = CompoundBarcodeView(context)
        preview.setStatusText("")
        preview.cameraSettings.isAutoTorchEnabled = false
        preview.apply {
            val capture = CaptureManager(context as Activity, this)
            capture.initializeFromIntent(context.intent, null)
            capture.decode()
            this.decodeContinuous { result ->
                if (scanFlag) {
                    return@decodeContinuous
                }
                scanFlag = true
                result.text?.let { barCodeOrQr ->
                    scanFlag = true
                    onScanQRCodeSuccess(barCodeOrQr)
                }
            }
            this.resume()
        }
    }, modifier = modifier)
}