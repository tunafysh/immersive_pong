package com.tunafysh.immersivepong

import androidx.compose.ui.geometry.Offset

data class PhysicsResult(
    val position: Offset,
    val velocity: Offset,
    val playerScoreDelta: Int = 0,
    val opponentScoreDelta: Int = 0
)

fun updateBallPhysics(
    ballPos: Offset,
    velocity: Offset,
    canvasWidth: Float,
    canvasHeight: Float,
    topPaddle: Paddle,
    playerPaddleX: Float,
): PhysicsResult {
    var newPos = ballPos + velocity
    var newVelocity = velocity
    var playerScoreDelta = 0
    var opponentScoreDelta = 0

    // Top paddle collision
    val topPaddleBottom = PADDLE_PADDING_TOP + topPaddle.height
    if (newPos.y - BALL_RADIUS <= topPaddleBottom && newVelocity.y < 0) {
        if (newPos.x >= topPaddle.x && newPos.x <= topPaddle.x + topPaddle.width) {
            newVelocity = Offset(newVelocity.x, -newVelocity.y)
            newPos = Offset(newPos.x, topPaddleBottom + BALL_RADIUS)
        } else {
            playerScoreDelta = 1
            newPos = Offset(canvasWidth / 2f, canvasHeight / 2f)
            newVelocity = Offset(INITIAL_VELOCITY_X, INITIAL_VELOCITY_Y)
        }
    }

    // Bottom paddle collision
    val bottomPaddleTop = canvasHeight - PADDLE_PADDING_BOTTOM - PADDLE_HEIGHT
    if (newPos.y + BALL_RADIUS >= bottomPaddleTop && newVelocity.y > 0) {
        if (newPos.x >= playerPaddleX && newPos.x <= playerPaddleX + PADDLE_WIDTH) {
            newVelocity = Offset(newVelocity.x, -newVelocity.y)
            newPos = Offset(newPos.x, bottomPaddleTop - BALL_RADIUS)
        } else {
            opponentScoreDelta = 1
            newPos = Offset(canvasWidth / 2f, canvasHeight / 2f)
            newVelocity = Offset(INITIAL_VELOCITY_X, INITIAL_VELOCITY_Y)
        }
    }

    // Wall bounce
    if (newPos.x - BALL_RADIUS <= 0f || newPos.x + BALL_RADIUS >= canvasWidth) {
        newVelocity = Offset(-newVelocity.x, newVelocity.y)
        newPos = Offset(newPos.x.coerceIn(BALL_RADIUS, canvasWidth - BALL_RADIUS), newPos.y)
    }

    return PhysicsResult(newPos, newVelocity, playerScoreDelta, opponentScoreDelta)
}
