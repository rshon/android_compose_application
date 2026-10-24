package com.example.ch_test_paparazzi.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

enum class ThemeType {
    LIGHT, DARK, CUSTOM
}

@Composable
fun AppTheme(themeType: ThemeType, content: @Composable () -> Unit) {
    val colors = when (themeType) {
        ThemeType.LIGHT -> LightThemeColors
        ThemeType.DARK -> DarkThemeColors
        ThemeType.CUSTOM -> CustomThemeColors
    }

    val typography = when (themeType) {
        ThemeType.LIGHT -> LightTypography
        ThemeType.DARK -> DarkTypography
        ThemeType.CUSTOM -> CustomTypography
    }

    val shapes = when (themeType) {
        ThemeType.LIGHT -> LightShapes
        ThemeType.DARK -> DarkShapes
        ThemeType.CUSTOM -> CustomShapes
    }

    MaterialTheme(
        colorScheme = colors,
        typography = typography,
        shapes = shapes,
        content = content
    )
}
