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
import com.example.responsiveness.ui.theme.DesignTokens
import kotlin.math.min

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun HealthScore(
    modifier: Modifier = Modifier,
    healthGrade: String, // Changed from healthScore: Int to healthGrade: String
    maxScore: Int = 10, // Keep for compatibility but won't be used in display
    tokens: DesignTokens.Tokens // Add tokens for responsiveness
) {
    val density = LocalDensity.current
    val gradeToProgress = mapOf(
        "A" to 6f,
        "B" to 5f,
        "C" to 4f,
        "D" to 3f,
        "E" to 2f,
        "F" to 1f
    )

    val progress = gradeToProgress[healthGrade.uppercase()] ?: 0f
    val maxProgress = 6f // Total chunks: A + BC(long) + DE(long) + F = 4 visual chunks, 6 progress units

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        // Label with letter grade
        Row() {
            Text(
                text = "Health Score: ",
                fontSize = tokens.nutrientTextSize,
                color = Color.Black,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.padding(bottom = tokens.sDp(4.dp))
            )

            Text(
                text = "Grade $healthGrade",
                fontSize = tokens.nutrientTextSize,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = tokens.sDp(4.dp)).offset(x = tokens.sDp(8.dp))
            )
        }

        Spacer(modifier = Modifier.height(tokens.sDp(8.dp)))

        // Horizontal line with grade chunks
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = tokens.sDp(8.dp))
                .height(tokens.sDp(20.dp))
        ) {
            val canvasWidth = constraints.maxWidth.toFloat()
            val strokeWidth = with(density) { tokens.sDp(12.dp).toPx() }
            val gapWidth = with(density) { tokens.sDp(24.dp).toPx() }

            // Define chunk structure: A (1 unit), BC (2 units), DE (2 units), F (1 unit)
            val chunkSizes = listOf(1f, 2f, 2f, 1f) // A, BC, DE, F
            val totalUnits = chunkSizes.sum()
            val totalGapWidth = (chunkSizes.size - 1) * gapWidth
            val unitWidth = (canvasWidth - totalGapWidth) / totalUnits

            var lastFilledChunkEndX = 0f
            var currentProgress = progress

            Canvas(modifier = Modifier.fillMaxWidth().height(tokens.sDp(20.dp))) {
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
                    val circleRadius = with(density) { tokens.sDp(8.dp).toPx() }

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
