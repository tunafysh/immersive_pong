package com.tunafysh.immersivepong

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.tunafysh.immersivepong.ui.theme.AppTheme

@Composable
@Preview
fun App() {
    AppTheme {
        var showRenderer by remember { mutableStateOf(false) }
        Box{
            Renderer()
        }

    }
}