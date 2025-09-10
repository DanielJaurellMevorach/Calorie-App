package com.example.responsiveness.ui.screens.profile.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.ChevronDown
import com.composables.icons.lucide.ChevronUp
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Minus
import com.composables.icons.lucide.Plus
import com.example.responsiveness.ui.theme.DesignTokens
import kotlin.math.min
import kotlin.math.roundToInt

@Composable
fun SettingAdjustableWheel(
    initialValue: Float,
    unitName: String,
    tokens: DesignTokens.Tokens,
    modifier: Modifier = Modifier,
    minValue: Float = 0f,
    maxValue: Float = 1000f,
    step: Float = 1f,
    onValueChange: (Float) -> Unit = {},
    defaultExpanded: Boolean = false,
    onExpandedChange: (Boolean) -> Unit = {},
    fallbackValue: Float
) {
    var currentValue by remember { mutableFloatStateOf(initialValue) }
    var expanded by remember { mutableStateOf(defaultExpanded) }

    // Sync internal state with external changes to initialValue
    LaunchedEffect(initialValue) {
        currentValue = initialValue
    }

    // Sync expanded state with parent
    LaunchedEffect(defaultExpanded) { expanded = defaultExpanded }
    val progress = (currentValue - minValue) / (maxValue - minValue)
    val chunkSizes = listOf(1f, 2f, 2f, 1f)
    val totalUnits = chunkSizes.sum()

    Box(
        modifier = modifier
            .background(
                color = Color.White,
                shape = RoundedCornerShape(tokens.sDp(38.dp))
            )
            .padding(horizontal = tokens.sDp(16.dp), vertical = tokens.sDp(20.dp)) // Changed to match CalendarCalories exactly - same padding on all sides
            .fillMaxWidth()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header row with title and chevron icon
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$unitName amount: ",
                        color = Color.Black,
                        fontWeight = FontWeight.Normal,
                        fontSize = tokens.sSp(14.sp), // Matches CalendarCalories month+year font size exactly
                        modifier = Modifier.padding(bottom = tokens.sDp(2.dp))
                    )
                    Text(
                        text = "${currentValue.roundToInt()}${if (unitName == "Calories") "" else "g"}",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = tokens.sSp(14.sp), // Matches CalendarCalories month+year font size exactly
                        modifier = Modifier.padding(bottom = tokens.sDp(2.dp))
                    )
                }

                Box(
                    modifier = Modifier
                        .then(Modifier) // keep any existing modifiers
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            expanded = !expanded
                            onExpandedChange(expanded)
                        }
                ) {
                    Icon(
                        imageVector = if (expanded) Lucide.ChevronUp else Lucide.ChevronDown,
                        contentDescription = if (expanded) "Collapse" else "Expand",
                        tint = Color.Gray
                    )
                }
            }

            // Animated expansion of the slider
            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(animationSpec = tween(durationMillis = 300)),
                exit = shrinkVertically(animationSpec = tween(durationMillis = 300))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = tokens.sDp(12.dp), bottom = tokens.sDp(8.dp)),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    BoxWithConstraints(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(tokens.sDp(20.dp))
                            .padding(horizontal = tokens.sDp(8.dp))
                    ) {
                        val canvasWidth = constraints.maxWidth.toFloat()
                        val strokeWidth = with(LocalDensity.current) { tokens.sDp(12.dp).toPx() }
                        val gapWidth = with(LocalDensity.current) { tokens.sDp(24.dp).toPx() }
                        val tipRadius = with(LocalDensity.current) { tokens.sDp(8.dp).toPx() }
                        val totalGapWidth = (chunkSizes.size - 1) * gapWidth
                        val unitWidth = (canvasWidth - totalGapWidth) / totalUnits
                        var lastFilledChunkEndX = 0f
                        val currentProgress = progress * totalUnits

                        Canvas(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(tokens.sDp(20.dp))
                                .pointerInput(Unit) {
                                    detectDragGestures { change, _ ->
                                        val touchX = change.position.x
                                        val newProgress = (touchX / canvasWidth).coerceIn(0f, 1f)
                                        val newValue = (minValue + newProgress * (maxValue - minValue)).coerceIn(minValue, maxValue)
                                        currentValue = newValue
                                        onValueChange(newValue)
                                    }
                                }
                        ) {
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
                            var remainingProgress = currentProgress
                            chunkSizes.forEach { chunkSize ->
                                val chunkWidth = unitWidth * chunkSize
                                val startX = currentX
                                if (remainingProgress > 0f) {
                                    val fillAmount = min(remainingProgress, chunkSize)
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
                                    remainingProgress -= fillAmount
                                }
                                currentX += chunkWidth + gapWidth
                            }
                            // Draw tip indicator
                            if (progress > 0f) {
                                drawCircle(
                                    color = Color.Black,
                                    radius = tipRadius,
                                    center = Offset(lastFilledChunkEndX, size.height / 2)
                                )
                                drawCircle(
                                    color = Color.White,
                                    radius = tipRadius,
                                    center = Offset(lastFilledChunkEndX, size.height / 2),
                                    style = Stroke(width = strokeWidth * 0.2f)
                                )
                            }
                        }
                    }

                    // Button row below slider
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = tokens.sDp(12.dp)),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Reset button on the left
                        Box(
                            modifier = Modifier
                                .background(
                                    color = Color.Black,
                                    shape = RoundedCornerShape(tokens.sDp(20.dp))
                                )
                                .clickable(
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() }
                                ) {
                                    currentValue = fallbackValue
                                    onValueChange(fallbackValue)
                                }
                                .padding(
                                    horizontal = tokens.sDp(14.dp),
                                    vertical = tokens.sDp(4.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Reset",
                                color = Color.White,
                                //fontSize = tokens.sSp(12.sp),
                                fontSize = tokens.calendarTextSize.times(1.2),
                                fontWeight = FontWeight.Medium
                            )
                        }

                        // Plus/Minus buttons on the right
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(tokens.sDp(8.dp))
                        ) {
                            // Minus button
                            Box(
                                modifier = Modifier
                                    .size(tokens.sDp(30.dp))
                                    .background(
                                        color = Color.Black,
                                        shape = RoundedCornerShape(tokens.sDp(20.dp))
                                    )
                                    .clickable(
                                        indication = null,
                                        interactionSource = remember { MutableInteractionSource() }
                                    ) {
                                        val newValue = (currentValue - step).coerceAtLeast(minValue)
                                        currentValue = newValue
                                        onValueChange(newValue)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
//                                Text(
//                                    text = "-",
//                                    color = Color.White,
//                                    fontSize = 18.sp,
//                                    fontWeight = FontWeight.Bold
//                                )
                                Icon(
                                    imageVector = Lucide.Minus,
                                    contentDescription = "Decrease",
                                    tint = Color.White,
                                    modifier = Modifier.size(tokens.sDp(18.dp))
                                )
                            }

                            // Plus button
                            Box(
                                modifier = Modifier
                                    .size(tokens.sDp(30.dp))
                                    .background(
                                        color = Color.Black,
                                        shape = RoundedCornerShape(tokens.sDp(20.dp))
                                    )
                                    .clickable(
                                        indication = null,
                                        interactionSource = remember { MutableInteractionSource() }
                                    ) {
                                        val newValue = (currentValue + step).coerceAtMost(maxValue)
                                        currentValue = newValue
                                        onValueChange(newValue)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
//                                Text(
//                                    text = "+",
//                                    color = Color.White,
//                                    fontSize = 18.sp,
//                                    fontWeight = FontWeight.Bold
//                                )
                                Icon(
                                    imageVector = Lucide.Plus,
                                    contentDescription = "Increase",
                                    tint = Color.White,
                                    modifier = Modifier.size(tokens.sDp(18.dp))
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}