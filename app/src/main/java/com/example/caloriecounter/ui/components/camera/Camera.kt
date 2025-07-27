package com.example.caloriecounter.ui.components.camera

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope
import com.example.caloriecounter.ui.components.openai.analyzeMealImageWithOpenAI
import com.example.caloriecounter.ui.components.openai.NutritionResultsViewModel
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Camera(
    modifier: Modifier = Modifier,
    onNavigateToNutritionResults: () -> Unit = {}
) {
    val context = LocalContext.current
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    val scope = rememberCoroutineScope()
    val scaffoldState = rememberBottomSheetScaffoldState()
    val controller = remember(context) {
        LifecycleCameraController(context).apply {
            setEnabledUseCases(CameraController.IMAGE_CAPTURE or CameraController.VIDEO_CAPTURE)
        }
    }

    // Get ViewModel instance properly
    val nutritionViewModel = viewModel<NutritionResultsViewModel>()

    fun takePhoto(
        controller: LifecycleCameraController,
        scope: CoroutineScope,
        onPhotoTaken: (Bitmap) -> Unit,
        onNavigateToNutritionResults: () -> Unit
    ) {
        controller.takePicture(
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    super.onCaptureSuccess(image)
                    val matrix = Matrix().apply {
                        postRotate(image.imageInfo.rotationDegrees.toFloat())
                        if (controller.cameraSelector == CameraSelector.DEFAULT_FRONT_CAMERA) {
                            postScale(-1f, 1f)
                        }
                    }

                    val rotated = Bitmap.createBitmap(
                        image.toBitmap(),
                        0, 0, image.width, image.height,
                        matrix, true
                    )

                    image.close()
                    onPhotoTaken(rotated)

                    // Comment out loading state and immediate navigation
                    // nutritionViewModel.startLoading()
                    // onNavigateToNutritionResults()

                    // Save bitmap to temporary file and analyze with OpenAI in background
                    scope.launch {
                        try {
                            val tempFile = File(context.cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")
                            val outputStream = FileOutputStream(tempFile)
                            rotated.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
                            outputStream.flush()
                            outputStream.close()

                            // Replace with your actual OpenAI API key
                            val apiKey = "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"

                            // Call OpenAI API on IO dispatcher
                            val result = withContext(Dispatchers.IO) {
                                analyzeMealImageWithOpenAI(apiKey, tempFile.absolutePath)
                            }

                            Log.d("OpenAI", "Analysis result: $result")

                            // Update ViewModel and navigate only after result is ready
                            withContext(Dispatchers.Main) {
                                nutritionViewModel.updateNutritionData(result)
                                onNavigateToNutritionResults()
                            }

                            // Clean up temporary file
                            tempFile.delete()
                        } catch (e: IOException) {
                            Log.e("Camera", "Failed to save or analyze image", e)
                            // Update ViewModel with error result on failure and navigate
                            val errorResult = """{"meal_name":"Analysis Error","ingredients":[],"nutrition":{"energy_kcal":0,"protein_g":0,"carbohydrates_g":0,"fat_g":0,"fiber_g":0,"sugars_g":0,"sodium_mg":0,"cholesterol_mg":0,"water_l":0},"meal_nutrition_score":"F","error":"Failed to analyze image"}"""

                            withContext(Dispatchers.Main) {
                                nutritionViewModel.updateNutritionData(errorResult)
                                onNavigateToNutritionResults()
                            }
                        }
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e("Camera", "Photo capture failed", exception)
                }
            }
        )
    }

    val viewModel = viewModel<MainViewModel>()
    val bitmaps by viewModel.bitmaps.collectAsState()

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 0.dp,
        sheetContent = {
            PhotoBottomSheetContent(
                bitmaps = bitmaps,
                modifier = Modifier.fillMaxWidth()
            )
        },
        modifier = modifier
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Fullscreen Camera Preview
            if (hasCameraPermission) {
                CameraPreview(
                    controller = controller,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Blurred Overlay With Transparent Cutout
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .drawWithContent {
                        drawContent()
                        val blurModifier = Modifier.blur(30.dp)
                        drawRect(Color.Black.copy(alpha = 0.2f)) // darken for contrast

                        val squareSize = size.width - 64.dp.toPx() * 2
                        val topLeft = Offset(
                            x = (size.width - squareSize) / 2,
                            y = (size.height - squareSize) / 2
                        )

                        drawRoundRect(
                            color = Color.Transparent,
                            topLeft = topLeft,
                            size = Size(squareSize, squareSize),
                            cornerRadius = CornerRadius(32.dp.toPx()),
                            blendMode = BlendMode.Clear
                        )
                    }
                    .blur(30.dp)
            )

            // White Corner Overlay
            CameraOverlay(cornerColor = Color.White)

            // Scanning text
            Text(
                text = "Scanning...",
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 64.dp),
                style = MaterialTheme.typography.titleLarge
            )

            // Camera Switch Button
            IconButton(
                onClick = {
                    controller.cameraSelector =
                        if (controller.cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA)
                            CameraSelector.DEFAULT_FRONT_CAMERA
                        else
                            CameraSelector.DEFAULT_BACK_CAMERA
                },
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Cameraswitch,
                    contentDescription = "Switch Camera",
                    tint = Color.Black
                )
            }

            // Capture & Gallery Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                IconButton(onClick = {
                    scope.launch { scaffoldState.bottomSheetState.expand() }
                }) {
                    Icon(
                        imageVector = Icons.Default.Photo,
                        contentDescription = "Gallery",
                        tint = Color.Black
                    )
                }

                IconButton(onClick = {
                    takePhoto(
                        controller = controller,
                        scope = scope,
                        onPhotoTaken = { bitmap ->
                            viewModel.onTakePhoto(bitmap)
                        },
                        onNavigateToNutritionResults = onNavigateToNutritionResults
                    )
                }) {
                    Icon(
                        imageVector = Icons.Default.PhotoCamera,
                        contentDescription = "Capture",
                        tint = Color.Black
                    )
                }
            }
        }
    }
}

@Composable
fun CameraOverlay(
    cornerColor: Color = Color.White,
    cornerLength: Dp = 32.dp,
    strokeWidth: Dp = 3.dp
) {
    Box(modifier = Modifier.fillMaxSize()) {
        val hLine = Modifier
            .width(cornerLength)
            .height(strokeWidth)
            .background(cornerColor)

        val vLine = Modifier
            .width(strokeWidth)
            .height(cornerLength)
            .background(cornerColor)

        val pad = 32.dp

        // Top-Left
        Box(Modifier.offset(pad, pad)) {
            Box(hLine)
            Box(vLine)
        }

        // Top-Right
        Box(
            Modifier
                .align(Alignment.TopEnd)
                .offset(-pad, pad)
        ) {
            Box(hLine.align(Alignment.TopEnd))
            Box(vLine.align(Alignment.TopEnd))
        }

        // Bottom-Left
        Box(
            Modifier
                .align(Alignment.BottomStart)
                .offset(pad, -pad)
        ) {
            Box(hLine.align(Alignment.BottomStart))
            Box(vLine.align(Alignment.BottomStart))
        }

        // Bottom-Right
        Box(
            Modifier
                .align(Alignment.BottomEnd)
                .offset(-pad, -pad)
        ) {
            Box(hLine.align(Alignment.BottomEnd))
            Box(vLine.align(Alignment.BottomEnd))
        }
    }
}
