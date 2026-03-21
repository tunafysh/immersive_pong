package com.tunafysh.immersivepong.game

import platform.Foundation.NSDate
import platform.Foundation.timeIntervalSince1970

fun currentTimeMillis(): Long = (NSDate().timeIntervalSince1970() * 1000).toLong()
actual class Timer {
    actual var lastTime: Long = currentTimeMillis()

    actual fun update(): Double {
        val now = currentTimeMillis()
        val dt = (now - lastTime) / 1000.0
        lastTime = now
        return dt.coerceAtMost(0.1)
    }
}