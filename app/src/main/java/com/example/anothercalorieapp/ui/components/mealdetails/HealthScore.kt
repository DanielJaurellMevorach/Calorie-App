package com.example.anothercalorieapp.ui.components.mealdetails

import android.annotation.SuppressLint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.anothercalorieapp.ui.utils.getResponsiveFontSize
import com.example.anothercalorieapp.ui.utils.getResponsivePadding
import com.example.anothercalorieapp.ui.utils.getResponsiveSize
import kotlin.math.min

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun HealthScore(
    modifier: Modifier = Modifier,
    healthGrade: String,
    maxScore: Int = 10,
) {
    val density = LocalDensity.current

    // Define grade mapping - A=6, B=5, C=4, D=3, E=2, F=1
    val gradeToProgress = mapOf(
        "A" to 6f,
        "B" to 5f,
        "C" to 4f,
        "D" to 3f,
        "E" to 2f,
        "F" to 1f
    )

    val progress = gradeToProgress[healthGrade.uppercase()] ?: 0f
    val maxProgress = 6f

    // Pre-calculate all responsive values outside Canvas
    val strokeWidthDp = getResponsiveSize(12.dp)
    val gapWidthDp = getResponsiveSize(24.dp)
    val circleRadiusDp = getResponsiveSize(8.dp)
    val canvasHeightDp = getResponsiveSize(20.dp)

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        // Label with letter grade
        Row() {
            Text(
                text = "Health Score: ",
                fontSize = getResponsiveFontSize(14.sp, minScale = 0.8f, maxScale = 1.0f),
                color = Color.Black,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.padding(bottom = getResponsivePadding(4.dp))
            )

            Text(
                text = "Grade $healthGrade",
                fontSize = getResponsiveFontSize(14.sp, minScale = 0.8f, maxScale = 1.0f),
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = getResponsivePadding(4.dp)).offset(x = getResponsivePadding(8.dp))
            )
        }

        Spacer(modifier = Modifier.height(getResponsivePadding(8.dp)))

        // Horizontal line with grade chunks
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = getResponsivePadding(8.dp))
                .height(getResponsiveSize(20.dp))
        ) {
            val canvasWidth = constraints.maxWidth.toFloat()
            val strokeWidth = with(density) { strokeWidthDp.toPx() }
            val gapWidth = with(density) { gapWidthDp.toPx() }

            // Define chunk structure: A (1 unit), BC (2 units), DE (2 units), F (1 unit)
            val chunkSizes = listOf(1f, 2f, 2f, 1f) // A, BC, DE, F
            val totalUnits = chunkSizes.sum()
            val totalGapWidth = (chunkSizes.size - 1) * gapWidth
            val unitWidth = (canvasWidth - totalGapWidth) / totalUnits

            var lastFilledChunkEndX = 0f
            var currentProgress = progress

            Canvas(modifier = Modifier.fillMaxWidth().height(canvasHeightDp)) {
                var currentX = 0f

                // Draw background chunks
                chunkSizes.forEach { chunkSize ->
                    val chunkWidth = unitWidth * chunkSize
                    val startX = currentX
                    val endX = currentX + chunkWidth

                    drawLine(
                        color = Color.LightGray,
                        start = Offset(startX, size.height / 2),
                        end = Offset(endX, size.height / 2),
                        strokeWidth = strokeWidth,
                        cap = StrokeCap.Round
                    )

                    currentX = endX + gapWidth
                }

                // Draw filled chunks
                currentX = 0f
                currentProgress = progress

                chunkSizes.forEach { chunkSize ->
                    val chunkWidth = unitWidth * chunkSize
                    val startX = currentX

                    if (currentProgress > 0f) {
                        // Calculate how much of this chunk to fill
                        val fillAmount = min(currentProgress, chunkSize)
                        val fillWidth = (fillAmount / chunkSize) * chunkWidth
                        val endX = startX + fillWidth

                        drawLine(
                            color = Color.Black,
                            start = Offset(startX, size.height / 2),
                            end = Offset(endX, size.height / 2),
                            strokeWidth = strokeWidth,
                            cap = StrokeCap.Round
                        )

                        lastFilledChunkEndX = endX
                        currentProgress -= fillAmount
                    }

                    currentX += chunkWidth + gapWidth
                }

                // Draw popup circle at the end of black progression
                if (progress > 0f) {
                    val circleRadius = with(density) { circleRadiusDp.toPx() }

                    // Draw black filled circle
                    drawCircle(
                        color = Color.Black,
                        radius = circleRadius,
                        center = Offset(lastFilledChunkEndX, size.height / 2)
                    )

                    // Draw white border circle
                    drawCircle(
                        color = Color.White,
                        radius = circleRadius,
                        center = Offset(lastFilledChunkEndX, size.height / 2),
                        style = Stroke(width = strokeWidth * 0.2f)
                    )
                }
            }
        }
    }
}
