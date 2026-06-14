package com.example.aiidentifier

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun CameraXScreen(modifier: Modifier) {

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        CameraPreview()
    }
}