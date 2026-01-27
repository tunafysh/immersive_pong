package com.tunafysh.immersivepong

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.tunafysh.immersivepong.ui.theme.AppTheme

sealed class Screen {
    object Menu: Screen()
    object Game: Screen()
}

sealed interface Tab {
    data object SP: Tab
    data object MP: Tab
}

@Composable
@Preview
fun App() {
    AppTheme {
        var currentScreen by remember { mutableStateOf<Screen>(Screen.Game) }
        Box{
            when (currentScreen){
                Screen.Game -> Renderer( onExit = { currentScreen = Screen.Menu; ImmersiveMode(false) })
                Screen.Menu -> MenuScreen( onStartGame = { currentScreen = Screen.Game; ImmersiveMode(true) } )
            }
        }

    }
}