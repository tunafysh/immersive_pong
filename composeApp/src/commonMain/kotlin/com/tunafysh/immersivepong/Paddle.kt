package com.tunafysh.immersivepong

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope

interface Paddle {
    var x: Float
    val width: Float
    val height: Float
    
    fun update(ballX: Float, canvasWidth: Float, playerX: Float)
    fun draw(drawScope: DrawScope, colors: ColorScheme, paddingTop: Float, cornerRadius: Float)
}

class AIPaddle(
    override var x: Float,
    override val width: Float,
    override val height: Float
) : Paddle {
    override fun update(ballX: Float, canvasWidth: Float, playerX: Float) {
        val targetX = (ballX - width / 2f).coerceIn(0f, canvasWidth - width)
        val speed = 8f
        x = when {
            x < targetX -> (x + speed).coerceAtMost(targetX)
            x > targetX -> (x - speed).coerceAtLeast(targetX)
            else -> x
        }
    }
    
    override fun draw(drawScope: DrawScope, colors: ColorScheme, paddingTop: Float, cornerRadius: Float) {
        drawScope.drawRoundRect(
            color = colors.primary,
            topLeft = Offset(x, paddingTop),
            size = Size(width, height),
            cornerRadius = CornerRadius(cornerRadius, cornerRadius)
        )
    }
}

class PlayerPaddle(
    override var x: Float,
    override val width: Float,
    override val height: Float
) : Paddle {
    override fun update(ballX: Float, canvasWidth: Float, playerX: Float) {
        x = playerX
    }
    
    override fun draw(drawScope: DrawScope, colors: ColorScheme, paddingTop: Float, cornerRadius: Float) {
        drawScope.drawRoundRect(
            color = colors.tertiary,
            topLeft = Offset(x, paddingTop),
            size = Size(width, height),
            cornerRadius = CornerRadius(cornerRadius, cornerRadius)
        )
    }
}
