package com.portfolio.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

// Elevation tokens — strictly 0dp everywhere to enforce the flat, minimalist look.
object MinimalTokens {
    val Elevation = 0.dp
    val BorderWidth = 1.dp
}

private val DarkColors = darkColorScheme(
    primary = ElectricEmerald,
    onPrimary = PureBlack,
    secondary = DeepCharcoal,
    onSecondary = PureWhite,
    background = PureBlack,
    onBackground = PureWhite,
    surface = PureBlack,
    onSurface = PureWhite,
    surfaceVariant = DeepCharcoal,
    onSurfaceVariant = PureWhite,
    outline = ElectricEmerald,
)

private val LightColors = lightColorScheme(
    primary = ElectricEmerald,
    onPrimary = PureBlack,
    secondary = DeepCharcoal,
    onSecondary = PureWhite,
    background = PureWhite,
    onBackground = PureBlack,
    surface = PureWhite,
    onSurface = PureBlack,
    surfaceVariant = PureWhite,
    onSurfaceVariant = PureBlack,
    outline = ElectricEmerald,
)

@Composable
fun PortfolioTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colors,
        typography = MaterialTheme.typography,
        content = content
    )
}

