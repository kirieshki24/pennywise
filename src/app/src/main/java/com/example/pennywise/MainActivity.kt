package com.example.pennywise

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.pennywise.ui.navigation.PennyWiseNavHost
import com.example.pennywise.ui.theme.PennyWiseTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PennyWiseTheme {
                val container = (application as PennyWiseApplication).container
                PennyWiseNavHost(appContainer = container)
            }
        }
    }
}