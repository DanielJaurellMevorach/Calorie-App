package com.example.anothercalorieapp.ui.components.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.anothercalorieapp.ui.utils.getResponsiveFontSize
import com.example.anothercalorieapp.ui.utils.getResponsiveIconSize
import com.example.anothercalorieapp.ui.utils.getResponsiveSize

@Composable
fun NutrientMeter(
    nutrient: String,
    currentValue: Int,
    color: Color,
    maxValue: Int,
    icon : ImageVector,
    modifier: Modifier = Modifier
) {
    val strokeWidth = getResponsiveSize(10.dp) // Move this outside Canvas

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier.size(getResponsiveSize(60.dp)),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(
                    color = Color.LightGray.copy(alpha = 0.3f),
                    radius = size.width / 2,
                    style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
                )
            }

            val progress = currentValue.toFloat() / maxValue.toFloat()
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawArc(
                    color = color,
                    startAngle = -90f,
                    sweepAngle = 360f * progress,
                    useCenter = false,
                    style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
                )
            }

            Box(
                modifier = Modifier.fillMaxSize(0.5f),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = nutrient,
                    tint = color,
                    modifier = Modifier.size(getResponsiveIconSize())
                )
            }
        }

        Spacer(modifier = Modifier.height(getResponsiveSize(16.dp)))

        Text(
            text = "${currentValue}g",
            fontSize = getResponsiveFontSize(16.sp),
            fontWeight = FontWeight.W900,
            color = Color.Black
        )

        Text(
            text = "$nutrient left",
            fontSize = getResponsiveFontSize(14.sp),
            color = Color.Gray,
            fontWeight = FontWeight.W400,
            maxLines = 1
        )
    }
}
