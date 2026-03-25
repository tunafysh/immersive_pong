package com.tunafysh.immersivepong.game

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.DrawScope

data class Ball(
    val pos: Offset,
    val vel: Offset,
    val visibility: Boolean = true
) {
    companion object {
        fun create(): Ball {
            return Ball(
                pos = Offset(SCREEN_WIDTH / 2f, SCREEN_HEIGHT / 2f),
                vel = Offset(450f, 450f)
            )
        }
    }
}

fun Ball.update(
    deltaTime: Double,
    score: Scoreboard,
    config: RendererConfig,
    paddles: List<Paddle>
): Pair<Ball, Scoreboard> {
    val dt = deltaTime.toFloat()
    var newPos = pos + vel * dt
    var newVel = vel
    var newScore = score
    
    // Wall collisions - sides
    if (newPos.x < BALL_SIZE || newPos.x > SCREEN_WIDTH - BALL_SIZE) {
        newVel = newVel.copy(x = -newVel.x)
        newPos = newPos.copy(x = newPos.x.coerceIn(BALL_SIZE, SCREEN_WIDTH - BALL_SIZE))
    }
    
    // Wall collisions - top (scoring zone)
    if (newPos.y < BALL_SIZE && visibility) {
        if (config is RendererConfig.SingleplayerMode) {
            newVel = newVel.copy(y = -newVel.y)
            newPos = newPos.copy(y = BALL_SIZE)
            newScore = score.scoreHome()
        } else {
            return copy(visibility = false) to score
        }
    }
    
    // Wall collisions - bottom (scoring zone)
    if (newPos.y > SCREEN_HEIGHT - BALL_SIZE) {
        newVel = newVel.copy(y = -newVel.y)
        newPos = newPos.copy(y = SCREEN_HEIGHT - BALL_SIZE)
        newScore = score.scoreVisitor()
    }

    // Paddle collisions
    for (paddle in paddles) {
        val paddleY = if (paddle.top) {
            PADDLE_SUSPENSION
        } else {
            SCREEN_HEIGHT - PADDLE_SUSPENSION - PADDLE_HEIGHT
        }
        
        val ballIntersectsX = newPos.x + BALL_SIZE > paddle.x && 
                              newPos.x - BALL_SIZE < paddle.x + PADDLE_WIDTH
        val ballIntersectsY = newPos.y + BALL_SIZE > paddleY && 
                              newPos.y - BALL_SIZE < paddleY + PADDLE_HEIGHT
        
        if (ballIntersectsX && ballIntersectsY) {
            newVel = newVel.copy(y = -newVel.y)
            paddle.onHit()
            
            // Prevent sticking
            newPos = if (newVel.y < 0) {
                newPos.copy(y = paddleY - BALL_SIZE)
            } else {
                newPos.copy(y = paddleY + PADDLE_HEIGHT + BALL_SIZE)
            }
            break
        }
    }
    
    return copy(pos = newPos, vel = newVel) to newScore
}

fun DrawScope.drawBall(ball: Ball, colors: ColorScheme) {
    drawCircle(
        color = colors.primary,
        radius = BALL_SIZE,
        center = ball.pos
    )
}