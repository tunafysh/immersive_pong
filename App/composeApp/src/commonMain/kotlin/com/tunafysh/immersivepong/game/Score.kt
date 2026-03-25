package com.tunafysh.immersivepong.game

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily

data class Scoreboard(
    val home: Int = 0,
    val visitor: Int = 0
) {
    fun scoreHome() = copy(home = home + 1)
    fun scoreVisitor() = copy(visitor = visitor + 1)
}

@Composable
fun Scoreboard.DrawScores(isMultiplayer: Boolean, color: Color) {
    val font = scoreFontFamily()
    
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = if (isMultiplayer) Arrangement.Center else Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (!isMultiplayer) {
            Text(
                text = visitor.toString(),
                fontFamily = font,
                fontSize = SCORE_FONT_SIZE,
                color = color
            )
        }
        Text(
            text = home.toString(),
            fontFamily = font,
            fontSize = SCORE_FONT_SIZE,
            color = color
        )
    }
}