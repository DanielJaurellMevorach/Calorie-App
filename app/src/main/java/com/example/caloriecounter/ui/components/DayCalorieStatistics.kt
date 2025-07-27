package com.example.caloriecounter.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun DayCalorieStatistics() {

    val backgroundColor = Color(0xFF918686).copy(alpha = 0.1f)
    val calorieColor = Color(0xFFF38D00)

    Column (
        modifier = Modifier
            .fillMaxWidth()
            // Increased height to give the graph more space
            .height(250.dp)
            .padding(horizontal = 12.dp, vertical = 16.dp)
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(24.dp)
            )
    ) {
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = "Statistics",
                color = Color.Black,
                fontWeight = FontWeight.W500,
                fontSize = 14.sp
            )
            Text(
                text = "Calories",
                color = calorieColor,
                fontWeight = FontWeight.W500,
                fontSize = 14.sp
            )
        }

        // Graph section
        LineGraph()
    }
}

@Composable
fun LineGraph() {
    val graphColor = Color(0xFFF38D00) // The orange line color
    val axisColor = Color.LightGray.copy(alpha = 0.5f) // The dashed line color
    val textColor = Color.Black.copy(alpha = 0.7f) // Text color for labels

    // Sample data for the graph (calorie values for each day)
    val dataPoints = remember { listOf(1000f, 1250f, 1100f, 1400f, 1200f, 1050f) }
    val days = remember { listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat") }

    // Determine the min and max calorie values for scaling the graph
    val minCalorie = remember { dataPoints.minOrNull() ?: 1000f }
    val maxCalorie = remember { dataPoints.maxOrNull() ?: 1400f }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            // Increased height for the graph area to make it less squished
            .height(320.dp)
            .padding(vertical = 8.dp, horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Define padding within the canvas for y-axis labels
            val yAxisAreaWidth = 40.dp.toPx()
            val xAxisAreaHeight = 30.dp.toPx()

            val graphWidth = size.width - yAxisAreaWidth
            val graphHeight = size.height - xAxisAreaHeight

            // Draw horizontal dashed lines and labels
            val yValuesToDraw = listOf(1000f, 1200f, 1400f)
            yValuesToDraw.forEach { value ->
                val y = graphHeight - ((value - minCalorie) / (maxCalorie - minCalorie) * graphHeight)
                // Draw dashed lines starting after the label area
                drawLine(
                    color = axisColor,
                    start = Offset(yAxisAreaWidth, y),
                    end = Offset(size.width, y),
                    strokeWidth = 1.dp.toPx(),
                    pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                )
                // Draw calorie value labels within the designated y-axis area
                drawContext.canvas.nativeCanvas.drawText(
                    value.toInt().toString(),
                    0f, // Positioned at the start of the canvas
                    y + 5.dp.toPx(),
                    android.graphics.Paint().apply {
                        color = textColor.toArgb()
                        textSize = 12.sp.toPx()
                        textAlign = android.graphics.Paint.Align.LEFT
                    }
                )
            }

            // Prepare the path for the line graph, offset by the y-axis area
            val path = Path()
            val xStep = graphWidth / (dataPoints.size - 1)

            dataPoints.forEachIndexed { i, dataPoint ->
                val x = yAxisAreaWidth + i * xStep
                val y = graphHeight - ((dataPoint - minCalorie) / (maxCalorie - minCalorie) * graphHeight)

                if (i == 0) {
                    path.moveTo(x, y)
                } else {
                    val previousX = yAxisAreaWidth + (i - 1) * xStep
                    val previousY = graphHeight - ((dataPoints[i - 1] - minCalorie) / (maxCalorie - minCalorie) * graphHeight)
                    val controlX1 = previousX + (x - previousX) / 2
                    val controlY1 = previousY
                    val controlX2 = previousX + (x - previousX) / 2
                    val controlY2 = y
                    path.cubicTo(controlX1, controlY1, controlX2, controlY2, x, y)
                }
            }

            // Draw the smooth curve
            drawPath(
                path = path,
                color = graphColor,
                style = Stroke(width = 3.dp.toPx())
            )
        }

        // Day labels at the bottom, aligned with the graph axis
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                // Padding to align the labels with the start of the graph line
                .padding(start = 40.dp),
            // SpaceBetween ensures the first and last items are at the edges
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            days.forEach { day ->
                Text(
                    text = day,
                    fontSize = 12.sp,
                    color = textColor
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DayCalorieStatisticsPreview() {
    DayCalorieStatistics()
}