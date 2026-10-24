package com.example.ch_test_paparazzi.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

val LightThemeColors = lightColorScheme(
    primary = Color(0xFF6200EE),    // Deep Purple
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onBackground = Color.Black,
    onSurface = Color.Black
)

val DarkThemeColors = darkColorScheme(
    primary = Color(0xFFBB86FC),     // Vivid Purple
    background = Color(0xFF121212),  // Near Black
    surface = Color(0xFF212121),     // Dark Gray
    onPrimary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White
)

val CustomThemeColors = lightColorScheme(
    primary = Color(0xFFFF4081),     // Vibrant pink
    background = Color(0xFFE0F7FA),  // Light Cyan
    surface = Color(0xFFFFF9C4),     // Light yellow
    onPrimary = Color.White,
    onBackground = Color(0xFF006064), // Dark Cyan
    onSurface = Color(0xFFBB6064)
)
