package com.tunafysh.immersivepong

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.Icons.Outlined
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

sealed interface Tab {
    data object SP : Tab
    data object MP : Tab
}

@Composable
fun MenuScreen(onStartGame: (Boolean) -> Unit) {
    var currentTab by remember { mutableStateOf<Tab>(Tab.SP) }

    ImmersiveMode(false)

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = if (currentTab == Tab.SP) {
                                Filled.Person
                            } else {
                                Outlined.Person
                            },
                            contentDescription = ""
                        )
                    },
                    label = { Text("Single Player") },
                    selected = currentTab == Tab.SP,
                    onClick = { currentTab = Tab.SP }
                )
                NavigationBarItem(
                    icon = {
                        Icon(
                            if(currentTab == Tab.MP){
                                Filled.Group
                            }
                            else {
                                Outlined.Group
                            },
                            contentDescription = ""
                        )
                    },
                    label = { Text("Multiplayer") },
                    selected = currentTab == Tab.MP,
                    onClick = { currentTab = Tab.MP }
                )
            }
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier.fillMaxSize().safeContentPadding(),
            color = MaterialTheme.colorScheme.background
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    Text(
                        text = "Immersive Pong",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Button(
                        onClick = { onStartGame(currentTab == Tab.SP) }
                    ) {
                        Text("Start Game")
                    }
                }
            }
        }
    }
}