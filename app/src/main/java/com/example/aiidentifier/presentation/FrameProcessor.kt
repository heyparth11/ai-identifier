package com.example.aiidentifier.presentation

import androidx.camera.core.ImageProxy

interface FrameProcessor {

    fun process(
        imageProxy: ImageProxy,
        onComplete:()-> Unit
    )
}
