package com.example.anothercalorieapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.camera.view.PreviewView
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.android.example.cameraxapp.CameraScreen
import com.example.anothercalorieapp.ui.components.scanner.viewmodel.MealDetailScreenDestination
import com.example.anothercalorieapp.ui.screens.Home
import com.example.anothercalorieapp.ui.screens.Profile
import com.example.anothercalorieapp.ui.screens.LogsScreen
import com.example.anothercalorieapp.ui.theme.AnotherCalorieAppTheme
import kotlinx.serialization.Serializable

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AnotherCalorieAppTheme {
                // The NavController is the central component for navigation in Compose
                val navController = rememberNavController()
                val context = LocalContext.current
                val previewView = remember { PreviewView(context) }

                NavHost(
                    navController = navController,
                    startDestination = HomeRoute
                ) {
                    // Home Screen Destination
                    composable<HomeRoute> {
                        Home(navController = navController)
                    }

                    // Scanner Screen Destination
                    composable<ScannerRoute> {
                        CameraScreen(
                            previewView = previewView,
                            navController = navController
                        )
                    }

                    // Profile Screen Destination
                    composable<ProfileRoute> {
                        Profile(navController = navController)
                    }

                    // Logs Screen Destination (placeholder)
                    composable<LogsRoute> {
                        LogsScreen(navController = navController)
                    }

                    // Meal Details Screen Destination (object-based)
                    composable<MealDetailScreenRoute> { backStackEntry ->
                        val route = backStackEntry.toRoute<MealDetailScreenRoute>()
                        MealDetailScreenDestination(
                            imageUriString = route.imageUri,
                            navController = navController
                        )
                    }

                    // NavHost defines the navigation graph
//                NavHost(navController = navController, startDestination = "camera_route") {
//                    // Camera Screen Destination
//                    composable("camera_route") {
//                        CameraScreen(
//                            previewView = previewView,
//                            navController = navController
//                        )
//                    }
//
//                    // Meal Details Screen Destination
//                    composable(
//                        route = "meal_details_route/{imageUri}",
//                        arguments = listOf(navArgument("imageUri") { type = NavType.StringType })
//                    ) { backStackEntry ->
//                        val imageUriString = backStackEntry.arguments?.getString("imageUri") ?: ""
//                        MealDetailScreenDestination(imageUriString = imageUriString)
//                    }
//                }
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
data class MealDetailScreenRoute (
    val imageUri: String
)