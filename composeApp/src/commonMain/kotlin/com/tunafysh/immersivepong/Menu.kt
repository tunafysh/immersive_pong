package com.tunafysh.immersivepong

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Text
import androidx.compose.material3.Tab as M3Tab
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@Composable
fun MenuScreen(onStartGame: @Composable () -> Unit) {
    var currentTab by remember { mutableStateOf<Tab>(Tab.SP) }
    var tabIndex by remember { mutableStateOf(0) }
    val tabs = listOf(Tab.SP, Tab.MP)

    ImmersiveMode(false)

    Column (
     modifier = Modifier.fillMaxSize().safeContentPadding()
    ){

        Box(modifier = Modifier.weight(1f))
        {
            Text("test")
        }

        PrimaryTabRow(
            selectedTabIndex = tabs.indexOf(currentTab)
        ) {
            // Loop over your tabs manually
            tabs.forEachIndexed { index, tab ->
                M3Tab(
                    selected = tab == currentTab,
                    onClick = { currentTab = tab },
                    text = {
                        Text(
                            when (tab) {
                                Tab.SP -> "Single Player"
                                Tab.MP -> "Multiplayer"
                            }
                        )
                    }
                )
            }
        }
    }
}