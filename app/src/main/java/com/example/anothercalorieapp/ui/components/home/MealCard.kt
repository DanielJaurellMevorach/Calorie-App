package com.example.anothercalorieapp.ui.components.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.anothercalorieapp.ui.utils.getResponsiveCornerRadius
import com.example.anothercalorieapp.ui.utils.getResponsiveFontSize
import com.example.anothercalorieapp.ui.utils.getResponsivePadding
import com.example.anothercalorieapp.ui.utils.getResponsiveSize

@Composable
fun MealCard(
    modifier: Modifier = Modifier,
    mealName: String,
    calories: Int,
    carbs: Int,
    proteins: Int,
    fat: Int,
    time: String,
    imageRes: Int
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(getResponsiveSize(72.dp))
            .background(Color.White, RoundedCornerShape(getResponsiveCornerRadius()))
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Image
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = "Meal Image",
                modifier = Modifier
                    .size(getResponsiveSize(52.dp))
                    .clip(RoundedCornerShape(getResponsiveCornerRadius(8.dp))),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(getResponsivePadding(12.dp)))

            // Text content
            Column(
                modifier = Modifier
                    .height(getResponsiveSize(72.dp))
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Meal name + time in one row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = mealName,
                        fontSize = getResponsiveFontSize(14.sp),
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black,
                        modifier = Modifier.offset(y = getResponsivePadding(4.dp))
                    )
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFF0F0F0), shape = RoundedCornerShape(getResponsiveCornerRadius(4.dp)))
                    ) {
                        Text(
                            text = time,
                            fontSize = getResponsiveFontSize(10.sp),
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Black,
                            modifier = Modifier.padding(horizontal = getResponsivePadding(6.dp))
                        )
                    }
                }

                Text(
                    text = "$calories Calories",
                    fontSize = getResponsiveFontSize(12.sp),
                    fontWeight = FontWeight.Bold,
                    color = Color(0xF5000000)
                )

                Row(
                    modifier = Modifier.offset(y = getResponsivePadding(-4.dp))
                ) {
                    Text(
                        text = "$carbs Carbs",
                        fontSize = getResponsiveFontSize(12.sp),
                        color = Color(0xFF888888)
                    )
                    Text(" • ", fontSize = getResponsiveFontSize(10.sp), color = Color(0xFF888888))
                    Text(
                        text = "$proteins Proteins",
                        fontSize = getResponsiveFontSize(12.sp),
                        color = Color(0xFF888888)
                    )
                    Text(" • ", fontSize = getResponsiveFontSize(10.sp), color = Color(0xFF888888))
                    Text(
                        text = "$fat Fat",
                        fontSize = getResponsiveFontSize(12.sp),
                        color = Color(0xFF888888)
                    )
                }
            }
        }
    }
}
