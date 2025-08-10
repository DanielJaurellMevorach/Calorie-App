package com.example.anothercalorieapp.ui.components.home

import CalorieSpeedometer
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun CircleOverlay(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(Color.Transparent)
    ) {
        // Background circle (taller and more pointed)
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            drawCircle(
                color = Color.Gray.copy(alpha = 0.1f),
                radius = canvasWidth * 1.3f, // Coverage
                center = Offset(canvasWidth / 2f, canvasHeight * 2.0f) // Adjust pointiness
            )
        }

        // Foreground stacked content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp)) // Calendar's offset
            CalendarCalories()

            Spacer(modifier = Modifier.height(36.dp))
            CalorieSpeedometer(
                currentCalories = 748,
                maxCalories = 1950,
            )
        }
    }
}
