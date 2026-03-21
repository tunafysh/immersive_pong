package com.tunafysh.immersivepong.game

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.UIKit.UIScreen

@OptIn(ExperimentalForeignApi::class)
actual fun getScreenWidth(): Int {
    return UIScreen.mainScreen.bounds.useContents { size.width.toInt() }
}

@OptIn(ExperimentalForeignApi::class)
actual fun getScreenHeight(): Int {
    return UIScreen.mainScreen.bounds.useContents { size.height.toInt() }
}