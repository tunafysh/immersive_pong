package com.tunafysh.immersivepong

import androidx.compose.runtime.mutableStateOf

enum class VolumeButton {
    UP, DOWN
}

val volumeButtonState = mutableStateOf<VolumeButton?>(null)

fun setVolumeButtonPressed(button: VolumeButton) {
    volumeButtonState.value = button
}

fun clearVolumeButtonState() {
    volumeButtonState.value = null
}
