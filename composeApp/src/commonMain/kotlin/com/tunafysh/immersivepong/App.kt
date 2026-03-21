package com.tunafysh.immersivepong

import androidx.compose.runtime.*
import androidx.compose.foundation.layout.Box
import com.tunafysh.immersivepong.game.Renderer
import com.tunafysh.immersivepong.game.RendererConfig
import com.tunafysh.immersivepong.game.SingleplayerConfig
import com.tunafysh.immersivepong.ui.theme.AppTheme

sealed class Screen {
    object Menu : Screen()
    data class Game(val config: RendererConfig) : Screen()
}

// Top-level mutable state (optional, can also be inside App)
var configState = mutableStateOf<RendererConfig?>(null)

@Composable
fun App() {
    AppTheme {
        var currentScreen by remember { mutableStateOf<Screen>(Screen.Menu) }

        Box {
            when (val screen = currentScreen) { // safer: avoid casting
                is Screen.Game -> {
                    // Use the config from the Screen.Game instance
                    Renderer(screen.config)
                }
                Screen.Menu -> {
                    Menu {
                        // Create a RendererConfig and switch screens
                        val config = RendererConfig.SingleplayerMode(SingleplayerConfig.Local)
                        configState.value = config // store in global state if needed
                        currentScreen = Screen.Game(config)
                    }
                }
            }
        }
    }
}