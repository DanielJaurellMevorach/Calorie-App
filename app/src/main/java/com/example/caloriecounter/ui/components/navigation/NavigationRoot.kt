package com.example.caloriecounter.ui.components.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition.Companion.None
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.caloriecounter.ui.pages.Activity
import com.example.caloriecounter.ui.pages.Home
import com.example.caloriecounter.ui.pages.NutritionResults
import com.example.caloriecounter.ui.pages.Recipes
import com.example.caloriecounter.ui.pages.Scanner
import com.example.caloriecounter.ui.pages.Weight
import com.example.caloriecounter.ui.components.openai.NutritionResult
import kotlinx.serialization.Serializable

@Serializable
data object HomeScreen : NavKey

@Serializable
data object WeightScreen : NavKey

@Serializable
data object ActivityScreen : NavKey

@Serializable
data object RecipesScreen : NavKey

@Serializable
data object ScannerScreen : NavKey

@Serializable
data class NutritionResultsScreen(val nutritionData: String? = null) : NavKey

@Composable
fun NavigationRoot(
    modifier : Modifier = Modifier,
    windowSize: WindowSizeClass
) {
    val backStack = rememberNavBackStack(
        HomeScreen
    )

    NavDisplay(
        modifier = modifier,
        backStack = backStack,
        entryDecorators = listOf(
            rememberSavedStateNavEntryDecorator(),
        ),
        entryProvider = { key ->
            when(key) {
                is HomeScreen -> {
                    NavEntry(
                        key = key,
                    ) {
                        Home(
                            windowSize = windowSize,
                            onNavigateToHome = {
                            },
                            onNavigateToWeight = {
                                backStack.add(WeightScreen)
                            },
                            onNavigateToActivity = {
                                backStack.add(ActivityScreen)
                            },
                            onNavigateToRecipes = {
                                backStack.add(RecipesScreen)
                            },
                            onNavigateToScanner = {
                                backStack.add(ScannerScreen)
                            }
                        )
                    }
                }

                is WeightScreen -> {
                    NavEntry(
                        key = key,
                    ) {
                        Weight(
                            windowSize = windowSize,
                            onNavigateToHome = {
                                backStack.add(HomeScreen)
                            },
                            onNavigateToWeight = {
                                backStack.add(WeightScreen)
                            },
                            onNavigateToActivity = {
                                backStack.add(ActivityScreen)
                            },
                            onNavigateToRecipes = {
                                backStack.add(RecipesScreen)
                            },
                            onNavigateToScanner = {
                                backStack.add(ScannerScreen)
                            }
                        )
                    }
                }

                is ActivityScreen -> {
                    NavEntry(
                        key = key,
                    ) {
                        Activity(
                            windowSize = windowSize,
                            onNavigateToHome = {
                                backStack.add(HomeScreen)
                            },
                            onNavigateToWeight = {
                                backStack.add(WeightScreen)
                            },
                            onNavigateToActivity = {
                                backStack.add(ActivityScreen)
                            },
                            onNavigateToRecipes = {
                                backStack.add(RecipesScreen)
                            },
                            onNavigateToScanner = {
                                backStack.add(ScannerScreen)
                            }
                        )
                    }
                }

                is RecipesScreen -> {
                    NavEntry(
                        key = key,
                    ) {
                        Recipes(
                            windowSize = windowSize,
                            onNavigateToHome = {
                                backStack.add(HomeScreen)
                            },
                            onNavigateToWeight = {
                                backStack.add(WeightScreen)
                            },
                            onNavigateToActivity = {
                                backStack.add(ActivityScreen)
                            },
                            onNavigateToRecipes = {
                                backStack.add(RecipesScreen)
                            },
                            onNavigateToScanner = {
                                backStack.add(ScannerScreen)
                            }
                        )
                    }
                }

                is ScannerScreen -> {
                    NavEntry(
                        key = key,
                    ) {
                        Scanner(
                            onNavigateToNutritionResults = {
                                // Always navigate to loading state first, then update via ViewModel
                                backStack.add(NutritionResultsScreen())
                            }
                        )
                    }
                }

                is NutritionResultsScreen -> {
                    NavEntry(
                        key = key,
                    ) {
                        NutritionResults(
                            nutritionData = key.nutritionData,
                            windowSize = windowSize,
                            onNavigateToHome = {
                                backStack.add(HomeScreen)
                            },
                            onNavigateToWeight = {
                                backStack.add(WeightScreen)
                            },
                            onNavigateToActivity = {
                                backStack.add(ActivityScreen)
                            },
                            onNavigateToRecipes = {
                                backStack.add(RecipesScreen)
                            },
                            onNavigateToScanner = {
                                backStack.add(ScannerScreen)
                            }
                        )
                    }
                }


                else -> throw IllegalArgumentException("Unknown key: $key")
            }
        }
    )
}