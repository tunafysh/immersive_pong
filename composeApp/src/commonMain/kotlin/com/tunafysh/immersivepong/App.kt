package com.tunafysh.immersivepong

import androidx.compose.runtime.*
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.tooling.preview.Preview
import com.tunafysh.immersivepong.ui.theme.AppTheme

sealed class Screen {
    object Menu: Screen()
    data class Game(val isSinglePlayer: Boolean): Screen()
}

sealed interface Tab {
    data object SP: Tab
    data object MP: Tab
}

@Composable
@Preview
fun App() {
    AppTheme {
        var currentScreen by remember { mutableStateOf<Screen>(Screen.Menu) }
        Box{
            when (currentScreen){
                is Screen.Game -> Renderer( 
                    isSinglePlayer = (currentScreen as Screen.Game).isSinglePlayer,
                    onExit = { currentScreen = Screen.Menu; ImmersiveMode(false) }
                )
                Screen.Menu -> MenuScreen( 
                    onStartGame = { isSinglePlayer -> 
                        currentScreen = Screen.Game(isSinglePlayer)
                    } 
                )
            }
        }

    }
}