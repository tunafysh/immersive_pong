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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerId
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.sp

@Composable
fun Renderer(isSinglePlayer: Boolean, onExit: @Composable () -> Unit) {
    ImmersiveMode(true)
    val colors = MaterialTheme.colorScheme
    val radius = 24f

    val config = remember { GameConfig(isSinglePlayer = isSinglePlayer) }

    var ballPos by remember { mutableStateOf(Offset(300f, 500f)) }
    var velocity by remember { mutableStateOf(Offset(6.5f, 7f)) }

    val trail = remember { ArrayDeque<Offset>() }
    val maxTrail = 15

    val paddleWidth = 200f
    val paddleHeight = 20f
    val paddleCornerRadius = 8f
    val paddlePaddingTop = 100f
    val paddlePaddingBottom = 100f

    var playerPaddleX by remember { mutableStateOf(300f) }
    var topPaddleX by remember { mutableStateOf(300f) }
    var playerScore by remember { mutableStateOf(0) }
    var opponentScore by remember { mutableStateOf(0) }
    var shouldExit by remember { mutableStateOf(false) }

    if (shouldExit) {
        onExit()
        return
    }

    val topPaddle = remember<Paddle>(config.topPaddleMode) {
        when (config.topPaddleMode) {
            TopPaddleMode.AI -> AIPaddle(x = 300f, width = paddleWidth, height = paddleHeight)
            TopPaddleMode.PLAYER -> PlayerPaddle(x = 300f, width = paddleWidth, height = paddleHeight)
        }
    }
    val textMeasurer = rememberTextMeasurer()

    Box(modifier = Modifier.fillMaxSize()) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(config.topPaddleMode) {
                    awaitPointerEventScope {
                        val trackedPointers = mutableMapOf<PointerId, Offset>()
                        
                        while (true) {
                            val event = awaitPointerEvent()
                            
                            event.changes.forEach { change ->
                                val currentPos = change.position
                                val previousPos = trackedPointers[change.id]
                                
                                if (change.pressed) {
                                    if (previousPos != null) {
                                        val dragAmount = currentPos - previousPos
                                        
                                        if (config.topPaddleMode == TopPaddleMode.PLAYER) {
                                            // In multiplayer, top half controls top paddle, bottom half controls bottom paddle
                                            if (currentPos.y < size.height / 2) {
                                                topPaddleX = (topPaddleX + dragAmount.x).coerceIn(0f, size.width - paddleWidth)
                                            } else {
                                                playerPaddleX = (playerPaddleX + dragAmount.x).coerceIn(0f, size.width - paddleWidth)
                                            }
                                        } else {
                                            // In single player, only control bottom paddle if touch is in bottom half
                                            if (currentPos.y >= size.height / 2) {
                                                playerPaddleX = (playerPaddleX + dragAmount.x).coerceIn(0f, size.width - paddleWidth)
                                            }
                                        }
                                    }
                                    trackedPointers[change.id] = currentPos
                                    change.consume()
                                } else {
                                    trackedPointers.remove(change.id)
                                }
                            }
                        }
                    }
                }
        ) {
        val width = size.width
        val height = size.height

        // Handle volume button input (cross-platform)
        when (volumeButtonState.value) {
            VolumeButton.UP -> playerPaddleX = (playerPaddleX - 15f).coerceIn(0f, width - paddleWidth)
            VolumeButton.DOWN -> playerPaddleX = (playerPaddleX + 15f).coerceIn(0f, width - paddleWidth)
            null -> {}
        }

        var newPos = ballPos + velocity

        // Top paddle collision with proper padding
        val topPaddleBottom = paddlePaddingTop + topPaddle.height
        if (newPos.y - radius <= topPaddleBottom && velocity.y < 0) {
            if (newPos.x >= topPaddle.x && newPos.x <= topPaddle.x + topPaddle.width) {
                velocity = Offset(velocity.x, -velocity.y)
                newPos = Offset(newPos.x, topPaddleBottom + radius)
            } else {
                // Player scores
                playerScore++
                newPos = Offset(width / 2f, height / 2f)
                velocity = Offset(6.5f, 7f)
            }
        }

        // Bottom paddle collision with proper padding
        val bottomPaddleTop = height - paddlePaddingBottom - paddleHeight
        if (newPos.y + radius >= bottomPaddleTop && velocity.y > 0) {
            if (newPos.x >= playerPaddleX && newPos.x <= playerPaddleX + paddleWidth) {
                velocity = Offset(velocity.x, -velocity.y)
                newPos = Offset(newPos.x, bottomPaddleTop - radius)
            } else {
                // Opponent scores
                opponentScore++
                newPos = Offset(width / 2f, height / 2f)
                velocity = Offset(6.5f, 7f)
            }
        }

        // Wall bounce
        if (newPos.x - radius <= 0f || newPos.x + radius >= width) {
            velocity = Offset(-velocity.x, velocity.y)
            newPos = Offset(newPos.x.coerceIn(radius, width - radius), newPos.y)
        }

        ballPos = newPos

        // Draw scoreboard in background with blocky/pixelated style
        val scoreStyle = TextStyle(
            fontSize = 100.sp,
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.Monospace,
            color = colors.onBackground.copy(alpha = 0.12f),
            letterSpacing = 8.sp
        )

        // Always show both scores, centered
        val opponentScoreText = textMeasurer.measure(opponentScore.toString(), scoreStyle)
        drawText(
            textMeasurer,
            opponentScore.toString(),
            topLeft = Offset((width - opponentScoreText.size.width) / 2f, height * 0.25f - opponentScoreText.size.height / 2f),
            style = scoreStyle
        )

        val playerScoreText = textMeasurer.measure(playerScore.toString(), scoreStyle)
        drawText(
            textMeasurer,
            playerScore.toString(),
            topLeft = Offset((width - playerScoreText.size.width) / 2f, height * 0.75f - playerScoreText.size.height / 2f),
            style = scoreStyle
        )

        // Trail rendering
        trail.addFirst(ballPos)
        if (trail.size > maxTrail) trail.removeLast()
        trail.forEachIndexed { index, pos ->
            val alpha = 1f - (index.toFloat() / maxTrail)
            drawCircle(
                color = colors.primary.copy(alpha = alpha * 0.6f),
                radius = radius * (1f - index * 0.04f),
                center = pos
            )
        }

        // Draw bottom paddle with rounded corners
        drawRoundRect(
            color = colors.secondary,
            topLeft = Offset(playerPaddleX, bottomPaddleTop),
            size = Size(paddleWidth, paddleHeight),
            cornerRadius = CornerRadius(paddleCornerRadius, paddleCornerRadius)
        )

        // Update and draw top paddle
        topPaddle.update(ballPos.x, width, topPaddleX)
        topPaddle.draw(this, colors, paddlePaddingTop, paddleCornerRadius)
    }
    
        IconButton(
            onClick = { shouldExit = true },
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
