package com.example.anothercalorieapp.ui.components.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.anothercalorieapp.ui.utils.getResponsiveCornerRadius
import com.example.anothercalorieapp.ui.utils.getResponsiveFontSize
import com.example.anothercalorieapp.ui.utils.getResponsivePadding
import com.example.anothercalorieapp.ui.utils.getResponsiveSize

@Composable
fun CalendarCalories(
    modifier: Modifier = Modifier
) {
    Column {
        Spacer(modifier = Modifier.height(getResponsiveSize(52.dp)))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(getResponsiveSize(72.dp)),
            horizontalArrangement = Arrangement.spacedBy(getResponsiveSize(8.dp)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(6) { index ->
                val dayNames = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
                val dayNumbers = listOf("07", "08", "09", "10", "11", "12")
                val calories = listOf("2300", "1890", "2150", "1950", "2400", "1750")

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            Color.White,
                            RoundedCornerShape(getResponsiveCornerRadius(24.dp))
                        )
                        .padding(top = getResponsivePadding(2.dp)),
                    horizontalAlignment = (Alignment.CenterHorizontally),
                    verticalArrangement = Arrangement.spacedBy(getResponsivePadding(2.dp))
                ) {
                    // Day name - very soft gray
                    Text(
                        text = dayNames[index],
                        color = Color(0xFFBBBBBB),
                        fontSize = getResponsiveFontSize(10.sp),
                        fontWeight = FontWeight.SemiBold,
                    )

                    // Day number - bigger black font
                    Text(
                        text = dayNumbers[index],
                        color = Color.Black,
                        fontSize = getResponsiveFontSize(14.sp),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.offset(y = getResponsivePadding(-6.dp))
                    )

                    // Calories - small dark green
                    Text(
                        text = calories[index],
                        color = Color.Black,
                        fontSize = getResponsiveFontSize(10.sp),
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.offset(y = getResponsivePadding(-12.dp))
                    )
                }
            }
        }
    }
    Spacer(
        modifier = Modifier.height(getResponsiveSize(40.dp))
    )
}