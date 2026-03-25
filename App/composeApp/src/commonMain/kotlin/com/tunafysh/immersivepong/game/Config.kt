package com.tunafysh.immersivepong.game

enum class Difficulty {
    EASY,
    MEDIUM,
    HARD
}

sealed class SingleplayerConfig {
    data object Local : SingleplayerConfig()

    data class AI(
        val difficulty: Difficulty
    ) : SingleplayerConfig()
}

data class MultiplayerConfig(
    val ip: String,
    val port: String
)

sealed class RendererConfig {
    data class MultiplayerMode(
        val config: MultiplayerConfig
    ) : RendererConfig()

    data class SingleplayerMode(
        val config: SingleplayerConfig
    ) : RendererConfig()
}