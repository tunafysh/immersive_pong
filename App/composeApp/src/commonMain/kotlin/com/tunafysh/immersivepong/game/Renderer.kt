package com.tunafysh.immersivepong.game

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.tunafysh.immersivepong.ImmersiveMode
import kotlin.math.abs
import kotlin.math.max

private val timer = Timer()

@Composable
fun Renderer(config: RendererConfig) {
    ImmersiveMode(true)

    // State
    val colors = MaterialTheme.colorScheme
    var ball by remember { mutableStateOf(Ball.create()) }
    var score by remember { mutableStateOf(Scoreboard()) }
    var fps by remember { mutableIntStateOf(0) }
    var fingerDragX by remember { mutableStateOf(0f) }
    
    val trail = remember { ArrayDeque<Offset>(TRAIL_MAX) }
    val bottomPaddle = remember { 
        HumanPaddle(
            x = SCREEN_WIDTH / 2f - PADDLE_WIDTH / 2f,
            color = colors.primary,
            animatedColor = colors.inversePrimary,
            top = false
        )
    }
    val topPaddle = remember {
        AIPaddle(
            x = SCREEN_WIDTH / 2f - PADDLE_WIDTH / 2f,
            color = colors.error,
            animatedColor = Color.Red,
            top = true
        )
    }
    val paddles = remember { listOf(bottomPaddle, topPaddle) }
    
    val isMultiplayer = config is RendererConfig.MultiplayerMode

    // Game loop
    LaunchedEffect(Unit) {
        while (true) {
            val dt = timer.update().toFloat()
            
            if (IS_DEBUG_FPS_ENABLED && dt > 0.0f) {
                fps = (1.0f / dt).toInt()
            }

            // Update game state
            val (newBall, newScore) = ball.update(dt.toDouble(), score, config, paddles)
            ball = newBall
            score = newScore

            // Update paddles
            val context = UpdateContext(
                deltaTime = dt,
                ballPos = ball.pos,
                ballVel = ball.vel,
                inputX = fingerDragX,
                config = config
            )
            bottomPaddle.update(context)
            bottomPaddle.updateAnimation(dt)
            topPaddle.update(context)
            topPaddle.updateAnimation(dt)

            // Update trail
            trail.addLast(ball.pos)
            if (trail.size > TRAIL_MAX) trail.removeFirst()
            
            withFrameNanos { }
        }
    }

    // UI
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    fingerDragX += dragAmount.x
                }
            }
    ) {
        score.DrawScores(isMultiplayer, colors.inversePrimary)
        
        Canvas(modifier = Modifier.fillMaxSize()) {
            if (IS_DEBUG_GUIDES_ENABLED) drawDebugGuides(bottomPaddle.x)
            
            drawTrail(trail, colors.primary)
            topPaddle.draw(this)
            bottomPaddle.draw(this)
            drawBall(ball, colors)
            
            if (IS_DEBUG_GUIDES_ENABLED) {
                drawCircle(
                    color = Color.Red.copy(alpha = 0.5f),
                    radius = BALL_SIZE,
                    center = ball.pos
                )
            }
        }
        
        if (IS_DEBUG_FPS_ENABLED || IS_DEBUG_STATE_ENABLED) {
            DebugOverlay(fps, ball, score, fingerDragX)
        }
    }
}

private fun DrawScope.drawTrail(points: List<Offset>, color: Color) {
    if (points.isEmpty()) return
    
    val lastIndex = (points.size - 1).toFloat()
    points.forEachIndexed { i, pos ->
        val progress = if (points.size == 1) 1f else i / lastIndex
        val radius = max(1f, BALL_SIZE * (0.25f + 0.75f * progress))
        val alpha = 0.10f + 0.60f * progress
        drawCircle(
            color = color.copy(alpha = alpha),
            radius = radius,
            center = pos
        )
    }
}

@Composable
private fun DebugOverlay(fps: Int, ball: Ball, score: Scoreboard, dragX: Float) {
    Column(modifier = Modifier.padding(12.dp)) {
        if (IS_DEBUG_FPS_ENABLED) {
            Text("FPS: $fps", color = Color.White)
        }
        if (IS_DEBUG_STATE_ENABLED) {
            val (x, y) = ball.pos.x.toInt() to ball.pos.y.toInt()
            val (vx, vy) = ball.vel.x.toInt() to ball.vel.y.toInt()
            Text("Ball($x, $y) vel($vx, $vy)", color = Color.White)
            Text("Score H:${score.home} V:${score.visitor} | Drag:${abs(dragX).toInt()}", color = Color.White)
        }
    }
}

private fun DrawScope.drawDebugGuides(paddleX: Float) {
    val midX = SCREEN_WIDTH / 2f
    val midY = SCREEN_HEIGHT / 2f
    val paddleY = SCREEN_HEIGHT - PADDLE_SUSPENSION - (PADDLE_HEIGHT / 2f)
    val paddleCenterX = paddleX + (PADDLE_WIDTH / 2f)
    
    // Center crosshair
    drawLine(Color.Yellow.copy(0.55f), Offset(midX, 0f), Offset(midX, SCREEN_HEIGHT.toFloat()), 2f)
    drawLine(Color.Yellow.copy(0.35f), Offset(0f, midY), Offset(SCREEN_WIDTH.toFloat(), midY), 2f)
    
    // Paddle guides
    drawLine(Color.Cyan.copy(0.65f), Offset(0f, paddleY), Offset(SCREEN_WIDTH.toFloat(), paddleY), 2f)
    drawLine(Color.Cyan.copy(0.65f), Offset(paddleCenterX, 0f), Offset(paddleCenterX, SCREEN_HEIGHT.toFloat()), 1.5f)
}