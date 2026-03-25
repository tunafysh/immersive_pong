package com.tunafysh.immersivepong.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Surface
import androidx.compose.material3.Typography
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun AppTheme (content: @Composable () -> Unit ){
    val darkTheme= isSystemInDarkTheme()
    val context = LocalContext.current

    val colorScheme: ColorScheme = if (Build.VERSION.SDK_INT >=  31 ) {
        if (darkTheme) dynamicDarkColorScheme(context)
        else dynamicLightColorScheme(context)
    }
    else {
        if (darkTheme) ZincDarkColors else ZincLightColors
    }

    MaterialTheme(
        colorScheme = colorScheme,
        shapes = Shapes(),      // default shapes
        typography = Typography(), // default typography
    ){
        Surface (
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ){
            content()
        }
    }
}