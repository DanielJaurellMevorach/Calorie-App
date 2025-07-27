package com.example.caloriecounter.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun Statistics(
    currentPage: String = "Food",
    onNavigateToHome: () -> Unit = {},
    onNavigateToWeight: () -> Unit = {},
    onNavigateToActivity: () -> Unit = {},
    onNavigateToRecipes: () -> Unit = {},
    onNavigateToScanner: () -> Unit = {}
) {
    var activeTab by remember { mutableStateOf(currentPage) }

    val statsTextColor = Color(0xFF4463DE)
    val unSelectedTextColor = Color(0xFFA2A2A2)

    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Food tab
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    activeTab = "Food"
                    onNavigateToHome()
                }
            ) {
                Text(
                    text = "Food",
                    color = if (currentPage == "Food") statsTextColor else unSelectedTextColor,
                    fontWeight = if (currentPage == "Food") FontWeight.W500 else FontWeight.Normal
                )
                if (currentPage == "Food") {
                    Box(
                        modifier = Modifier
                            .background(statsTextColor)
                            .height(2.dp)
                            .width(32.dp)
                    )
                }
            }

            Column (
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    activeTab = "Weight"
                    onNavigateToWeight()
                }
            ) {
                Text(
                    text = "Weight",
                    color = if (currentPage == "Weight") statsTextColor else unSelectedTextColor,
                    fontWeight = if (currentPage == "Weight") FontWeight.W500 else FontWeight.Normal
                )
                if (currentPage == "Weight") {
                    Box(
                        modifier = Modifier
                            .background(statsTextColor)
                            .height(2.dp)
                            .width(42.dp)
                    )
                }
            }

            // Activity tab
            Column (
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    activeTab = "Activity"
                    onNavigateToActivity()
                }
            ) {
                Text(
                    text = "Activity",
                    color = if (currentPage == "Activity") statsTextColor else unSelectedTextColor,
                    fontWeight = if (currentPage == "Activity") FontWeight.W500 else FontWeight.Normal
                )
                if (currentPage == "Activity") {
                    Box(
                        modifier = Modifier
                            .background(statsTextColor)
                            .height(2.dp)
                            .width(48.dp)
                    )
                }
            }

            // Recipes tab
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    activeTab = "Recipes"
                    onNavigateToRecipes()
                }
            ) {
                Text(
                    text = "Recipes",
                    color = if (currentPage == "Recipes") statsTextColor else unSelectedTextColor,
                    fontWeight = if (currentPage == "Recipes") FontWeight.W500 else FontWeight.Normal
                )
                if (currentPage == "Recipes") {
                    Box(
                        modifier = Modifier
                            .background(statsTextColor)
                            .height(2.dp)
                            .width(48.dp)
                    )
                }
            }
        }

        Spacer(
            modifier = Modifier.height(40.dp)
        )

        // Show content based on current page
        when (currentPage) {
            "Food" -> {
                FoodStatistics()
                Spacer(modifier = Modifier.height(80.dp))
                DayCalorieStatistics()
            }
            // Other pages handle their own content
        }
    }
}