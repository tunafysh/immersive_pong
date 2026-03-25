package com.tunafysh.immersivepong.game

import android.content.res.Resources

val system: Resources = Resources.getSystem()

actual fun getScreenWidth(): Int {
    return system.displayMetrics.widthPixels
}

actual fun getScreenHeight(): Int {
    return system.displayMetrics.heightPixels
}