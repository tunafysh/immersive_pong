package com.tunafysh.immersivepong.game


expect class Timer() {
    var lastTime: Long

    fun update(): Double
}