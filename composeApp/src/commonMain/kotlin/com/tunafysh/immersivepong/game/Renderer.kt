@file:Suppress("SameParameterValue")

package com.tunafysh.immersivepong.game

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.tunafysh.immersivepong.ImmersiveMode
import kotlin.math.max

private val timer = Timer()

@Composable
fun Renderer(config: RendererConfig) {
    ImmersiveMode(true)

    val colors = MaterialTheme.colorScheme
    var ball by remember { mutableStateOf(Ball()) }

    val trail = remember { ArrayDeque<Offset>(TRAIL_MAX) }
    var redrawTrigger by remember { mutableIntStateOf(0) }

    var score by remember { mutableStateOf(Scoreboard()) }

    // Determine game mode
    val isMultiplayer = config is RendererConfig.MultiplayerMode

    Box {
        score.DrawScores(isMultiplayer, colors.inversePrimary)
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Draw ball trail and ball
            drawTrail(trail, colors.primary)
            drawBall(ball, colors)

            if (redrawTrigger < 0) return@Canvas // Force redraw hook (i have no idea how tf this works but it does.)
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            val dt = timer.update()

            // Update physics
            ball = ball.update(dt, score)

            // Update trail
            trail.addLast(ball.pos)
            while (trail.size > TRAIL_MAX) {
                trail.removeFirst()
            }

            // Force canvas redraw by toggling trigger
            redrawTrigger = redrawTrigger xor 1

            withFrameNanos { }
        }
    }
}

/**
 * Draws a fading trail showing the ball's motion history.
 * Older points are smaller and more transparent, newer points are larger and more opaque.
 */
private fun DrawScope.drawTrail(
    points: List<Offset>,
    color: Color
) {
    val n = points.size
    if (n == 0) return

    points.forEachIndexed { i, p ->
        val t = if (n == 1) 1f else i.toFloat() / (n - 1).toFloat() // 0..1
        val radius = max(1f, BALL_SIZE * (0.25f + 0.75f * t))
        val alpha = 0.10f + 0.60f * t
        drawCircle(color = color.copy(alpha = alpha), radius = radius, center = p)
    }
}