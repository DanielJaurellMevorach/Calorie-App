package com.example.anothercalorieapp.ui.components.home

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.responsiveness.ui.theme.DesignTokens
import java.util.Locale

@Composable
fun NutrientMeter(
    nutrient: String,
    currentValue: Double,
    color: Color,
    maxValue: Double,
    icon: ImageVector,
    tokens: DesignTokens.Tokens,
    modifier: Modifier = Modifier
) {
    Log.d("NutrientMeter", "Nutrient: $nutrient, Current: $currentValue, Max: $maxValue")

    val baseSizeDp = tokens.sDp(60.dp)
    val strokeWidthDp = tokens.sDp(10.dp)
    val iconSizeDp = tokens.sDp(24.dp)

    val valueFontSize = tokens.nutrientTextSize
    val labelFontSize = (tokens.nutrientTextSize.value - tokens.sSp(2.sp).value).sp

    // Main white container with rounded corners and padding
    Column(
        modifier = modifier
            .background(Color.White, RoundedCornerShape(tokens.sDp(16.dp)))
            .padding(tokens.sDp(12.dp)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(tokens.sDp(16.dp))
    ) {
        Box(
            modifier = Modifier.size(baseSizeDp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                // The background circle
                drawCircle(
                    //0xFFFAFAFA
                    //color = Color(0xFFF0F0F0),
                    color = Color(0xFFFAFAFA),
                    radius = size.minDimension / 2,
                    style = Stroke(width = strokeWidthDp.toPx(), cap = StrokeCap.Round)
                )
            }

            val progress = (currentValue / maxValue).coerceIn(0.0, 1.0)
            Canvas(modifier = Modifier.fillMaxSize()) {
                // The progress arc
                drawArc(
                    color = color,
                    startAngle = -90f,
                    sweepAngle = 360f * progress.toFloat(),
                    useCenter = false,
                    style = Stroke(width = strokeWidthDp.toPx(), cap = StrokeCap.Round)
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
                    modifier = Modifier.size(iconSizeDp)
                )
            }
        }

        // Display the value with one decimal place
        Text(
            text = "${String.format(Locale.getDefault(), "%.1f", currentValue)}g",
            style = TextStyle(
                fontSize = valueFontSize,
                fontWeight = FontWeight.Bold,
                platformStyle = PlatformTextStyle(includeFontPadding = false)
            ),
            color = color,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Text(
            text = "$nutrient left",
            fontSize = labelFontSize,
            color = Color.Black,
            fontWeight = FontWeight.Normal,
            maxLines = 1,
            style = TextStyle(
                platformStyle = PlatformTextStyle(includeFontPadding = false),
                lineHeight = labelFontSize
            ),
            modifier = Modifier.offset(y = tokens.sDp(0.dp))
        )

//        Text(
//            text = "$nutrient left",
//            fontSize = labelFontSize,
//            color = Color.Gray,
//            fontWeight = FontWeight.W400,
//            maxLines = 1,
//            style = TextStyle(
//                platformStyle = PlatformTextStyle(includeFontPadding = false),
//                lineHeight = labelFontSize
//            ),
//            modifier = Modifier.offset(y = tokens.sDp(0.dp))
//        )
    }
}
