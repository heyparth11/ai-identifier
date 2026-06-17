package com.example.aiidentifier.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.aiidentifier.presentation.camera.CameraViewModel

@Composable
fun CameraXScreen(modifier: Modifier, viewModel: CameraViewModel = hiltViewModel()) {

    val uiState by viewModel
        .uiState
        .collectAsState()

    Box {
        CameraPreview(
            modifier = modifier,
            onFrameProcessed = {
                viewModel.onFrameProcessed()
            },
            viewModel = viewModel
        )
//        Text(
//            text = uiState.recognizedText,
//            modifier = Modifier
//                .align(Alignment.BottomCenter)
//                .padding(16.dp)
//        )

        Text(
            text = uiState.detectedObjects.joinToString {
                "${it.label} ${(it.confidence * 100).toInt()}%"
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
                .offset(y = -20.dp)
        )
        Text(
            text = "Objects: ${uiState.detectedObjects.size}",
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)

        )
//        Text(
//            text = "asdfsadfsd",
//            modifier = Modifier
//                .align(Alignment.BottomCenter)
//                .padding(16.dp)
//        )
    }
}