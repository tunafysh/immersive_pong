package com.tunafysh.immersivepong

import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset

@Composable
expect fun ImmersiveMode(enabled: Boolean)

fun offsetFromFloat(value: Float) = Offset(value, value)