package com.tunafysh.immersivepong

import okio.FileSystem
import okio.SYSTEM

/**
 * Predict the next paddle move given a 5-element input state.
 * @param inputs [ball_x, ball_y, vel_x, vel_y, paddle_x]
 * @return "left", "right", or "stay"
 */


expect fun predictPaddleMove(inputs: List<Float>): String