package boardgame_tracker_desktop.initial_interface_UI.UITheme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
private val PencilColorScheme = lightColorScheme(
    background = PaperWhite,
    surface = PaperWhite,
    primary = PencilDark,
    secondary = PencilLight,
    error = PencilRed,
    tertiary = PencilGreen,
    secondaryContainer = PencilYellow,

    onBackground = PencilDark,
    onSurface = PencilDark,
    onError = PaperWhite
)

@Composable
fun SeaBattleTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = PencilColorScheme,
        typography = AppTypography,
        content = content
    )
}
