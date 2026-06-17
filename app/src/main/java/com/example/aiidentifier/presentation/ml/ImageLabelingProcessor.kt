package com.example.aiidentifier.presentation.ml

import android.graphics.Rect
import android.util.Log
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageProxy
import com.example.aiidentifier.domain.DetectedObjectUi
import com.example.aiidentifier.presentation.FrameProcessor
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions

class ImageLabelingProcessor(
    private val onDetectionResult: (List<DetectedObjectUi>) -> Unit
) : FrameProcessor {

    private var isProcessing = false

    private val options = ImageLabelerOptions.Builder()
        .setConfidenceThreshold(0.4f)
        .build()

    private val labeler = ImageLabeling.getClient(options)

    @androidx.annotation.OptIn(ExperimentalGetImage::class)
    @OptIn(ExperimentalGetImage::class)
    override fun process(
        imageProxy: ImageProxy,
        onComplete: () -> Unit
    ) {
        if (isProcessing) {
            onComplete()
            return
        }

        isProcessing = true

        val mediaImage = imageProxy.image

        if (mediaImage == null) {
            isProcessing = false
            onComplete()
            return
        }

        val image = InputImage.fromMediaImage(
            mediaImage,
            imageProxy.imageInfo.rotationDegrees
        )

        labeler.process(image)
            .addOnSuccessListener { labels ->
                val detections = labels.map { label ->
                    DetectedObjectUi(
                        label = label.text,
                        confidence = label.confidence,
                        boundingBox = Rect()
                    )
                }

                Log.d(
                    "ObjectDetection",
                    "Labels detected: ${labels.size} -> ${detections.joinToString { "${it.label} (${(it.confidence * 100).toInt()}%)" }}"
                )

                onDetectionResult(detections)
            }
            .addOnFailureListener {
                it.printStackTrace()
            }
            .addOnCompleteListener {
                isProcessing = false
                onComplete()
            }
    }
}
