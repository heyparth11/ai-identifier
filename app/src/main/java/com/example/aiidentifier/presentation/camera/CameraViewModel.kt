package com.example.aiidentifier.presentation.camera

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aiidentifier.domain.DetectedObjectUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor() : ViewModel() {

    private val _uiState =
        MutableStateFlow(
            CameraUiState()
        )

    val uiState =
        _uiState.asStateFlow()

    private var clearTextJob: Job? = null
    private var clearObjectsJob: Job? = null

    fun updateRecognizedText(
        text: String
    ) {
        if (text.isNotBlank()) {
            clearTextJob?.cancel()
            _uiState.update {
                it.copy(
                    recognizedText = text
                )
            }
        } else {
            // If the incoming text is empty, wait for 1.5 seconds before clearing it.
            // This prevents the UI text overlay card from blinking/flickering.
            if (clearTextJob == null || clearTextJob?.isCompleted == true) {
                clearTextJob = viewModelScope.launch {
                    delay(1500)
                    _uiState.update {
                        it.copy(
                            recognizedText = ""
                        )
                    }
                }
            }
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
        if (detections.isNotEmpty()) {
            clearObjectsJob?.cancel()
            _uiState.update {
                it.copy(
                    detectedObjects = detections,
                    imageWidth = imageWidth,
                    imageHeight = imageHeight,
                    rotationDegrees = rotationDegrees
                )
            }
        } else {
            // Keep the previous detected objects visible for 1.5 seconds to smooth out frame dropouts.
            if (clearObjectsJob == null || clearObjectsJob?.isCompleted == true) {
                clearObjectsJob = viewModelScope.launch {
                    delay(1500)
                    _uiState.update {
                        it.copy(
                            detectedObjects = emptyList()
                        )
                    }
                }
            }
        }
    }

}