package com.tunafysh.immersivepong

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset

class GameState(
    val isMultiplayer: Boolean,
    isAIMode: Boolean,
) {
    var ballPos by mutableStateOf(Offset(300f, 500f))
    var velocity by mutableStateOf(Offset(INITIAL_VELOCITY_X, INITIAL_VELOCITY_Y))
    val trail = ArrayDeque<Offset>()

    var playerPaddleX by mutableStateOf(300f)
    var topPaddleX by mutableStateOf(300f)
    var playerScore by mutableStateOf(0)
    var opponentScore by mutableStateOf(0)
    var shouldExit by mutableStateOf(false)

    val topPaddle: Paddle = when {
        isAIMode -> AIPaddle(x = 300f, width = PADDLE_WIDTH, height = PADDLE_HEIGHT)
        else -> PlayerPaddle(x = 300f, width = PADDLE_WIDTH, height = PADDLE_HEIGHT)
    }
}

@Composable
fun rememberGameState(config: RendererConfig?): GameState {
    val isMultiplayer = config?.isMultiplayer == true
    val isAIMode = config?.singleplayer?.isAI == true
    return remember(isMultiplayer, isAIMode) { GameState(isMultiplayer, isAIMode) }
}
