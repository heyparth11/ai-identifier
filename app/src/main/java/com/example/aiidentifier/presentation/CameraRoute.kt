package com.example.aiidentifier.presentation

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.aiidentifier.presentation.permission.CameraPermissionState
import com.example.aiidentifier.presentation.permission.PermissionViewModel

@Composable
fun CameraRoute(
    modifier: Modifier,
    viewModel: PermissionViewModel = hiltViewModel()
) {

    val context = LocalContext.current

    val activity = context as Activity
    val permissionState by viewModel
        .permissionState
        .collectAsState()

    val permissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { granted ->

            viewModel.updatePermissionState(
                getPermissionState(
                    activity = activity,
                    isGranted = granted
                )
            )
        }

//    var permissionGranted by remember {
//        mutableStateOf(
//            checkSelfPermission(
//                context,
//                Manifest.permission.CAMERA
//            ) == PackageManager.PERMISSION_GRANTED
//        )
//    }
//
//    val permissionLauncher =
//        rememberLauncherForActivityResult(
//            contract = ActivityResultContracts.RequestPermission()
//        ) { granted ->
//            permissionGranted = granted
//        }

    LaunchedEffect(Unit) {

        val granted =
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED

        if (granted) {

            viewModel.updatePermissionState(
                CameraPermissionState.Granted
            )

        } else {

            permissionLauncher.launch(
                Manifest.permission.CAMERA
            )
        }
    }

    when (permissionState) {

        CameraPermissionState.Initial -> {
            CircularProgressIndicator()
        }

        CameraPermissionState.Granted -> {
            CameraXScreen(
                modifier = modifier.fillMaxSize()
            )
        }

        CameraPermissionState.Denied -> {
            PermissionDeniedScreen(
                onRetry = {
                    permissionLauncher.launch(
                        Manifest.permission.CAMERA
                    )
                }
            )
        }

        CameraPermissionState.PermanentlyDenied -> {
            PermissionPermanentlyDeniedScreen()
        }
    }

}

fun getPermissionState(
    activity: Activity,
    isGranted: Boolean
): CameraPermissionState {

    return when {

        isGranted -> {
            CameraPermissionState.Granted
        }

        ActivityCompat.shouldShowRequestPermissionRationale(
            activity,
            Manifest.permission.CAMERA
        ) -> {
            CameraPermissionState.Denied
        }

        else -> {
            CameraPermissionState.PermanentlyDenied
        }
    }
}