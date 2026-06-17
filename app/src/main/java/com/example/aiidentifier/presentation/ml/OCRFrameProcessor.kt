package com.example.aiidentifier.presentation.ml

import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageProxy
import com.example.aiidentifier.presentation.FrameProcessor
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition.getClient
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

class OcrFrameProcessor(
    private val onTextRecognized: (String) -> Unit
) : FrameProcessor {

    private var isProcessing = false
    private val recognizer =
        getClient(
            TextRecognizerOptions.DEFAULT_OPTIONS
        )

    @OptIn(ExperimentalGetImage::class)
    override fun process(
        imageProxy: ImageProxy,
        onComplete:()->Unit
    ) {

        if (isProcessing) {
            onComplete()
            return
        }
        isProcessing = true

        val mediaImage =
            imageProxy.image


        if (mediaImage == null){
            isProcessing = false
            onComplete()
            return
        }

        val image =
            InputImage.fromMediaImage(
                mediaImage,
                imageProxy.imageInfo.rotationDegrees
            )

        recognizer.process(image)
            .addOnSuccessListener { result ->

                onTextRecognized(
                    result.text
                )
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