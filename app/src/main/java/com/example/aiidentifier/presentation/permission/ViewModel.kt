package com.example.aiidentifier.presentation.permission

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class PermissionViewModel @Inject constructor() : ViewModel() {

    private val _permissionState =
        MutableStateFlow<CameraPermissionState>(
            CameraPermissionState.Initial
        )

    val permissionState =
        _permissionState.asStateFlow()

    fun updatePermissionState(
        state: CameraPermissionState
    ) {
        _permissionState.value = state
    }
}
