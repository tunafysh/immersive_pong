package com.tunafysh.immersivepong

/**
 * Predict the next paddle move given a 5-element input state.
 * @param inputState [ball_x, ball_y, vel_x, vel_y, paddle_y]
 * @return "up", "down", or "stay"
 */
expect fun predictPaddleMove(inputs: List<Float>): String

