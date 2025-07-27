package com.example.caloriecounter.ui.pages

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.caloriecounter.ui.components.openai.NutritionAnalysisDisplay
import com.example.caloriecounter.ui.components.openai.NutritionResult
import com.example.caloriecounter.ui.components.openai.NutritionResultsViewModel
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun NutritionResults(
    nutritionData: String?,
    modifier: Modifier = Modifier,
    windowSize: WindowSizeClass,
    onNavigateToWeight: () -> Unit = {},
    onNavigateToActivity: () -> Unit = {},
    onNavigateToRecipes: () -> Unit = {},
    onNavigateToScanner: () -> Unit = {},
    onNavigateToHome: () -> Unit = {}
) {
    // Use proper ViewModel instance
    val nutritionViewModel = viewModel<NutritionResultsViewModel>()
    val nutritionDataFromViewModel by nutritionViewModel.nutritionData.collectAsState()

    val nutritionResult = remember(nutritionDataFromViewModel) {
        if (nutritionDataFromViewModel.isNullOrEmpty()) {
            null
        } else {
            try {
                Gson().fromJson(nutritionDataFromViewModel, NutritionResult::class.java)
            } catch (e: JsonSyntaxException) {
                // Fallback to sample data if JSON parsing fails
                createSampleNutritionResult()
            }
        }
    }

    NutritionAnalysisDisplay(
        nutritionResult = nutritionResult,
        isLoading = false, // Always false since we only navigate here when data is ready
        onNavigateToHome = {
            nutritionViewModel.reset()
            onNavigateToHome()
        },
        modifier = modifier
    )
}

private fun createSampleNutritionResult(): NutritionResult {
    return NutritionResult(
        mealName = "Analysis Failed",
        ingredients = emptyList(),
        nutrition = com.example.caloriecounter.ui.components.openai.Nutrition(
            energyKcal = 0.0,
            proteinG = 0.0,
            carbohydratesG = 0.0,
            fatG = 0.0,
            fiberG = 0.0,
            sugarsG = 0.0,
            sodiumMg = 0.0,
            cholesterolMg = 0.0,
            waterL = 0.0
        ),
        mealNutritionScore = "F",
        error = "Failed to parse nutrition data"
    )
}
