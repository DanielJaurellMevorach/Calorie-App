package com.android.example.cameraxapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
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
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.composables.icons.lucide.Camera
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.SwitchCamera
import com.composables.icons.lucide.Zap
import com.composables.icons.lucide.ZapOff
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

private const val TAG = "CameraXApp"
private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
private val REQUIRED_PERMISSIONS = mutableListOf(
    Manifest.permission.CAMERA,
    Manifest.permission.RECORD_AUDIO
).apply {
    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
        add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }
}.toTypedArray()

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun CameraScreen(
    previewView: PreviewView,
    navController: NavController
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val density = LocalDensity.current

    var isLoading by remember { mutableStateOf(true) }
    var hasPermissions by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }
    var camera: Camera? by remember { mutableStateOf(null) }
    var isFlashOn by remember { mutableStateOf(false) }
    var isUsingBackCamera by remember { mutableStateOf(true) }

    val cameraExecutor: ExecutorService = remember { Executors.newSingleThreadExecutor() }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.all { it.value }
        if (allGranted) {
            hasPermissions = true
            startCamera(context, lifecycleOwner, previewView, isUsingBackCamera) { success, error, captureInstance, cameraInstance ->
                isLoading = false
                if (success) {
                    imageCapture = captureInstance
                    camera = cameraInstance
                } else {
                    errorMessage = error ?: "Failed to start camera"
                }
            }
        } else {
            isLoading = false
            errorMessage = "Camera permission is required"
            Toast.makeText(context, "Permissions not granted", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        if (allPermissionsGranted(context)) {
            hasPermissions = true
            startCamera(context, lifecycleOwner, previewView, isUsingBackCamera) { success, error, captureInstance, cameraInstance ->
                isLoading = false
                if (success) {
                    imageCapture = captureInstance
                    camera = cameraInstance
                } else {
                    errorMessage = error ?: "Failed to start camera"
                }
            }
        } else {
            permissionLauncher.launch(REQUIRED_PERMISSIONS)
        }
    }

    // Restart camera when switching between front/back
    LaunchedEffect(isUsingBackCamera) {
        if (hasPermissions && errorMessage == null) {
            startCamera(context, lifecycleOwner, previewView, isUsingBackCamera) { success, error, captureInstance, cameraInstance ->
                if (success) {
                    imageCapture = captureInstance
                    camera = cameraInstance
                    // Reset flash state when switching cameras
                    isFlashOn = false
                } else {
                    errorMessage = error ?: "Failed to start camera"
                }
            }
        }
    }

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {
        val fullHeight = with(density) { constraints.maxHeight.toDp() }

        // Define the minimum and maximum height for the draggable sheet
        val minSheetHeight = 120.dp
        val maxSheetHeight = fullHeight * 0.475f
        val sheetInitialHeight = minSheetHeight

        // Use a mutableState to store the current sheet height
        var sheetHeight by remember { mutableStateOf(sheetInitialHeight) }

        // Camera preview background
        Box(modifier = Modifier.fillMaxSize()) {
            if (hasPermissions && errorMessage == null) {
                AndroidView(
                    factory = { previewView },
                    modifier = Modifier.fillMaxSize()
                )
            }

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            if (errorMessage != null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = errorMessage!!,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Bottom sheet with camera controls
        if (hasPermissions && errorMessage == null && !isLoading) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(fullHeight)
                    .offset { IntOffset(x = 0, y = (fullHeight - sheetHeight).roundToPx()) }
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .background(Color.White)
            ) {
                // Drag handle and gesture detector
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(32.dp)
                        .pointerInput(Unit) {
                            detectVerticalDragGestures { change, dragAmount ->
                                change.consume()
                                val newHeight = sheetHeight - with(density) { dragAmount.toDp() }
                                sheetHeight = newHeight.coerceIn(minSheetHeight, maxSheetHeight)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Spacer(
                        modifier = Modifier
                            .height(4.dp)
                            .width(40.dp)
                            .background(Color.Gray.copy(alpha = 0.5f), RoundedCornerShape(2.dp))
                    )
                }

                // Camera controls
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                        .offset(y = (-16).dp)
                    ,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    // Camera switch button
                    IconButton(
                        onClick = {
                            isUsingBackCamera = !isUsingBackCamera
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .background(Color.Gray.copy(alpha = 0.1f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Lucide.SwitchCamera,
                            contentDescription = "Switch Camera",
                            tint = Color.Black,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Capture Button (center)
                    FloatingActionButton(
                        onClick = {
                            imageCapture?.let { capture ->
                                // Pass the navController to takePhoto
                                takePhoto(context, capture, navController)
                            }
                        },
                        modifier = Modifier.size(64.dp),
                        shape = CircleShape,
                        containerColor = Color.Black,
                        contentColor = Color.White
                    ) {
                        Icon(
                            imageVector = Lucide.Camera,
                            contentDescription = "Take Photo",
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // Flash toggle button
                    IconButton(
                        onClick = {
                            camera?.let { cam ->
                                if (cam.cameraInfo.hasFlashUnit()) {
                                    isFlashOn = !isFlashOn
                                    cam.cameraControl.enableTorch(isFlashOn)
                                } else {
                                    Toast.makeText(context, "Flash not available", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                if (isFlashOn) Color.Yellow.copy(alpha = 0.2f) else Color.Gray.copy(alpha = 0.1f),
                                CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = if (isFlashOn) Lucide.Zap else Lucide.ZapOff,
                            contentDescription = if (isFlashOn) "Turn Flash Off" else "Turn Flash On",
                            tint = if (isFlashOn) Color.Yellow else Color.Black,
                            modifier = Modifier.size(20.dp)
                        )
                    }


                }

                // Instructions (only visible when sheet is expanded)
                if (sheetHeight > minSheetHeight + 50.dp) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 16.dp)
                    ) {
                        Text(
                            text = "Camera Instructions",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        val instructions = listOf(
                            "Keep the meal you wish to register in the focusing frame",
                            "Only maintain the meal to be scanned in the frame",
                            "Hold the device steady when capturing",
                            "Use the flash button to toggle torch on/off",
                            "Switch between front and rear cameras",
                            "Upon failure, try again; if issues persist, try changing lighting conditions"
                        )

                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            instructions.forEach { instruction ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Text(
                                        text = "•",
                                        fontSize = 14.sp,
                                        color = Color.Black.copy(alpha = 0.8f),
                                        modifier = Modifier.padding(end = 8.dp)
                                    )
                                    Text(
                                        text = instruction,
                                        fontSize = 14.sp,
                                        color = Color.Black.copy(alpha = 0.8f),
                                        lineHeight = 20.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            cameraExecutor.shutdown()
        }
    }
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
    navController: NavController
) {
    // Create time stamped name for the file
    val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis())

    // Create a file in app-specific storage (won't appear in gallery)
    val photoFile = File(
        context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
        "$name.jpg"
    )

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
                // Create a file URI from the saved file
                val savedUri = Uri.fromFile(photoFile)
                Log.d(TAG, "Photo capture succeeded: $savedUri")

                // Encode the URI to be passed as a navigation argument
                val encodedUri = Uri.encode(savedUri.toString())

                // Navigate to the meal details screen, passing the image URI
                navController.navigate("meal_details_route/$encodedUri")
            }
        }
    )
}

private fun allPermissionsGranted(context: Context) = REQUIRED_PERMISSIONS.all {
    ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
}
