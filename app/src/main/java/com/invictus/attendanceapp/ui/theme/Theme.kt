package com.invictus.attendanceapp.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Impeccable Kinpaku Dark Color Scheme (Default)
private val DarkColorScheme = darkColorScheme(
    primary = KinpakuGold,
    onPrimary = LacquerDeep,
    primaryContainer = KinpakuDeep,
    onPrimaryContainer = KinpakuPale,
    secondary = PatinaTeal,
    onSecondary = LacquerDeep,
    secondaryContainer = PatinaDeep,
    onSecondaryContainer = PatinaPale,
    tertiary = KinpakuRich,
    error = VermilionRed,
    background = LacquerDark,
    onBackground = ChampagneText,
    surface = LacquerRaised,
    onSurface = TextPrimary,
    surfaceVariant = GraphiteSurface,
    onSurfaceVariant = TextMuted,
    outline = RuleDivider
)

// Impeccable Kinpaku Light Color Scheme
private val LightColorScheme = lightColorScheme(
    primary = KinpakuDeep,
    onPrimary = ChampagneText,
    primaryContainer = KinpakuPale,
    onPrimaryContainer = LacquerDeep,
    secondary = PatinaDeep,
    onSecondary = ChampagneText,
    secondaryContainer = PatinaPale,
    onSecondaryContainer = LacquerDeep,
    tertiary = KinpakuRich,
    error = VermilionRed,
    background = ChampagneText,
    onBackground = LacquerDark,
    surface = ChampagneText,
    onSurface = LacquerDark,
    surfaceVariant = GraphiteSurface,
    onSurfaceVariant = TextMuted,
    outline = RuleDivider
)

@Composable
fun AttendanceAppTheme(
    darkTheme: Boolean = true, // Default to Impeccable dark theme
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}