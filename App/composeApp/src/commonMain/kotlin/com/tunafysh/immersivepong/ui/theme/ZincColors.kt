package com.tunafysh.immersivepong.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme

// Light theme colors
val ZincLightColors = lightColorScheme(
    primary = Color(0xFF71717A),        // zinc500
    onPrimary = Color(0xFFFAFAFA),      // zinc50
    secondary = Color(0xFFA1A1AA),      // zinc400
    onSecondary = Color(0xFFFAFAFA),    // zinc50
    background = Color(0xFFF4F4F5),     // zinc100
    surface = Color(0xFFF4F4F5),        // zinc100
    onSurface = Color(0xFF27272A)       // zinc800
)

// Dark theme colors
val ZincDarkColors = darkColorScheme(
    primary = Color(0xFFA1A1AA),        // zinc400
    onPrimary = Color(0xFF18181B),      // zinc900
    secondary = Color(0xFF71717A),      // zinc500
    onSecondary = Color(0xFF18181B),    // zinc900
    background = Color(0xFF000000),     // zinc800
    surface = Color(0xFF3F3F46),        // zinc700
    onSurface = Color(0xFFE4E4E7)       // zinc200
)