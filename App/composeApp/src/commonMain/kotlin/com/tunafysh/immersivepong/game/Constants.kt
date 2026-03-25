package com.tunafysh.immersivepong.game

import androidx.compose.ui.text.font.FontFamily
import immersivepong.composeapp.generated.resources.Res
import immersivepong.composeapp.generated.resources.scorefont
import org.jetbrains.compose.resources.Font
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp

// val FPS = 60
const val BALL_SIZE = 20f
const val PADDLE_WIDTH = 200f
const val PADDLE_HEIGHT = 50f
const val PADDLE_SUSPENSION = 85f

const val TRAIL_MAX = 20
val SCREEN_WIDTH = getScreenWidth()
val SCREEN_HEIGHT = getScreenHeight()
val SCORE_FONT_SIZE = 128.sp
val AI_SPEED = 20

const val DEBUG_ENABLED = false
const val DEBUG_SHOW_FPS = true
const val DEBUG_SHOW_STATE = true
const val DEBUG_DRAW_GUIDES = true

const val IS_DEBUG_FPS_ENABLED = DEBUG_ENABLED && DEBUG_SHOW_FPS
const val IS_DEBUG_STATE_ENABLED = DEBUG_ENABLED && DEBUG_SHOW_STATE
const val IS_DEBUG_GUIDES_ENABLED = DEBUG_ENABLED && DEBUG_DRAW_GUIDES

@Composable
fun scoreFontFamily() = FontFamily(
    Font(Res.font.scorefont)
)
