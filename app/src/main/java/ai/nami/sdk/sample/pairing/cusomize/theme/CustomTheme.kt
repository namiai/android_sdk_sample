package ai.nami.sdk.sample.pairing.cusomize.theme

import ai.nami.sdk.pairing.ui.designsystem.theme.NamiSdkColors
import ai.nami.sdk.pairing.ui.designsystem.theme.NamiSdkShapes
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

val customNamiSDKColors = NamiSdkColors(
    primary = Color(0xffa03671),
    secondary = Color(0xff725762),
    tertiary = Color(0xff7f5539),
    error = Color(0xffba1a1a),
    onError = Color(0xFFffffff),
    background = Color(0xfffffbff),
    primaryText = Color(0xff1f1a1c),
    line = Color(0xff827378),
    statusBarColor = Color(0xfffdd9e7),
    toolbarColor = Color(0xffffd8e7)
)

val customNamiSDKShapes = NamiSdkShapes(
    ButtonShape = RoundedCornerShape(16.dp),
    MessageShape = RoundedCornerShape(16.dp),
    TextFieldShape = RoundedCornerShape(4.dp)
)