package com.example.aiidentifier.presentation.camera

import androidx.lifecycle.ViewModel
import com.example.aiidentifier.domain.DetectedObjectUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor() : ViewModel() {

    private val _uiState =
        MutableStateFlow(
            CameraUiState()
        )

    val uiState =
        _uiState.asStateFlow()


    fun updateRecognizedText(
        text: String
    ) {

        _uiState.update {
            it.copy(
                recognizedText = text
            )
        }
    }

    fun onFrameProcessed() {

        _uiState.update {

            it.copy(
                framesProcessed =
                    it.framesProcessed + 1
            )
        }
    }

    fun updateDetections(
        detections: List<DetectedObjectUi>,
        imageWidth: Int,
        imageHeight: Int,
        rotationDegrees: Int
    ) {

        _uiState.update {
            it.copy(
                detectedObjects = detections,
                imageWidth = imageWidth,
                imageHeight = imageHeight,
                rotationDegrees = rotationDegrees
            )
        }
    }

}