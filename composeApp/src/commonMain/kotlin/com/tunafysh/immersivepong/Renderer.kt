package com.tunafysh.immersivepong

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size

abstract class TopPaddle(
    var x: Float,
    val width: Float,
    val height: Float
) {
    // Update paddle logic each frame
    abstract fun update(ballX: Float, canvasWidth: Float)

    // Draw paddle
    fun Draw(drawScope: androidx.compose.ui.graphics.drawscope.DrawScope, color: ColorScheme) {
        drawScope.drawRect(
            color = color.secondary,
            topLeft = Offset(x, 0f),
            size = Size(width, height)
        )
    }
}

class AIPaddle(x: Float, width: Float, height: Float) : TopPaddle(x, width, height) {
    override fun update(ballX: Float, canvasWidth: Float) {
        // Simple tracking AI
        x += ((ballX - (x + width / 2)) * 0.1f)
        x = x.coerceIn(0f, canvasWidth - width)
    }
}

@Composable
fun Renderer(onExit: @Composable () -> Unit) {
    ImmersiveMode(true)
    val colors = MaterialTheme.colorScheme
    val radius = 24f

    // Ball state
    var ballPos by remember { mutableStateOf(Offset(300f, 500f)) }
    var velocity by remember { mutableStateOf(Offset(6.5f, 7f)) }

    // Trail history
    val trail = remember { ArrayDeque<Offset>() }
    val maxTrail = 15

    // Bottom paddle (player)
    val paddleWidth = 120f
    val paddleHeight = 20f
    var playerPaddleX by remember { mutableStateOf(300f) }

    // Top paddle (can be AI / Human / Network)
    val topPaddle = remember { AIPaddle(x = 300f, width = paddleWidth, height = paddleHeight) }

    Canvas(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val width = size.width
        val height = size.height

        // --- Update Ball ---
        var newPos = ballPos + velocity

        // Bounce Y (top/bottom)
        if (newPos.y - radius <= topPaddle.height) {
            if (newPos.x in topPaddle.x..(topPaddle.x + topPaddle.width)) {
                velocity = Offset(velocity.x, -velocity.y)
            } else {
                newPos = Offset(width / 2f, height / 2f)
            }
        }

        if (newPos.y + radius >= height - paddleHeight) {
            if (newPos.x in playerPaddleX..(playerPaddleX + paddleWidth)) {
                velocity = Offset(velocity.x, -velocity.y)
            } else {
                newPos = Offset(width / 2f, height / 2f)
            }
        }

        // Bounce X
        if (newPos.x - radius <= 0f || newPos.x + radius >= width) {
            velocity = Offset(-velocity.x, velocity.y)
        }

        ballPos = newPos

        // Update trail
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

        // Draw paddles
        drawRect(
            color = colors.secondary,
            topLeft = Offset(playerPaddleX, height - paddleHeight),
            size = androidx.compose.ui.geometry.Size(paddleWidth, paddleHeight)
        )

        topPaddle.update(ballPos.x, width)
        topPaddle.Draw(this, color = colors)
    }
}