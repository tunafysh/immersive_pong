package com.tunafysh.immersivepong.game

import io.ktor.util.date.getTimeMillis

actual class Timer {
    actual var lastTime = getTimeMillis()

    actual fun update(): Double {
        val now = getTimeMillis()
        val dt = (now - lastTime) / 1000.0
        lastTime = now
        return dt.coerceAtMost(0.1)
    }
}