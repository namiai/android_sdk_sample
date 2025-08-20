package ai.nami.demo.sdk.pairing.cusomizeTheme.theme


import ai.nami.demo.sdk.ui.R
import ai.nami.sdk.designsystem.theme.NamiSdkShapes
import ai.nami.sdk.designsystem.theme.NamiSdkTypography
import ai.nami.sdk.designsystem.theme.colorsDarkMode
import ai.nami.sdk.designsystem.theme.colorsLightMode
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val customNamiSDKColors = colorsLightMode()

val customNamiSDKColorsDarkMode = colorsDarkMode()

val customNamiSDKShapes = NamiSdkShapes(
    buttonShape = RoundedCornerShape(16.dp),
    MessageShape = RoundedCornerShape(16.dp),
    TextFieldShape = RoundedCornerShape(4.dp)
)

val YourFonts = FontFamily(
    Font(R.font.lexend_regular, FontWeight.Normal),
    Font(R.font.lexend_medium, FontWeight.Medium),
    Font(R.font.lexend_semi_bold, FontWeight.SemiBold),
    Font(R.font.lexend_bold, FontWeight.Bold),
)

val customNamiSDKTypography = NamiSdkTypography(
    h1 = TextStyle(
        fontFamily = YourFonts,
        fontSize = 34.sp,
        fontWeight = FontWeight.Medium
    ),
    h2 = TextStyle(
        fontFamily = YourFonts,
        fontSize = 28.sp,
        fontWeight = FontWeight.Medium,
    ),
    h3 = TextStyle(
        fontFamily = YourFonts,
        fontSize = 24.sp,
        fontWeight = FontWeight.Medium,
    ),
    h4 = TextStyle(
        fontFamily = YourFonts,
        fontSize = 20.sp,
        fontWeight = FontWeight.Medium,
    ),
    h5 = TextStyle(
        fontFamily = YourFonts,
        fontSize = 17.sp,
        fontWeight = FontWeight.Medium,
    ),
    h6 = TextStyle(
        fontFamily = YourFonts,
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
    ),
    p1 = TextStyle(
        fontFamily = YourFonts,
        fontSize = 17.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 18.sp
    ),
    p2 = TextStyle(
        fontFamily = YourFonts,
        fontSize = 14.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 16.sp
    ),
    small1 = TextStyle(
        fontFamily = YourFonts,
        fontSize = 11.sp,
        fontWeight = FontWeight.Normal,

        ),
    small2 = TextStyle(
        fontFamily = YourFonts,
        fontSize = 8.sp,
        fontWeight = FontWeight.Normal,
    )
)