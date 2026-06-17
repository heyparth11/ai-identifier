package com.example.aiidentifier.presentation

import android.graphics.Rect
import android.graphics.RectF
import android.util.Log
import androidx.camera.core.CameraSelector.DEFAULT_BACK_CAMERA
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.aiidentifier.presentation.camera.CameraViewModel
import com.example.aiidentifier.presentation.camera.FrameAnalyzer
import com.example.aiidentifier.presentation.ml.ObjectDetectionProcessor
import java.util.concurrent.Executors

@Composable
fun CameraPreview(
    modifier: Modifier = Modifier,
    onFrameProcessed: () -> Unit,
    viewModel: CameraViewModel
) {
    Log.d("ObjectDetection", "CameraPreview Composable")
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    val processor = remember {
        ObjectDetectionProcessor { detections, imageWidth, imageHeight, rotationDegrees ->
            viewModel.updateDetections(detections, imageWidth, imageHeight, rotationDegrees)
            onFrameProcessed()
        }
    }

    val imageAnalysis = remember {
        ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
            .apply {
                setAnalyzer(cameraExecutor, FrameAnalyzer(processor))
            }
    }

    DisposableEffect(Unit) {
        onDispose {
            cameraExecutor.shutdown()
        }
    }

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val uiState by viewModel.uiState.collectAsState()

    var containerWidth by remember { mutableStateOf(0f) }
    var containerHeight by remember { mutableStateOf(0f) }

    Box(
        modifier = modifier.onGloballyPositioned { coordinates ->
            containerWidth = coordinates.size.width.toFloat()
            containerHeight = coordinates.size.height.toFloat()
        }
    ) {
        AndroidView(
            factory = { ctx ->
                Log.d("ObjectDetection", "AndroidView factory called")
                val previewView = PreviewView(ctx)
                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder().build()
                    preview.surfaceProvider = previewView.surfaceProvider
                    val cameraSelector = DEFAULT_BACK_CAMERA

                    try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            preview,
                            imageAnalysis
                        )
                    } catch (e: Exception) {
                        Log.e("ObjectDetection", "Bind failed", e)
                    }
                }, ContextCompat.getMainExecutor(ctx))

                previewView
            },
            modifier = Modifier.fillMaxSize()
        )

        // Draw bounding box overlay
        if (containerWidth > 0 && containerHeight > 0 && uiState.imageWidth > 0 && uiState.imageHeight > 0) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                uiState.detectedObjects.forEach { detection ->
                    val scaledRect = scaleRect(
                        boundingBox = detection.boundingBox,
                        imageWidth = uiState.imageWidth,
                        imageHeight = uiState.imageHeight,
                        viewWidth = containerWidth,
                        viewHeight = containerHeight,
                        rotationDegrees = uiState.rotationDegrees
                    )

                    // Draw bounding box
                    drawRect(
                        color = Color.Green,
                        topLeft = androidx.compose.ui.geometry.Offset(scaledRect.left, scaledRect.top),
                        size = androidx.compose.ui.geometry.Size(scaledRect.width(), scaledRect.height()),
                        style = Stroke(width = 3.dp.toPx())
                    )
                }
            }
        }
    }
}

private fun scaleRect(
    boundingBox: Rect,
    imageWidth: Int,
    imageHeight: Int,
    viewWidth: Float,
    viewHeight: Float,
    rotationDegrees: Int
): RectF {
    val effectiveImgWidth = if (rotationDegrees == 90 || rotationDegrees == 270) imageHeight else imageWidth
    val effectiveImgHeight = if (rotationDegrees == 90 || rotationDegrees == 270) imageWidth else imageHeight

    // Calculate scale and offsets for center crop (FILL_CENTER)
    val scale = maxOf(viewWidth / effectiveImgWidth, viewHeight / effectiveImgHeight)
    val offsetX = (viewWidth - effectiveImgWidth * scale) / 2f
    val offsetY = (viewHeight - effectiveImgHeight * scale) / 2f

    val left: Float
    val top: Float
    val right: Float
    val bottom: Float

    // Map coordinates based on rotation
    when (rotationDegrees) {
        90 -> {
            left = (imageHeight - boundingBox.bottom) * scale + offsetX
            top = boundingBox.left * scale + offsetY
            right = (imageHeight - boundingBox.top) * scale + offsetX
            bottom = boundingBox.right * scale + offsetY
        }
        270 -> {
            left = boundingBox.top * scale + offsetX
            top = (imageWidth - boundingBox.right) * scale + offsetY
            right = boundingBox.bottom * scale + offsetX
            bottom = (imageWidth - boundingBox.left) * scale + offsetY
        }
        180 -> {
            left = (imageWidth - boundingBox.right) * scale + offsetX
            top = (imageHeight - boundingBox.bottom) * scale + offsetY
            right = (imageWidth - boundingBox.left) * scale + offsetX
            bottom = (imageHeight - boundingBox.top) * scale + offsetY
        }
        else -> { // 0
            left = boundingBox.left * scale + offsetX
            top = boundingBox.top * scale + offsetY
            right = boundingBox.right * scale + offsetX
            bottom = boundingBox.bottom * scale + offsetY
        }
    }

    return RectF(left, top, right, bottom)
}