package com.example.anothercalorieapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.Beef
import com.composables.icons.lucide.Droplets
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Wheat
import com.example.anothercalorieapp.R
import com.example.anothercalorieapp.ui.components.home.CircleOverlay
import com.example.anothercalorieapp.ui.components.home.MealCard
import com.example.anothercalorieapp.ui.components.home.NutrientMeter
import com.example.anothercalorieapp.ui.utils.getResponsiveFontSize
import com.example.anothercalorieapp.ui.utils.getResponsiveSize
import androidx.navigation.NavController
import com.example.anothercalorieapp.ui.components.general.NavigationBar

@Composable
fun Home(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = modifier.fillMaxSize()
        ) {
            item {
                // Gray overlay with integrated CalendarCalories and CalorieSpeedometer
                CircleOverlay()
            }

            item {
                Spacer(
                    modifier = Modifier.height(getResponsiveSize(160.dp))
                )
                // Preview nutrient meters below the arc - centered horizontally
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    NutrientMeter(
                        nutrient = "Protein",
                        currentValue = 50,
                        color = Color(0xFFF49A0E),
                        maxValue = 70,
                        icon = Lucide.Beef
                    )

                    NutrientMeter(
                        nutrient = "Carbs",
                        currentValue = 200,
                        color = Color(0xFF19B6DE),
                        maxValue = 300,
                        icon = Lucide.Wheat
                    )

                    NutrientMeter(
                        nutrient = "Fat",
                        currentValue = 30,
                        color = Color(0xFF89C40C),
                        maxValue = 70,
                        icon = Lucide.Droplets
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(getResponsiveSize(40.dp)))
            }

            item {
                Text(
                    text = "Tracked Today: 3",
                    fontSize = getResponsiveFontSize(16.sp),
                    fontWeight = FontWeight.Bold,
                )
            }

            item {
                Spacer(modifier = Modifier.height(getResponsiveSize(16.dp)))
            }

            item {
                MealCard(
                    mealName = "Oatmeal with Almond Milk",
                    calories = 250,
                    carbs = 38,
                    proteins = 6,
                    fat = 38,
                    time = "09:10",
                    imageRes = R.drawable.meal_two
                )

                Spacer(
                    modifier = Modifier.height(getResponsiveSize(16.dp))
                )

                MealCard(
                    mealName = "Bruschetta with Tomato and Basil",
                    calories = 431,
                    carbs = 14,
                    proteins = 10,
                    fat = 17,
                    time = "13:19",
                    imageRes = R.drawable.meal_one
                )

                Spacer(
                    modifier = Modifier.height(getResponsiveSize(16.dp))
                )

                MealCard(
                    mealName = "Vanilla Apple Pie",
                    calories = 480,
                    carbs = 60,
                    proteins = 5,
                    fat = 20,
                    time = "17:47",
                    imageRes = R.drawable.meal_three
                )
            }

            item {
                Spacer(modifier = Modifier.height(getResponsiveSize(120.dp)))
            }
        }

        // Navigation Bar at bottom
        Box(
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            NavigationBar(navController = navController)
        }
    }
}