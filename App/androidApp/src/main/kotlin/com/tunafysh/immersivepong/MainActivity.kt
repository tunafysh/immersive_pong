package com.tunafysh.immersivepong

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import java.io.File

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        initContext(this)
        setContent {
            App()
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return when (keyCode) {
            KeyEvent.KEYCODE_VOLUME_UP -> {
                setVolumeButtonPressed(VolumeButton.UP)
                true
            }
            KeyEvent.KEYCODE_VOLUME_DOWN -> {
                setVolumeButtonPressed(VolumeButton.DOWN)
                true
            }
            else -> super.onKeyDown(keyCode, event)
        }
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        return when (keyCode) {
            KeyEvent.KEYCODE_VOLUME_UP, KeyEvent.KEYCODE_VOLUME_DOWN -> {
                clearVolumeButtonState()
                true
            }
            else -> super.onKeyUp(keyCode, event)
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}