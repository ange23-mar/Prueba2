package com.example.view.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Material Design 3: esquema de color claro/oscuro según el tema del sistema
private val LightColors = lightColorScheme(
    primary = Color(0xFF1565C0),   // compra
    secondary = Color(0xFF00897B),
    tertiary = Color(0xFFF9A825)   // solo tránsito
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF90CAF9),
    secondary = Color(0xFF80CBC4),
    tertiary = Color(0xFFFFE082)
)

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) DarkColors else LightColors,
        content = content
    )
}
