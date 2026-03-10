package com.tunafysh.immersivepong

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

fun DrawScope.drawTrail(
    trail: ArrayDeque<Offset>,
    colors: ColorScheme,
    radius: Float = BALL_RADIUS,
    maxTrail: Int = TRAIL_MAX_SIZE
) {
    trail.forEachIndexed { index, pos ->
        val alpha = 1f - (index.toFloat() / maxTrail)
        drawCircle(
            color = colors.primary.copy(alpha = alpha * 0.6f),
            radius = radius * (1f - index * 0.04f),
            center = pos
        )
    }
}

fun DrawScope.drawScoreboard(
    textMeasurer: TextMeasurer,
    playerScore: Int,
    opponentScore: Int,
    colors: ColorScheme
) {
    val width = size.width
    val height = size.height

    val scoreStyle = TextStyle(
        fontSize = 100.sp,
        fontWeight = FontWeight.Black,
        fontFamily = FontFamily.Monospace,
        color = colors.onBackground.copy(alpha = 0.12f),
        letterSpacing = 8.sp
    )

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
}

fun DrawScope.drawBottomPaddle(
    playerPaddleX: Float,
    colors: ColorScheme
) {
    val bottomPaddleTop = size.height - PADDLE_PADDING_BOTTOM - PADDLE_HEIGHT
    drawRoundRect(
        color = colors.secondary,
        topLeft = Offset(playerPaddleX, bottomPaddleTop),
        size = Size(PADDLE_WIDTH, PADDLE_HEIGHT),
        cornerRadius = CornerRadius(PADDLE_CORNER_RADIUS, PADDLE_CORNER_RADIUS)
    )
}
