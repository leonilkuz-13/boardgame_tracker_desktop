package boardgame_tracker_desktop.initial_interface_UI.UITheme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

expect val HandwritingFontFamily: FontFamily

val AppTypography = Typography(
    headlineLarge = TextStyle(
        fontFamily = HandwritingFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = HandwritingFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp
    ),
    labelSmall = TextStyle(
        fontFamily = HandwritingFontFamily,
        fontWeight = FontWeight.Light,
        fontSize = 12.sp
    )
)
