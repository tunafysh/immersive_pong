package com.tunafysh.immersivepong

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp

@Composable
fun Renderer(config: RendererConfig?, onExit: @Composable (() -> Unit)) {
    ImmersiveMode(true)

    val state = rememberGameState(config)

    if (state.shouldExit) {
        onExit()
        return
    }

    val colors = MaterialTheme.colorScheme
    val textMeasurer = rememberTextMeasurer()

    Box(modifier = Modifier.fillMaxSize()) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .gamePointerInput(
                    isMultiplayer = state.isMultiplayer,
                    isAIMode = false,
                    getTopPaddleX = { state.topPaddleX },
                    getPlayerPaddleX = { state.playerPaddleX },
                    onTopPaddleMove = { state.topPaddleX = it },
                    onPlayerPaddleMove = { state.playerPaddleX = it },
                )
        ) {
            val width = size.width
            val height = size.height

            // Handle volume button input
            when (volumeButtonState.value) {
                VolumeButton.UP -> state.playerPaddleX = (state.playerPaddleX - VOLUME_BUTTON_SPEED).coerceIn(0f, width - PADDLE_WIDTH)
                VolumeButton.DOWN -> state.playerPaddleX = (state.playerPaddleX + VOLUME_BUTTON_SPEED).coerceIn(0f, width - PADDLE_WIDTH)
                null -> {}
            }

            // Update physics
            val physics = updateBallPhysics(state.ballPos, state.velocity, width, height, state.topPaddle, state.playerPaddleX)
            state.ballPos = physics.position
            state.velocity = physics.velocity
            state.playerScore += physics.playerScoreDelta
            state.opponentScore += physics.opponentScoreDelta

            // Update trail
            state.trail.addFirst(state.ballPos)
            if (state.trail.size > TRAIL_MAX_SIZE) state.trail.removeLast()

            // Draw game elements
            drawScoreboard(textMeasurer, state.playerScore, state.opponentScore, colors)
            drawTrail(state.trail, colors)
            drawBottomPaddle(state.playerPaddleX, colors)

            state.topPaddle.update(state.ballPos.x, width, state.topPaddleX)
            state.topPaddle.draw(this, colors, PADDLE_PADDING_TOP, PADDLE_CORNER_RADIUS)
        }

        IconButton(
            onClick = { state.shouldExit = true },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = colors.onBackground
            )
        }
    }
}
