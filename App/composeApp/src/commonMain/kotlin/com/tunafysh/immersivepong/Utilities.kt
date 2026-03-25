package com.tunafysh.immersivepong

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset

@Composable
expect fun ImmersiveMode(enabled: Boolean)

fun offsetFromFloat(value: Float) = Offset(value, value)
fun ForceRedraw(trigger: Int) = trigger xor 1