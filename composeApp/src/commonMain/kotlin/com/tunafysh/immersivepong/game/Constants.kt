package com.tunafysh.immersivepong.game

import androidx.compose.ui.text.font.FontFamily
import immersivepong.composeapp.generated.resources.Res
import immersivepong.composeapp.generated.resources.scorefont
import org.jetbrains.compose.resources.Font
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp

// val FPS = 60
val BALL_SIZE = 20f
// val PADDLE_WIDTH = 80f
// val PADDLE_HEIGHT = 20f
val TRAIL_MAX = 20
val SCREEN_WIDTH = getScreenWidth()
val SCREEN_HEIGHT = getScreenHeight()
val SCORE_FONT_SIZE = 128.sp
@Composable
fun scoreFontFamily() = FontFamily(
    Font(Res.font.scorefont)
)