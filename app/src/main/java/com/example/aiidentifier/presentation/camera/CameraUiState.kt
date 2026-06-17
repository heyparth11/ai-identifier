package com.example.aiidentifier.presentation.camera

import com.example.aiidentifier.domain.DetectedObjectUi

data class CameraUiState(

    val framesProcessed: Long = 0,
    val fps: Int = 0,
    val recognizedText: String = "",
    val detectedObjects: List<DetectedObjectUi> = emptyList(),
    val imageWidth: Int = 0,
    val imageHeight: Int = 0,
    val rotationDegrees: Int = 0,
)