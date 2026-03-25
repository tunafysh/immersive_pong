package com.tunafysh.immersivepong.game

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.util.lerp
import com.tunafysh.immersivepong.predictPaddleMove

data class UpdateContext(
    val deltaTime: Float,
    val ballPos: Offset? = null,
    val ballVel: Offset? = null,
    val paddleX: Float? = null,
    val inputX: Float? = null,
    val config: RendererConfig
)

abstract class Paddle(
    var x: Float,
    val color: Color,
    val animatedColor: Color,
    val top: Boolean
) {
    private var hitFlashTime = 0f
    private val hitFlashDuration = 0.3f

    abstract fun update(ctx: UpdateContext)

    fun onHit() {
        hitFlashTime = hitFlashDuration
    }

    fun updateAnimation(deltaTime: Float) {
        if (hitFlashTime > 0f) {
            hitFlashTime = (hitFlashTime - deltaTime).coerceAtLeast(0f)
        }
    }

    private fun computeAnimatedColor(): Color {
        if (hitFlashTime <= 0f) return color

        val progress = hitFlashTime / hitFlashDuration
        return lerp(color, animatedColor, progress)
    }

    fun draw(scope: DrawScope) {
        val yPos = if (top) {
            PADDLE_SUSPENSION
        } else {
            SCREEN_HEIGHT - PADDLE_HEIGHT - PADDLE_SUSPENSION
        }

        scope.drawRoundRect(
            color = computeAnimatedColor(),
            topLeft = Offset(x, yPos),
            size = Size(PADDLE_WIDTH, PADDLE_HEIGHT),
            cornerRadius = CornerRadius(PADDLE_HEIGHT / 2f)
        )
    }
}

class HumanPaddle(x: Float, color: Color, animatedColor: Color, top: Boolean) : Paddle(x = x, color = color, animatedColor = animatedColor, top = top) {
    override fun update(ctx: UpdateContext) {
        ctx.inputX?.let { dragX ->
            x = dragX.coerceIn(0f, SCREEN_WIDTH - PADDLE_WIDTH)
        }
    }
}

class AIPaddle(x: Float, color: Color, animatedColor: Color, top: Boolean) : Paddle(x = x, color = color, animatedColor = animatedColor, top = top) {
    override fun update(ctx: UpdateContext) {
        val ballX = ctx.ballPos?.x ?: return
        val ballY = ctx.ballPos.y
        val velX = ctx.ballVel?.x ?: return
        val velY = ctx.ballVel.y

        val speed = when (ctx.config) {
            is RendererConfig.SingleplayerMode -> {
                when (val cfg = ctx.config.config) {
                    is SingleplayerConfig.AI -> when (cfg.difficulty) {
                        Difficulty.EASY -> 100f
                        Difficulty.MEDIUM -> 250f
                        Difficulty.HARD -> 500f
                    }
                    is SingleplayerConfig.Local -> 10f
                }
            }
            is RendererConfig.MultiplayerMode -> 10f
        }

        val inputs = listOf(ballX, ballY, velX, velY, x)
        val speedMultiplier = 1.5f // tune this
        val delta = speed * ctx.deltaTime * speedMultiplier

        when (predictPaddleMove(inputs)) {
            "left" -> x -= delta
            "right" -> x += delta
            "stay" -> {}
        }
        x = x.coerceIn(0f, SCREEN_WIDTH - PADDLE_WIDTH)
    }
}