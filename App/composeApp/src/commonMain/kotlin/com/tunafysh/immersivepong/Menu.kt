package com.tunafysh.immersivepong

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

//sealed interface Tab {
//    data object SP : Tab
//    data object MP : Tab
//}
//
//@Composable
//fun SingleplayerConfigMenu() {
//    Column(
//        modifier = Modifier
//            .safeContentPadding()
//            .fillMaxSize()
//    ){}
//}

@Composable
fun Menu(start: () -> Unit) {
    ImmersiveMode(false)

    Column(
        modifier = Modifier
            .safeContentPadding()
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text("Immersive Pong")
        Button(
            onClick = start
        ){
            Text("Start Game")
        }
    }
}