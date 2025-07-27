package com.example.caloriecounter.ui.components.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun NavBar(
    currentPage: String,
    onNavigateToHome: () -> Unit,
    onNavigateToWeight: () -> Unit,
    onNavigateToActivity: () -> Unit,
    onNavigateToRecipes: () -> Unit,
    modifier: Modifier = Modifier
) {
    val statsTextColor = Color(0xFF4463DE)
    val unSelectedTextColor = Color(0xFFA2A2A2)

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Food tab (navigates to Home)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
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
                        .fillMaxWidth(0.055f)
                )
            }
        }

        // Weight tab
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
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
                        .fillMaxWidth(0.075f)
                )
            }
        }

        // Activity tab
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
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
                        .fillMaxWidth(0.085f)
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
                        .fillMaxWidth(0.175f)
                )
            }
        }
    }
}
