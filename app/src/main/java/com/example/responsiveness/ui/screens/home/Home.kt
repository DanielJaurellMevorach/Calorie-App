package com.example.responsiveness.ui.screens.home

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollFactory
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.composables.icons.lucide.Beef
import com.composables.icons.lucide.CookingPot
import com.composables.icons.lucide.Droplets
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Milk
import com.composables.icons.lucide.Soup
import com.composables.icons.lucide.Wheat
import com.example.anothercalorieapp.ui.components.home.NutrientMeter
import com.example.responsiveness.ui.components.general.rememberSafeContentPadding
import com.example.responsiveness.ui.screens.home.components.CalorieSpeedometer
import com.example.responsiveness.ui.screens.home.components.TimeOfDaySummary
import com.example.responsiveness.ui.screens.home.components.CalendarCalories
import com.example.responsiveness.ui.screens.home.viewmodel.HomeViewModel
import com.example.responsiveness.ui.theme.DesignTokens

@OptIn(ExperimentalFoundationApi::class)
@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel,
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAFAFA))
    ) {
        val tokens = DesignTokens.provideTokens(availableWidth = maxWidth, availableHeight = maxHeight)

        // Single state collection - all logic moved to ViewModel
        val homeState by viewModel.homeState.collectAsState()
        val uiState = homeState.uiState

        // Get proper content padding that accounts for the status bar and floating navigation bar
        val safePadding = rememberSafeContentPadding(
            includeStatusBar = true,
            includeNavigationBar = true,
            additionalBottomPadding = tokens.navContainerHeight + tokens.navHorizontalPadding * 2 + tokens.sDp(16.dp)
        )

        CompositionLocalProvider(
            LocalOverscrollFactory provides null
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(safePadding)
                    .padding(horizontal = tokens.outerInset),
                verticalArrangement = Arrangement.spacedBy(tokens.sDp(8.dp), Alignment.Top),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Calendar component - data prepared in ViewModel
                CalendarCalories(
                    calendarData = homeState.calendarData,
                    tokens = tokens,
                    analytics = false,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = tokens.sDp(8.dp))
                )

                Spacer(modifier = Modifier.height(tokens.sDp(4.dp)))

                // Calorie speedometer - values from ViewModel
                CalorieSpeedometer(
                    currentCalories = homeState.todayCalories,
                    maxCalories = homeState.maxCalories,
                    tokens = tokens,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = tokens.sDp(8.dp))
                )

                Spacer(modifier = Modifier.height(tokens.sDp(4.dp)))

                // Nutrient meters - all values from ViewModel
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = tokens.sDp(8.dp)),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    NutrientMeter(
                        nutrient = "Protein",
                        currentValue = homeState.todayProtein,
                        maxValue = homeState.maxProtein,
                        tokens = tokens,
                        color = Color.Black,
                        icon = Lucide.Beef,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(tokens.sDp(8.dp)))
                    NutrientMeter(
                        nutrient = "Carbs",
                        currentValue = homeState.todayCarbs,
                        maxValue = homeState.maxCarbs,
                        tokens = tokens,
                        color = Color.Black,
                        icon = Lucide.Wheat,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(tokens.sDp(8.dp)))
                    NutrientMeter(
                        nutrient = "Fats",
                        currentValue = homeState.todayFat,
                        maxValue = homeState.maxFat,
                        tokens = tokens,
                        color = Color.Black,
                        icon = Lucide.Droplets,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(tokens.sDp(8.dp)))

                // Time of day summaries - data processed in ViewModel
                TimeOfDaySummary(
                    mealName = "Morning",
                    calories = uiState.morning.calories.toString(),
                    icon = Lucide.Milk,
                    tokens = tokens,
                    mealImageUris = uiState.morning.mealImageUris,
                    mealDetails = uiState.morning.mealDetails,
                    navController = navController,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = tokens.sDp(8.dp)),
                    onAddMealClick = {
                        navController.navigate("scanner?selectedTimeOfDay=Morning")
                    }
                )

                Spacer(modifier = Modifier.height(tokens.sDp(8.dp)))

                TimeOfDaySummary(
                    mealName = "Afternoon",
                    calories = uiState.afternoon.calories.toString(),
                    icon = Lucide.CookingPot,
                    tokens = tokens,
                    mealImageUris = uiState.afternoon.mealImageUris,
                    mealDetails = uiState.afternoon.mealDetails,
                    navController = navController,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = tokens.sDp(8.dp)),
                    onAddMealClick = {
                        navController.navigate("scanner?selectedTimeOfDay=Afternoon")
                    }
                )

                Spacer(modifier = Modifier.height(tokens.sDp(8.dp)))

                TimeOfDaySummary(
                    mealName = "Evening",
                    calories = uiState.evening.calories.toString(),
                    icon = Lucide.Soup,
                    tokens = tokens,
                    mealImageUris = uiState.evening.mealImageUris,
                    mealDetails = uiState.evening.mealDetails,
                    navController = navController,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = tokens.sDp(8.dp)),
                    onAddMealClick = {
                        navController.navigate("scanner?selectedTimeOfDay=Evening")
                    }
                )
            }
        }
    }
}