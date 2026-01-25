package com.tunafysh.immersivepong.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.material3.Surface

@Composable
actual fun AppTheme (content: @Composable () -> Unit ){
    val darkTheme= isSystemInDarkTheme()
    val colorScheme = if (darkTheme) ZincDarkColors else ZincLightColors

    MaterialTheme(
        colorScheme = colorScheme,
        shapes = Shapes(),      // default shapes
        typography = Typography(), // default typography
    ){
        Surface {
            content()
        }
    }
}