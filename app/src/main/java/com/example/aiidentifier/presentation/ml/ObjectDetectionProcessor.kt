package com.example.aiidentifier.presentation.ml

import android.graphics.Bitmap
import android.graphics.Rect
import android.util.Log
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageProxy
import com.example.aiidentifier.domain.DetectedObjectUi
import com.example.aiidentifier.presentation.FrameProcessor
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions

class ObjectDetectionProcessor(
    private val onDetectionResult: (List<DetectedObjectUi>, Int, Int, Int) -> Unit
) : FrameProcessor {

    private var isProcessing = false

    // Single Object Detection and Tracking (STREAM_MODE, default is single object)
    private val detectorOptions = ObjectDetectorOptions.Builder()
        .setDetectorMode(ObjectDetectorOptions.STREAM_MODE)
        .build()

    private val detector = ObjectDetection.getClient(detectorOptions)

    // Image Labeler for classifying the cropped object region
    private val labelerOptions = ImageLabelerOptions.Builder()
        .setConfidenceThreshold(0.4f)
        .build()

    private val labeler = ImageLabeling.getClient(labelerOptions)

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

        val imageWidth = imageProxy.width
        val imageHeight = imageProxy.height
        val rotationDegrees = imageProxy.imageInfo.rotationDegrees

        detector.process(image)
            .addOnSuccessListener { objects ->
                val detectedObject = objects.firstOrNull()
                if (detectedObject == null) {
                    onDetectionResult(emptyList(), imageWidth, imageHeight, rotationDegrees)
                    isProcessing = false
                    onComplete()
                    return@addOnSuccessListener
                }

                val boundingBox = detectedObject.boundingBox
                val left = maxOf(0, boundingBox.left)
                val top = maxOf(0, boundingBox.top)
                val right = minOf(imageWidth, boundingBox.right)
                val bottom = minOf(imageHeight, boundingBox.bottom)
                val width = right - left
                val height = bottom - top

                if (width <= 0 || height <= 0) {
                    onDetectionResult(emptyList(), imageWidth, imageHeight, rotationDegrees)
                    isProcessing = false
                    onComplete()
                    return@addOnSuccessListener
                }

                try {
                    val rawBitmap = imageProxy.toBitmap()
                    val croppedBitmap = Bitmap.createBitmap(rawBitmap, left, top, width, height)
                    
                    // The croppedBitmap matches the raw image orientation, so we pass rotationDegrees
                    val croppedInputImage = InputImage.fromBitmap(
                        croppedBitmap,
                        rotationDegrees
                    )

                    labeler.process(croppedInputImage)
                        .addOnSuccessListener { labels ->
                            val bestLabel = labels.maxByOrNull { it.confidence }
                            val labelText = bestLabel?.text ?: "Object"
                            val confidence = bestLabel?.confidence ?: 1.0f

                            val detection = DetectedObjectUi(
                                label = labelText,
                                confidence = confidence,
                                boundingBox = boundingBox
                            )

                            Log.d(
                                "ObjectDetection",
                                "Detected single object: $labelText (${(confidence * 100).toInt()}%) at $boundingBox"
                            )

                            onDetectionResult(listOf(detection), imageWidth, imageHeight, rotationDegrees)
                        }
                        .addOnFailureListener { e ->
                            Log.e("ObjectDetection", "Image labeling failed", e)
                            // Fallback to generic object bounding box
                            val detection = DetectedObjectUi(
                                label = "Object",
                                confidence = 1.0f,
                                boundingBox = boundingBox
                            )
                            onDetectionResult(listOf(detection), imageWidth, imageHeight, rotationDegrees)
                        }
                        .addOnCompleteListener {
                            isProcessing = false
                            onComplete()
                        }

                } catch (e: Exception) {
                    Log.e("ObjectDetection", "Bitmap crop failed", e)
                    onDetectionResult(emptyList(), imageWidth, imageHeight, rotationDegrees)
                    isProcessing = false
                    onComplete()
                }
            }
            .addOnFailureListener { e ->
                Log.e("ObjectDetection", "Object detection failed", e)
                onDetectionResult(emptyList(), imageWidth, imageHeight, rotationDegrees)
                isProcessing = false
                onComplete()
            }
    }
}