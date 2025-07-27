package com.example.caloriecounter.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun FoodStatistics() {

    val statsBackgroundColor = Color(0xFF4463DE)
    val borderColor = Color(0xFFA2A2A2).copy(0.15f)

    Row (
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column (
            modifier = Modifier
                .background(
                    color = statsBackgroundColor,
                    shape = RoundedCornerShape(40.dp)
                )
                .weight(1f)
                .padding(start = 24.dp, top = 28.dp, bottom = 20.dp)
        ) {
            Text (
                text = "Taco",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.W500
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            CaloriesMeter(
                value = 672,
                fillColor = Color(0xFFC2FFC1)
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            CarbsMeter(
                value = 67,
                fillColor = Color(0xFFF4F622).copy(alpha = 0.8f)
            )
        }

        Spacer(
            modifier = Modifier.width(16.dp)
        )

        Column (
            Modifier
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(40.dp)
                )
                .border(
                    width = 1.5.dp,
                    color = borderColor,
                    shape = RoundedCornerShape(40.dp)
                )
                .weight(1f)
                .padding(start = 24.dp, top = 28.dp, bottom = 20.dp)
        ) {
            Text (
                text = "Donut",
                color = Color.Black,
                fontSize = 20.sp,
                fontWeight = FontWeight.W500
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            CaloriesMeter(
                value = 263,
                valueColor = Color.Black,
                textColor = Color(0xFFA2A2A2),
                fillColor = Color(0xFF72C2FC)
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            CarbsMeter(
                value = 31,
                valueColor = Color.Black,
                textColor = Color(0xFFA2A2A2),
                fillColor = Color(0xFFC1CFFF)
            )
        }
    }
}
@Composable
fun Meter(
    fillFraction: Float,
    backgroundColor: Color,
    fillColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .width(10.dp)
            .height(56.dp)
            .background(backgroundColor, RoundedCornerShape(5.dp))
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .width(10.dp)
                .height(56.dp * fillFraction)
                .background(fillColor, RoundedCornerShape(5.dp))
        )
    }
}

@Composable
fun CaloriesMeter(
    value: Int,
    fillColor: Color,
    valueColor: Color = Color.White,
    textColor : Color = Color.White
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.height(100.dp) // Ensures alignment with Meter
    ) {
        Meter(
            fillFraction = value / 1000f, // Assuming a max of 1000 for calories for fill fraction calculation
            backgroundColor = fillColor.copy(alpha = 0.3f), // 0.3 alpha of fillColor
            fillColor = fillColor,
            modifier = Modifier.padding(end = 12.dp)
        )
        Column {
            Text(
                text = value.toString(),
                color = valueColor
            )
            Text(
                text = "Calories",
                color = textColor,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun CarbsMeter(
    value: Int,
    fillColor: Color,
    valueColor: Color = Color.White,
    textColor : Color = Color.White,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.height(100.dp) // Ensures alignment with Meter
    ) {
        Meter(
            fillFraction = value / 100f, // Assuming a max of 100 for carbs for fill fraction calculation
            backgroundColor = fillColor.copy(alpha = 0.3f), // 0.3 alpha of fillColor
            fillColor = fillColor,
            modifier = Modifier.padding(end = 12.dp)
        )
        Column {
            Text(
                text = "${value}g",
                color = valueColor
            )
            Text(
                text = "Carbs",
                color = textColor,
                fontSize = 14.sp
            )
        }
    }
}