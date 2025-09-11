package com.example.responsiveness

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.anothercalorieapp.ui.components.scanner.viewmodel.MealDetailPageLoading
import com.example.responsiveness.ui.components.general.NavigationBar
import com.example.responsiveness.ui.components.general.rememberSafeContentPadding
import com.example.responsiveness.ui.screens.analytics.Analytics
import com.example.responsiveness.ui.screens.analytics.viewmodel.AnalyticsViewModel
import com.example.responsiveness.ui.screens.analytics.viewmodel.AnalyticsViewModelFactory
import com.example.responsiveness.ui.screens.home.HomeScreen
import com.example.responsiveness.ui.screens.home.viewmodel.HomeViewModel
import com.example.responsiveness.ui.screens.home.viewmodel.HomeViewModelFactory
import com.example.responsiveness.ui.screens.mealdetails.DatabaseMealDetailsScreen
import com.example.responsiveness.ui.screens.profile.ProfileScreen
import com.example.responsiveness.ui.screens.scanner.ScannerScreen
import com.example.responsiveness.ui.screens.scanner.viewmodel.ScannerViewModel
import com.example.responsiveness.ui.theme.ResponsivenessTheme
import kotlinx.serialization.Serializable

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ResponsivenessTheme {

                val navController = rememberNavController()
                val context = LocalContext.current
                val previewView = remember { PreviewView(context) }

                val mealDao = remember { com.example.anothercalorieapp.database.MealDatabase.getDatabase(context).mealDao() }

                val homeViewModelFactory = remember { HomeViewModelFactory(mealDao) }
                val homeViewModel: HomeViewModel = viewModel(factory = homeViewModelFactory)

                val scannerViewModel: ScannerViewModel = viewModel()

                val analyticsViewModelFactory = remember { AnalyticsViewModelFactory(mealDao) }
                val analyticsViewModel: AnalyticsViewModel = viewModel(factory = analyticsViewModelFactory)
                // Use SafeAreaHandler for robust inset handling
                // Use SafeAreaHandler for robust inset handling - fixes overlay issue
                val safeContentPadding = rememberSafeContentPadding(
                    includeStatusBar = false,
                    includeNavigationBar = true
                )

                Box(modifier = Modifier.fillMaxSize()) {
                    NavHost(
                        navController = navController,
                        startDestination = HomeRoute,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // Home Screen
                        composable<HomeRoute> {
                            HomeScreen(
                                navController = navController,
                                viewModel = homeViewModel,
                            )
                        }

                        // Scanner Screen (no arguments)
                        composable("scanner") {
                            ScannerScreen(previewView, navController, null, scannerViewModel)
                        }
                        // Scanner Screen (with selectedTimeOfDay argument)
                        composable(
                            route = "scanner?selectedTimeOfDay={selectedTimeOfDay}",
                            arguments = listOf(
                                androidx.navigation.navArgument("selectedTimeOfDay") {
                                    type = androidx.navigation.NavType.StringType
                                    nullable = true
                                    defaultValue = null
                                }
                            )
                        ) { backStackEntry ->
                            val selectedTimeOfDay = backStackEntry.arguments?.getString("selectedTimeOfDay")
                            ScannerScreen(previewView, navController, selectedTimeOfDay, scannerViewModel)
                        }

                        // Analytics Screen
                        composable<LogsRoute> {
                            Analytics(
                                viewModel = analyticsViewModel,
                                navController = navController
                            )
                        }

                        // Database Meal Details Screen
                        composable<DatabaseMealDetailRoute> { backStackEntry ->
                            val route = backStackEntry.toRoute<DatabaseMealDetailRoute>()
                            DatabaseMealDetailsScreen(route.mealId, navController, source = route.source)
                        }

                        // Meal Detail Page Loading Screen
                        composable<MealDetailPageLoadingRoute> { backStackEntry ->
                            val route = backStackEntry.toRoute<MealDetailPageLoadingRoute>()
                            val imageUri = Uri.parse(route.imageUri)
                            MealDetailPageLoading(
                                mealImageUri = imageUri,
                                onBackClick = { navController.navigateUp() },
                                navController = navController, // Pass NavController for navigation
                                selectedTimeOfDay = route.selectedTimeOfDay // FIX: pass selectedTimeOfDay
                            )
                        }

                        // Profile Screen
                        composable<ProfileRoute> {
                            ProfileScreen()
                        }
                    }

                    // Show NavigationBar with proper safe area handling
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route
                    val shouldShowNavBar = currentRoute != ScannerRoute::class.qualifiedName &&
                        currentRoute?.contains("MealDetailScreenRoute") != true &&
                        currentRoute?.contains("MealDetailPageLoadingRoute") != true &&
                        currentRoute?.contains("DatabaseMealDetailRoute") != true &&
                        currentRoute?.startsWith("scanner") != true

                    if (shouldShowNavBar) {
                        NavigationBar(
                            navController = navController,
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = safeContentPadding.calculateBottomPadding())
                        )
                    }
                }
            }
        }
    }
}

@Serializable
object HomeRoute

@Serializable
object ScannerRoute

@Serializable
object ProfileRoute

@Serializable
object LogsRoute

@Serializable
data class DatabaseMealDetailRoute (
    val mealId: Long,
    val source: String? = null
)

@Serializable
data class MealDetailPageLoadingRoute(
    val imageUri: String,
    val selectedTimeOfDay: String? = null
)
