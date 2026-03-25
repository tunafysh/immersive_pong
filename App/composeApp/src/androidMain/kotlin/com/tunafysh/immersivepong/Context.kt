package com.tunafysh.immersivepong

import android.content.Context
import androidx.activity.ComponentActivity

lateinit var appContext: Context

fun initContext(context: ComponentActivity) {
    // Use applicationContext to avoid leaking the Activity
    appContext = context.applicationContext
}

fun getContext(): Context {
    return appContext
}