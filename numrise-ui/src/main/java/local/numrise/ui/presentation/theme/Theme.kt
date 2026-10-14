package local.numrise.ui.presentation.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryPink,
    secondary = SecondaryGreen,
    tertiary = TertiaryCyan,
    background = DarkBackground,
    surface = DarkSurface,
    onPrimary = LightSurface,
    onSecondary = DarkBackground,
    onTertiary = DarkBackground,
    onBackground = DarkOnBackground,
    onSurface = DarkOnSurface
)

/**
 * Numrise game theme. Intentional override of core-ui `AppTheme` (which is
 * retained as the reusable starting point for other games): same edge-to-edge
 * light-bars `SideEffect` contract, game-owned neon palette.
 */
@Composable
fun NumriseTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val context = view.context
            if (context is Activity) {
                val window = context.window
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars =
                    false
            }
        }
    }

    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
