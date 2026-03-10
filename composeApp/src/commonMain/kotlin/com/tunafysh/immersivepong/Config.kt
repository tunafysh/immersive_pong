package com.tunafysh.immersivepong

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

enum class Difficulty { EASY, MEDIUM, HARD }
enum class Role { CLIENT, SERVER }
enum class SingleplayerMode { LOCAL, AI }

class SingleplayerConfig(
    mode: SingleplayerMode = SingleplayerMode.LOCAL,
    difficulty: Difficulty = Difficulty.EASY
) {
    var mode by mutableStateOf(mode)
    var difficulty by mutableStateOf(difficulty)

    val isAI get() = mode == SingleplayerMode.AI
}

class MultiplayerConfig(
    role: Role = Role.CLIENT,
    link: String = ""
) {
    var role by mutableStateOf(role)
    var link by mutableStateOf(link)
}

class RendererConfig(
    isMultiplayerInitial: Boolean = false
) {
    var isMultiplayer by mutableStateOf(isMultiplayerInitial)
    val singleplayer = SingleplayerConfig()
    val multiplayer = MultiplayerConfig()
}
