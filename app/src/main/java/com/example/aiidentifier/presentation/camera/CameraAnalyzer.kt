package com.example.aiidentifier.presentation.camera

import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.example.aiidentifier.presentation.FrameProcessor

//class FrameAnalyzer : ImageAnalysis.Analyzer {
//
//    override fun analyze(image: ImageProxy) {
//
//        Log.i(
//            "CameraFrame",
//            "Frame received: ${image.width} x ${image.height}"
//        )
//        println("CameraFrame: Frame received: ${image.width} x ${image.height}")
//
//        image.close()
//    }
//}

class FrameAnalyzer(
    private val frameProcessor: FrameProcessor
) : ImageAnalysis.Analyzer {

    override fun analyze(
        image: ImageProxy
    ) {

        Log.d("ObjectDetection", "Analyzer called")

        frameProcessor.process(
            imageProxy = image,
            onComplete = {
                image.close()
            }
        )
    }
}