package com.example.responsiveness.ui.screens.home.components

import android.annotation.SuppressLint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Zap
import com.example.responsiveness.ui.theme.DesignTokens
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun CalorieSpeedometer(
    currentCalories: Double,
    maxCalories: Double,
    tokens: DesignTokens.Tokens,
    modifier: Modifier = Modifier
) {
    // The main white container that holds both the text and the speedometer
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(tokens.sDp(38.dp)))
            .padding(tokens.sDp(16.dp))
    ) {
        // Row to place the text (left) and the speedometer (right) side-by-side
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(tokens.sDp(16.dp)) // handles spacing
        ) {
            val caloriesText = currentCalories.toInt().toString()
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Top
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFFAFAFA), RoundedCornerShape(50.dp))
                            .padding(tokens.sDp(8.dp))
                    ) {
                        Icon(
                            imageVector = Lucide.Zap,
                            contentDescription = "ZAP",
                            tint = Color(0xFF6B6B6B),
                            modifier = Modifier.size(tokens.sDp(16.dp))
                        )
                    }
                    Spacer(modifier = Modifier.width(tokens.sDp(8.dp)))
                    Text(
                        text = "Daily intake",
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        fontSize = tokens.nutrientTextSize
                    )
                }

                Spacer(modifier = Modifier.height(tokens.sDp(8.dp)))

                Column {
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFFAFAFA), RoundedCornerShape(50.dp))
                            .padding(horizontal = tokens.sDp(8.dp), vertical = tokens.sDp(2.dp))
                    ) {
                        Text(
                            text = "$caloriesText calories consumed",
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF6B6B6B),
                            fontSize = (tokens.nutrientTextSize.value - tokens.sSp(4.sp).value).sp
                        )
                    }
                    Spacer(modifier = Modifier.height(tokens.sDp(16.dp)))
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFFAFAFA), RoundedCornerShape(50.dp))
                            .padding(horizontal = tokens.sDp(8.dp), vertical = tokens.sDp(2.dp))
                    ) {
                        Text(
                            text = "${maxCalories.toInt() - currentCalories.toInt()} calories remain",
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF6B6B6B),
                            fontSize = (tokens.nutrientTextSize.value - tokens.sSp(4.sp).value).sp
                        )
                    }
                }
            }

            // Speedometer
            BoxWithConstraints(
                modifier = Modifier
                    .weight(1f)
                    .height(tokens.sDp(120.dp))
                    .padding(start = tokens.sDp(8.dp))
                    .graphicsLayer { rotationZ = 180f } // flip upside down
                    .offset(y = tokens.sDp((-24).dp))
            ) {
                val progress = (currentCalories / maxCalories).coerceIn(0.0, 1.0)
                val strokeWidth = with(LocalDensity.current) { tokens.speedometerStrokeWidth.toPx() }

                val totalSweep = 180f
                val gapAngle = 30f
                val segments = 3
                val segmentSweep = (totalSweep - gapAngle * (segments - 1)) / segments

                val backgroundStartAngle = 360f // <-- Inverted start angle

                val canvasWidth = constraints.maxWidth.toFloat()
                val canvasHeight = constraints.maxHeight.toFloat()

                val diameter = min(canvasWidth, canvasHeight * 2f) // half circle fits inside height
                val arcRect = Rect(
                    left = (canvasWidth - diameter) / 2f,
                    top = canvasHeight - (diameter / 2f),
                    right = (canvasWidth + diameter) / 2f,
                    bottom = canvasHeight + (diameter / 2f)
                )

                val progressPerSegment = 1f / segments.toFloat()
                val totalProgressInSegments = progress / progressPerSegment
                val endSegmentIndex = minOf(totalProgressInSegments.toInt(), segments - 1)
                val progressInCurrentSegment = totalProgressInSegments - endSegmentIndex
                val endSegmentSweep = progressInCurrentSegment * segmentSweep

                // Inverted indicator follows arc correctly
                val indicatorEndAngle = 360f - (
                        endSegmentIndex * (segmentSweep + gapAngle) +
                                endSegmentSweep.toFloat()
                        )

                Canvas(modifier = Modifier.fillMaxSize()) {
                    // Background arcs
                    var angle = backgroundStartAngle
                    repeat(segments) {
                        drawArc(
                            color = Color(0xFFFAFAFA),
                            startAngle = angle,
                            sweepAngle = -segmentSweep, // Negative sweep for inversion
                            useCenter = false,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                            topLeft = arcRect.topLeft,
                            size = arcRect.size
                        )
                        angle -= segmentSweep + gapAngle // Subtract for inversion
                    }

                    // Progress arcs
                    if (progress > 0f) {
                        var currentAngle = 360f // align with inverted arc system
                        repeat(segments) { segmentIndex ->
                            val sweepForThisSegment: Float = when {
                                segmentIndex < endSegmentIndex -> -segmentSweep // Negative sweep
                                segmentIndex == endSegmentIndex -> -endSegmentSweep.toFloat()
                                else -> 0f
                            }

                            if (sweepForThisSegment != 0f) {
                                drawArc(
                                    color = Color.Black,
                                    startAngle = currentAngle,
                                    sweepAngle = sweepForThisSegment,
                                    useCenter = false,
                                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                                    topLeft = arcRect.topLeft,
                                    size = arcRect.size
                                )
                            }
                            currentAngle -= segmentSweep + gapAngle
                        }

                        // Circle indicator
                        val radius = diameter / 2f
                        val centerX = arcRect.left + radius
                        val centerY = arcRect.top + radius

                        val angleRad = Math.toRadians(indicatorEndAngle.toDouble())

                        val circleRadius = strokeWidth / 2f // Make radius half of strokeWidth to fill it
                        val circleX = centerX + (radius * cos(angleRad)).toFloat()
                        val circleY = centerY + (radius * sin(angleRad)).toFloat()

                        drawCircle(
                            color = Color.Black,
                            radius = circleRadius,
                            center = Offset(circleX, circleY)
                        )
                        drawCircle(
                            color = Color.White,
                            radius = circleRadius,
                            center = Offset(circleX, circleY),
                            style = Stroke(width = strokeWidth * 0.15f)
                        )
                    }
                }
            }
        }
    }
}