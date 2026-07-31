package com.nathan.selah.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val SelahColorScheme = darkColorScheme(
    primary          = Color(0xFF8CC7B8),
    onPrimary        = Color(0xFF0F0F17),
    secondary        = Color(0xFFEDD6AD),
    onSecondary      = Color(0xFF0F0F17),
    background       = Color(0xFF0F0F17),
    onBackground     = Color(0xFFEDD6AD),
    surface          = Color(0xFF1C1C26),
    onSurface        = Color(0xFFEDD6AD),
    surfaceVariant   = Color(0xFF2A2A38),
    outline          = Color(0xFF2A2A38),
)

@Composable
fun SelahTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = SelahColorScheme,
        typography  = Typography,
        content     = content
    )
}
