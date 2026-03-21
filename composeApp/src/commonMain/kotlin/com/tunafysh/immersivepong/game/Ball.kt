package com.tunafysh.immersivepong.game

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.tunafysh.immersivepong.offsetFromFloat

data class Ball(
    var pos: Offset = Offset((SCREEN_WIDTH / 2).toFloat(), (SCREEN_HEIGHT / 2).toFloat()),
    var vel: Offset = offsetFromFloat(200f),
    var visibility: Boolean = true
)

/**
 * Updates ball position based on velocity and elapsed time.
 * @param deltaTime Time elapsed since last frame in seconds
 * @return Updated ball with new position
 */
fun Ball.update(deltaTime: Double, score: Scoreboard): Ball {
    val dt = deltaTime.toFloat()
    var newPos = pos + Offset(vel.x * dt, vel.y * dt)
    var newVel = vel

    when {
        // Bounce off left wall
        newPos.x - BALL_SIZE < 0 -> {
            newVel = newVel.copy(x = -newVel.x)
            newPos = newPos.copy(x = newPos.x.coerceIn(BALL_SIZE, SCREEN_WIDTH - BALL_SIZE))
        }

        // Bounce off right wall
        newPos.x + BALL_SIZE > SCREEN_WIDTH -> {
            newVel = newVel.copy(x = -newVel.x)
            newPos = newPos.copy(x = newPos.x.coerceIn(BALL_SIZE, SCREEN_WIDTH - BALL_SIZE))
        }

        // Bounce off top wall (home scores)
        newPos.y - BALL_SIZE < 0 && visibility -> {
            newVel = newVel.copy(y = -newVel.y)
            newPos = newPos.copy(y = newPos.y.coerceIn(BALL_SIZE, SCREEN_HEIGHT - BALL_SIZE))
            score.scoreHome()
        }

        // Bounce off bottom wall (visitor scores)
        newPos.y + BALL_SIZE > SCREEN_HEIGHT -> {
            newVel = newVel.copy(y = -newVel.y)
            newPos = newPos.copy(y = newPos.y.coerceIn(BALL_SIZE, SCREEN_HEIGHT - BALL_SIZE))
            score.scoreVisitor()
        }
    }

    return copy(pos = newPos, vel = newVel)
}

fun DrawScope.drawBall(ball: Ball, colors: ColorScheme) {
    drawCircle(
        color = colors.primary,
        radius = BALL_SIZE,
        center = ball.pos
    )
}