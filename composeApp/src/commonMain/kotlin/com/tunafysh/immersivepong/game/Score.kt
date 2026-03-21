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

class Scoreboard(
    var home: Int = 0,
    var visitor: Int = 0,
){
    fun scoreHome() = home++
    fun scoreVisitor() = visitor++
}

@Composable
fun Scoreboard.DrawVisitorScore(font: FontFamily, color: Color) {
    Text(
        text = visitor.toString(),
        fontFamily = font,
        fontSize = SCORE_FONT_SIZE,
        color = color
    )
}

@Composable
fun Scoreboard.DrawHomeScore(font: FontFamily, color: Color) {
    Text(
        text = home.toString(),
        fontFamily = font,
        fontSize = SCORE_FONT_SIZE,
        color = color
    )
}

@Composable
fun Scoreboard.DrawScores(isMultiplayer: Boolean, color: Color){
    val font = scoreFontFamily()

    if(isMultiplayer){
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DrawHomeScore(font, color)
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DrawVisitorScore(font, color)
            DrawHomeScore(font, color)
        }
    }
}