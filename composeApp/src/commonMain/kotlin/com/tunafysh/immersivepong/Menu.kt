package com.tunafysh.immersivepong

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
private fun PersonIcon(modifier: Modifier = Modifier, tint: Color = Color.White) {
    Canvas(modifier = modifier.size(24.dp)) {
        val strokeWidth = 2.dp.toPx()
        // Head
        drawCircle(
            color = tint,
            radius = size.width * 0.2f,
            center = Offset(size.width / 2, size.height * 0.3f),
            style = Stroke(width = strokeWidth)
        )
        // Body
        drawLine(
            color = tint,
            start = Offset(size.width / 2, size.height * 0.5f),
            end = Offset(size.width / 2, size.height * 0.75f),
            strokeWidth = strokeWidth
        )
        // Arms
        drawLine(
            color = tint,
            start = Offset(size.width * 0.3f, size.height * 0.55f),
            end = Offset(size.width * 0.7f, size.height * 0.55f),
            strokeWidth = strokeWidth
        )
        // Legs
        drawLine(
            color = tint,
            start = Offset(size.width / 2, size.height * 0.75f),
            end = Offset(size.width * 0.35f, size.height * 0.95f),
            strokeWidth = strokeWidth
        )
        drawLine(
            color = tint,
            start = Offset(size.width / 2, size.height * 0.75f),
            end = Offset(size.width * 0.65f, size.height * 0.95f),
            strokeWidth = strokeWidth
        )
    }
}

@Composable
private fun GroupIcon(modifier: Modifier = Modifier, tint: Color = Color.White) {
    Canvas(modifier = modifier.size(24.dp)) {
        val strokeWidth = 2.dp.toPx()
        val personWidth = size.width * 0.3f
        
        // Left person (smaller)
        drawCircle(
            color = tint,
            radius = personWidth * 0.15f,
            center = Offset(size.width * 0.25f, size.height * 0.35f),
            style = Stroke(width = strokeWidth)
        )
        drawLine(
            color = tint,
            start = Offset(size.width * 0.25f, size.height * 0.5f),
            end = Offset(size.width * 0.25f, size.height * 0.7f),
            strokeWidth = strokeWidth
        )
        
        // Right person (smaller)
        drawCircle(
            color = tint,
            radius = personWidth * 0.15f,
            center = Offset(size.width * 0.75f, size.height * 0.35f),
            style = Stroke(width = strokeWidth)
        )
        drawLine(
            color = tint,
            start = Offset(size.width * 0.75f, size.height * 0.5f),
            end = Offset(size.width * 0.75f, size.height * 0.7f),
            strokeWidth = strokeWidth
        )
        
        // Center person (larger, in front)
        drawCircle(
            color = tint,
            radius = personWidth * 0.2f,
            center = Offset(size.width / 2, size.height * 0.3f),
            style = Stroke(width = strokeWidth)
        )
        drawLine(
            color = tint,
            start = Offset(size.width / 2, size.height * 0.5f),
            end = Offset(size.width / 2, size.height * 0.75f),
            strokeWidth = strokeWidth
        )
    }
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
                        PersonIcon(
                            tint = if (currentTab == Tab.SP) 
                                MaterialTheme.colorScheme.primary 
                            else 
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    label = { Text("Single Player") },
                    selected = currentTab == Tab.SP,
                    onClick = { currentTab = Tab.SP }
                )
                NavigationBarItem(
                    icon = { 
                        GroupIcon(
                            tint = if (currentTab == Tab.MP) 
                                MaterialTheme.colorScheme.primary 
                            else 
                                MaterialTheme.colorScheme.onSurfaceVariant
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