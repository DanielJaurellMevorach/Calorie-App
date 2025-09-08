package com.example.responsiveness.ui.screens.scanner

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollFactory
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.example.responsiveness.MealDetailPageLoadingRoute
import com.example.responsiveness.ui.screens.scanner.components.CustomBottomSheet
import com.example.responsiveness.ui.screens.scanner.viewmodel.ScannerViewModel
import com.example.responsiveness.ui.theme.DesignTokens
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

private const val TAG = "CalorieChief"
private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
private val REQUIRED_PERMISSIONS = mutableListOf(
    Manifest.permission.CAMERA,
).apply {
    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
        add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }
}.toTypedArray()

@OptIn(ExperimentalFoundationApi::class)
@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun ScannerScreen(
    previewView: PreviewView,
    navController: NavController,
    selectedTimeOfDay: String? = null,
    viewModel: ScannerViewModel,
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val hasPermissions by viewModel.hasPermissions.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val imageCapture by viewModel.imageCapture.collectAsState()
    val camera by viewModel.camera.collectAsState()
    val isFlashOn by viewModel.isFlashOn.collectAsState()
    val isUsingBackCamera by viewModel.isUsingBackCamera.collectAsState()

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val cameraExecutor: ExecutorService = remember { Executors.newSingleThreadExecutor() }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.all { it.value }) {
            viewModel.onPermissionsGranted()
            startCamera(
                context,
                lifecycleOwner,
                previewView,
                isUsingBackCamera
            ) { success, error, captureInstance, cameraInstance ->
                if (success) {
                    viewModel.onCameraReady(captureInstance, cameraInstance)
                } else viewModel.onError(error)
            }
        } else {
            viewModel.onPermissionsDenied()
        }
    }

    LaunchedEffect(isUsingBackCamera) {
        if (hasPermissions) {
            startCamera(
                context,
                lifecycleOwner,
                previewView,
                isUsingBackCamera
            ) { success, error, captureInstance, cameraInstance ->
                if (success) {
                    viewModel.onCameraReady(captureInstance, cameraInstance)
                } else viewModel.onError(error)
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.setFlashState(false) // Always reset flash state on entry
        if (allPermissionsGranted(context)) {
            viewModel.onPermissionsGranted()
            startCamera(
                context,
                lifecycleOwner,
                previewView,
                isUsingBackCamera
            ) { success, error, captureInstance, cameraInstance ->
                if (success) {
                    viewModel.onCameraReady(captureInstance, cameraInstance)
                    cameraInstance?.cameraControl?.enableTorch(false) // Ensure torch is off
                } else viewModel.onError(error)
            }
        } else {
            permissionLauncher.launch(REQUIRED_PERMISSIONS)
        }
    }

    val instructions = listOf(
        "Keep the meal you wish to register in the focusing frame",
        "Hold the device steady when capturing",
        "Use the flash button to toggle torch on/off",
        "Switch between front and rear cameras",
        "If issues persist, change lighting conditions",
        "If meal analysis isn't accurate, prompt correction with explanation"
    )

    CompositionLocalProvider(
        LocalOverscrollFactory provides null
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val tokens = DesignTokens.provideTokens(availableWidth = maxWidth, availableHeight = maxHeight)

            // Camera preview
            Box(modifier = Modifier.fillMaxSize()) {
                if (hasPermissions && errorMessage == null) {
                    AndroidView(
                        factory = { previewView }, // Use stable PreviewView instance
                        modifier = Modifier.fillMaxSize()
                    )
                }
                if (isLoading) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                if (errorMessage != null) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(errorMessage!!, color = Color.White, fontSize = tokens.headerFont)
                    }
                }
            }

            // New bottom sheet
            if (hasPermissions && errorMessage == null && !isLoading) {
                CustomBottomSheet(
                    onDismiss = {},
                    tokens = tokens,
                    isFlashOn = isFlashOn,
                    onSwitchCamera = { viewModel.onSwitchCamera() },
                    onToggleFlash = {
                        camera?.let { cam ->
                            if (cam.cameraInfo.hasFlashUnit()) {
                                val newFlashState = !isFlashOn
                                viewModel.setFlashState(newFlashState)
                                cam.cameraControl.enableTorch(newFlashState)
                            }
                        }
                    },
                    onCapture = {
                        imageCapture?.let { takePhoto(context, it, navController, selectedTimeOfDay) }
                    },
                    instructions = instructions
                )
            }
        }
    }

    DisposableEffect(Unit) { onDispose { cameraExecutor.shutdown() } }
}

private fun startCamera(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    previewView: PreviewView,
    useBackCamera: Boolean,
    onComplete: (Boolean, String?, ImageCapture?, Camera?) -> Unit = { _, _, _, _ -> }
) {
    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

    cameraProviderFuture.addListener({
        try {
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

            val imageCapture = ImageCapture.Builder().build()

            val cameraSelector = if (useBackCamera) {
                CameraSelector.DEFAULT_BACK_CAMERA
            } else {
                CameraSelector.DEFAULT_FRONT_CAMERA
            }

            cameraProvider.unbindAll()
            val camera = cameraProvider.bindToLifecycle(
                lifecycleOwner, cameraSelector, preview, imageCapture
            )

            onComplete(true, null, imageCapture, camera)
        } catch (exc: Exception) {
            Log.e(TAG, "Use case binding failed", exc)
            onComplete(false, "Camera initialization failed: ${exc.message}", null, null)
        }
    }, ContextCompat.getMainExecutor(context))
}

private fun takePhoto(
    context: Context,
    imageCapture: ImageCapture,
    navController: NavController,
    selectedTimeOfDay: String? = null // NEW: pass selected time of day
) {
    // Create time stamped name for the file
    val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis())

    // Create a file in app-specific storage that will persist
    val photoFile = File(
        context.getExternalFilesDir("meal_images"), // Use dedicated meal_images directory
        "$name.jpg"
    )

    // Ensure the directory exists
    photoFile.parentFile?.mkdirs()

    // Create output options object for file-based storage
    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

    // Set up image capture listener
    imageCapture.takePicture(
        outputOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onError(exception: ImageCaptureException) {
                Log.e(TAG, "Photo capture failed: ${exception.message}", exception)
                Toast.makeText(context, "Photo capture failed: ${exception.message}", Toast.LENGTH_SHORT).show()
            }

            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                Log.d(TAG, "Photo capture succeeded, path: ${photoFile.absolutePath}")
                Log.d(TAG, "Scanner takePhoto: selectedTimeOfDay=$selectedTimeOfDay")
                // Pass the correct URI string for local file
                val imageUri = "file://${photoFile.absolutePath}"
                // Pass selectedTimeOfDay to MealDetailPageLoadingRoute
                navController.navigate(MealDetailPageLoadingRoute(imageUri = imageUri, selectedTimeOfDay = selectedTimeOfDay))
            }
        }
    )
}

private fun allPermissionsGranted(context: Context) = REQUIRED_PERMISSIONS.all {
    ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
}