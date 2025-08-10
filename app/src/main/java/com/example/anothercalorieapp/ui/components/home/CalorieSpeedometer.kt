import android.annotation.SuppressLint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.min


@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun CalorieSpeedometer(
    currentCalories: Int,
    maxCalories: Int,
    modifier: Modifier = Modifier
) {
    val progress = min(currentCalories.toFloat() / maxCalories.toFloat(), 1f)
    val density = LocalDensity.current

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(140.dp) // Height for arcs + spacing
    ) {
        val canvasWidth = constraints.maxWidth.toFloat()

        val strokeWidth = with(density) { 40.dp.toPx() }
        val totalSweep = 180f
        val gapAngle = 20f
        val segments = 3
        val segmentSweep = (totalSweep - gapAngle * (segments - 1)) / segments
        val startAngle = 180f

        val diameter = canvasWidth * 0.85f

        val arcLeft = (canvasWidth - diameter) / 2f
        val arcTop = 10f
        val arcRect = Rect(arcLeft, arcTop, arcLeft + diameter, arcTop + diameter)

        // These variables need to be declared outside the loop to be accessible later
        var lastFilledSegmentStartAngle = 0f
        var lastFilledSegmentSweep = 0f

        Canvas(modifier = Modifier.fillMaxWidth().height(90.dp)) {
            var angle = startAngle
            // Draw background arcs
            repeat(segments) {
                drawArc(
                    color = Color.White,
                    startAngle = angle,
                    sweepAngle = segmentSweep,
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                    topLeft = arcRect.topLeft,
                    size = arcRect.size
                )
                angle += segmentSweep + gapAngle
            }

            val totalProgressSweep = totalSweep * progress
            var remainingSweep = totalProgressSweep
            angle = startAngle

            // Draw progress arcs and track last filled segment and sweep
            repeat(segments) {
                val sweep = min(segmentSweep, remainingSweep)
                if (sweep > 0f) {
                    drawArc(
                        color = Color.Black,
                        startAngle = angle,
                        sweepAngle = sweep,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                        topLeft = arcRect.topLeft,
                        size = arcRect.size
                    )
                    lastFilledSegmentStartAngle = angle
                    lastFilledSegmentSweep = sweep
                }
                remainingSweep -= segmentSweep
                angle += segmentSweep + gapAngle
            }

            // Draw bigger circle at the END of the black progress
            if (progress > 0f) {
                val radius = diameter / 2f
                val centerX = arcLeft + radius
                val centerY = arcTop + radius

                // Use the correct end angle of the last filled segment
                val endAngleOfFill = lastFilledSegmentStartAngle + lastFilledSegmentSweep

                // Convert to radians for position calculation
                val angleRad = Math.toRadians(endAngleOfFill.toDouble())

                // Calculate position on arc circumference
                val circleRadius = with(density) { 48.dp.toPx() } / 2f
                val circleX = centerX + radius * kotlin.math.cos(angleRad).toFloat()
                val circleY = centerY + radius * kotlin.math.sin(angleRad).toFloat()

                // Draw black filled circle
                drawCircle(
                    color = Color.Black,
                    radius = circleRadius,
                    center = Offset(circleX, circleY)
                )
                // Draw white border circle slightly bigger
                drawCircle(
                    color = Color.White,
                    radius = circleRadius,
                    center = Offset(circleX, circleY),
                    style = Stroke(width = strokeWidth * 0.15f)
                )
            }
        }

        // Center calorie text horizontally (no horizontal offset)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = 100.dp) // Position below arcs
        ) {
            Text(
                text = "${maxCalories - currentCalories}",
                fontSize = 28.sp,
                color = Color.Black,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = "Sunday's remaining calories",
                modifier = Modifier.padding(top = 16.dp),
                fontSize = 14.sp,
                color = Color.Gray,
                fontWeight = FontWeight.W400,
            )
        }
    }
}