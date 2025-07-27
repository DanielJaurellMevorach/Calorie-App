package com.example.caloriecounter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.caloriecounter.ui.components.navigation.NavigationRoot
import com.example.caloriecounter.ui.components.openai.Ingredient
import com.example.caloriecounter.ui.components.openai.Nutrition
import com.example.caloriecounter.ui.components.openai.NutritionAnalysisDisplay
import com.example.caloriecounter.ui.components.openai.NutritionResult
import com.example.caloriecounter.ui.theme.CalorieCounterTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            CalorieCounterTheme {
                Scaffold(
                    containerColor = Color.White,
                    modifier = Modifier.fillMaxSize(),
                ) { paddingValues ->
                    val windowSize = calculateWindowSizeClass(this)

                    NavigationRoot(
                        modifier = Modifier
                            .padding(paddingValues)
                            .background(Color.White),
                        windowSize = windowSize
                    )

//                    NutritionAnalysisDisplay(
//                        nutritionResult = NutritionResult(
//                            mealName = "Sample Meal",
//                            ingredients = listOf(
//                                Ingredient("Ingredient 1", 100.0, "g", 50.0),
//                                Ingredient("Ingredient 2", 200.0, "g", 100.0)
//                            ),
//                            nutrition = Nutrition(
//                                energyKcal = 500.0,
//                                proteinG = 30.0,
//                                carbohydratesG = 60.0,
//                                fatG = 20.0,
//                                fiberG = 5.0,
//                                sugarsG = 10.0,
//                                sodiumMg = 300.0,
//                                cholesterolMg = 50.0,
//                                waterL = 0.5
//                            ),
//                            mealNutritionScore = "C",
//                            error = null
//                        ),
//                        modifier = Modifier
//                            .fillMaxSize()
//                            .padding(paddingValues)
//                            .background(Color.White),
//                    )
                }
            }
        }

    }
}