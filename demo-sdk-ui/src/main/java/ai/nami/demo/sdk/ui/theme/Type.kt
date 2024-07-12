package ai.nami.demo.sdk.ui.theme


import ai.nami.demo.sdk.ui.R
import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
val SkyNetFonts = FontFamily(
    Font(R.font.lexend_regular, FontWeight.Normal),
    Font(R.font.lexend_medium, FontWeight.Medium),
    Font(R.font.lexend_semi_bold, FontWeight.SemiBold),
    Font(R.font.lexend_bold, FontWeight.Bold),
)

val Typography = Typography(
    body1 = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    h1 = TextStyle(
        fontFamily = SkyNetFonts,
        fontSize = 34.sp,
        fontWeight = FontWeight.Medium,
    ),
    h2 = TextStyle(
        fontFamily = SkyNetFonts,
        fontSize = 28.sp,
        fontWeight = FontWeight.Medium,

        ),
    h3 = TextStyle(
        fontFamily = SkyNetFonts,
        fontSize = 24.sp,
        fontWeight = FontWeight.Medium,

        ),

    h4 = TextStyle(
        fontFamily = SkyNetFonts,
        fontSize = 20.sp,
        fontWeight = FontWeight.Medium,

        ),
    h5 = TextStyle(
        fontFamily = SkyNetFonts,
        fontSize = 17.sp,
        fontWeight = FontWeight.Medium,

        ),
    h6 = TextStyle(
        fontFamily = SkyNetFonts,
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,

        ),

    )