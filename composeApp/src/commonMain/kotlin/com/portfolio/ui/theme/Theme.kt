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
    primary          = EmeraldPrimaryDark,
    onPrimary        = EmeraldOnPrimaryDark,
    primaryContainer = EmeraldContainerDark,
    secondary        = EmeraldSecondaryDark,
    onSecondary      = EmeraldOnSurfaceDark,
    background       = EmeraldBackgroundDark,
    onBackground     = EmeraldOnBackgroundDark,
    surface          = EmeraldSurfaceDark,
    onSurface        = EmeraldOnSurfaceDark,
    surfaceVariant   = EmeraldSecondaryDark,
    onSurfaceVariant = EmeraldOnSurfaceDark,
    outline          = EmeraldOutlineDark,
)

private val LightColors = lightColorScheme(
    primary          = EmeraldPrimaryLight,
    onPrimary        = EmeraldOnPrimaryLight,
    primaryContainer = EmeraldContainerLight,
    secondary        = EmeraldSecondaryLight,
    onSecondary      = EmeraldOnSurfaceLight,
    background       = EmeraldBackgroundLight,
    onBackground     = EmeraldOnBackgroundLight,
    surface          = EmeraldSurfaceLight,
    onSurface        = EmeraldOnSurfaceLight,
    surfaceVariant   = EmeraldSecondaryLight,
    onSurfaceVariant = EmeraldOnSurfaceLight,
    outline          = EmeraldOutlineLight,
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

