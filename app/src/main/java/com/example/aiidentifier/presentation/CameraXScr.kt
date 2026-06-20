package com.example.aiidentifier.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aiidentifier.presentation.camera.CameraViewModel
import com.example.aiidentifier.presentation.camera.scaleRect
import com.example.aiidentifier.presentation.ml.ObjectDetectionProcessor

@Composable
fun CameraXScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    viewModel: CameraViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    val processor = remember {
        ObjectDetectionProcessor { detections, imageWidth, imageHeight, rotationDegrees ->
            viewModel.updateDetections(detections, imageWidth, imageHeight, rotationDegrees)
            viewModel.onFrameProcessed()
        }
    }

    var containerWidth by remember { mutableStateOf(0f) }
    var containerHeight by remember { mutableStateOf(0f) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Top Bar with White background wrapping status bar area cleanly
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .statusBarsPadding()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFF1E293B) // Slate 800
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Object Identifier",
                    color = Color(0xFF1E293B), // Slate 800
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }
        }

        // Camera Preview and overlays
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .onGloballyPositioned { coordinates ->
                    containerWidth = coordinates.size.width.toFloat()
                    containerHeight = coordinates.size.height.toFloat()
                }
        ) {
            // Camera View
            CameraPreview(
                modifier = Modifier.fillMaxSize(),
                frameProcessor = processor
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

            // Card displaying results at the bottom (Premium themed Slate 900)
            if (uiState.detectedObjects.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 24.dp)
                        .align(Alignment.BottomCenter),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A).copy(alpha = 0.85f)),
                    border = BorderStroke(
                        1.dp,
                        Brush.linearGradient(
                            colors = listOf(Color.White.copy(alpha = 0.2f), Color.White.copy(alpha = 0.05f))
                        )
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "IDENTIFIED OBJECTS",
                                color = Color(0xFF38BDF8), // Light Blue
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.5.sp
                            )
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(Color(0xFF34D399), CircleShape) // Emerald active indicator
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        uiState.detectedObjects.forEachIndexed { index, detection ->
                            if (index > 0) Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = detection.label,
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                // Badge for confidence
                                Box(
                                    modifier = Modifier
                                        .background(Color(0xFF334155), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "${(detection.confidence * 100).toInt()}%",
                                        color = Color(0xFF38BDF8),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}