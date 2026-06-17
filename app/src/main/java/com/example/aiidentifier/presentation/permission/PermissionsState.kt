package com.example.aiidentifier.presentation.permission


sealed interface CameraPermissionState {

    data object Initial : CameraPermissionState

    data object Granted : CameraPermissionState

    data object Denied : CameraPermissionState

    data object PermanentlyDenied : CameraPermissionState
}