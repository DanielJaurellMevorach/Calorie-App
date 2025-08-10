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
            .height(72.dp)
            .background(Color.White, RoundedCornerShape(12.dp))
//            .border(
//                width = 1.dp,
//                color = Color.Black,
//                shape = RoundedCornerShape(12.dp)
//            )
            //.padding(horizontal = 8.dp)
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
                    .size(52.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Text content
            Column(
                modifier = Modifier
                    .height(72.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Meal name + time in one row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically // aligns them together
                ) {
                    Text(
                        text = mealName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black,
                        modifier = Modifier.offset(y = 4.dp)
                    )
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFF0F0F0), shape = RoundedCornerShape(4.dp))
                    ) {
                        Text(
                            text = time,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Black,
                            modifier = Modifier.padding(horizontal = 6.dp)
                        )
                    }
                }


                Text(
                    text = "$calories Calories",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xF5000000)
                )

                Row(
                    modifier = Modifier.offset(y = (-4).dp)
                ) {
                    Text(
                        text = "$carbs Carbs",
                        fontSize = 12.sp,
                        color = Color(0xFF888888)
                    )
                    Text(" • ", fontSize = 10.sp, color = Color(0xFF888888))
                    Text(
                        text = "$proteins Proteins",
                        fontSize = 12.sp,
                        color = Color(0xFF888888)
                    )
                    Text(" • ", fontSize = 10.sp, color = Color(0xFF888888))
                    Text(
                        text = "$fat Fat",
                        fontSize = 12.sp,
                        color = Color(0xFF888888)
                    )
                }
            }
        }
    }
}


