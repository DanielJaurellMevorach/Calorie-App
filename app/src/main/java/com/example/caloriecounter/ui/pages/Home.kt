package com.example.caloriecounter.ui.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.caloriecounter.ui.components.*
import com.example.caloriecounter.ui.components.navigation.PageLayout

@Composable
fun Home(
    modifier: Modifier = Modifier,
    windowSize: WindowSizeClass,
    onNavigateToWeight: () -> Unit = {},
    onNavigateToActivity: () -> Unit = {},
    onNavigateToRecipes: () -> Unit = {},
    onNavigateToScanner: () -> Unit = {},
    onNavigateToHome: () -> Unit = {}
) {
    PageLayout(
        currentPage = "Food",
        windowSize = windowSize,
        onNavigateToHome = onNavigateToHome,
        onNavigateToWeight = onNavigateToWeight,
        onNavigateToActivity = onNavigateToActivity,
        onNavigateToRecipes = onNavigateToRecipes,
        onNavigateToScanner = onNavigateToScanner,
        modifier = modifier
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(40.dp),
        ) {
            item {
                FoodStatistics()
            }

            item {
                DayCalorieStatistics()
            }
        }
    }
}