package com.example.aiidentifier

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.example.aiidentifier.presentation.CameraRoute
import com.example.aiidentifier.ui.theme.AIIdentifierTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AIIdentifierTheme {
                Box(modifier = Modifier.fillMaxSize()) {
                    CameraRoute(modifier = Modifier.fillMaxSize())
                }
            }
        }
    }
}