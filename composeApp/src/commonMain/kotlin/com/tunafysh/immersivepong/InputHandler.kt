package com.tunafysh.immersivepong

import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerId
import androidx.compose.ui.input.pointer.pointerInput

/**
 * Handles multi-touch drag input for both paddles.
 * The top half of the screen controls the top paddle (multiplayer only);
 * the bottom half controls the player paddle.
 */
fun Modifier.gamePointerInput(
    isMultiplayer: Boolean,
    isAIMode: Boolean,
    getTopPaddleX: () -> Float,
    getPlayerPaddleX: () -> Float,
    onTopPaddleMove: (Float) -> Unit,
    onPlayerPaddleMove: (Float) -> Unit,
): Modifier = pointerInput(isMultiplayer, isAIMode) {
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
                        if (isMultiplayer && currentPos.y < size.height / 2) {
                            onTopPaddleMove(
                                (getTopPaddleX() + dragAmount.x).coerceIn(0f, size.width - PADDLE_WIDTH)
                            )
                        } else if (currentPos.y >= size.height / 2) {
                            onPlayerPaddleMove(
                                (getPlayerPaddleX() + dragAmount.x).coerceIn(0f, size.width - PADDLE_WIDTH)
                            )
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
