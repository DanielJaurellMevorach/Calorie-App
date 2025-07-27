package com.example.caloriecounter.ui.pages

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.caloriecounter.ui.components.camera.Camera

@Composable
fun Scanner(
    onNavigateToNutritionResults: () -> Unit = {}
) {
    Camera(
        modifier = Modifier.fillMaxSize(),
        onNavigateToNutritionResults = onNavigateToNutritionResults
    )
}
