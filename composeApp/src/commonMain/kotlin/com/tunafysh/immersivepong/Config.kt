package com.tunafysh.immersivepong

data class GameConfig(
    val isSinglePlayer: Boolean,
    val topPaddleMode: TopPaddleMode = if (isSinglePlayer) TopPaddleMode.AI else TopPaddleMode.PLAYER
)

enum class TopPaddleMode {
    AI,
    PLAYER
}