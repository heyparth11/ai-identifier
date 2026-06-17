package com.example.aiidentifier.domain

import android.graphics.Rect

data class DetectedObjectUi(
    val label: String,
    val confidence: Float,
    val boundingBox: Rect
)