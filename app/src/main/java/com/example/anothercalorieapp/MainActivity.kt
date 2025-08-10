package com.example.anothercalorieapp

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.android.example.cameraxapp.CameraScreen
import com.example.anothercalorieapp.ui.components.home.MealDetailPage
import com.example.anothercalorieapp.ui.components.scanner.viewmodel.MealDetailPageLoading
import com.example.anothercalorieapp.ui.components.scanner.viewmodel.MealDetailsUiState
import com.example.anothercalorieapp.ui.components.scanner.viewmodel.MealDetailsViewModel
import com.example.anothercalorieapp.ui.theme.AnotherCalorieAppTheme

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

                // NavHost defines the navigation graph
                NavHost(navController = navController, startDestination = "camera_route") {
                    // Camera Screen Destination
                    composable("camera_route") {
                        CameraScreen(
                            previewView = previewView,
                            navController = navController
                        )
                    }

                    // Meal Details Screen Destination
                    composable(
                        route = "meal_details_route/{imageUri}",
                        arguments = listOf(navArgument("imageUri") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val imageUriString = backStackEntry.arguments?.getString("imageUri") ?: ""
                        MealDetailScreenDestination(imageUriString = imageUriString)
                    }
                }
            }
        }
    }
}

// This composable is the entry point for the Meal Details screen.
// It handles the ViewModel logic and UI state.
@Composable
fun MealDetailScreenDestination(
    imageUriString: String,
    mealDetailsViewModel: MealDetailsViewModel = viewModel()
) {
    val context = LocalContext.current
    // Parse the URI that was passed as a navigation argument
    // The URI is already URL-encoded, so we just need to decode it once
    val imageUri = Uri.parse(Uri.decode(imageUriString))

    // This effect triggers the image analysis when the screen is first displayed
    LaunchedEffect(key1 = imageUri) {
        mealDetailsViewModel.analyzeImage(imageUri, context)
    }

    // Observe the UI state from the ViewModel
    val uiState by mealDetailsViewModel.uiState.collectAsState()

    // Display the appropriate UI based on the current state
    when (val state = uiState) {
        is MealDetailsUiState.Loading -> {
            // Show the meal detail page with shimmer effects while loading
            MealDetailPageLoading(
                mealImageUri = imageUri,
                onBackClick = { /* TODO: Handle back navigation */ },
                onDeleteClick = { /* TODO: Handle delete action */ }
            )
        }
        is MealDetailsUiState.Success -> {
            // On success, show the MealDetailPage with the data from the API
            MealDetailPage(
                mealData = state.mealDetails,
                mealImageUri = state.imageUri,
                onBackClick = { /* TODO: Handle back navigation */ },
                onDeleteClick = { /* TODO: Handle delete action */ }
            )
        }
        is MealDetailsUiState.Error -> {
            ErrorState(message = state.message)
        }
    }
}

// A simple composable to show if an error occurs.
@Composable
fun ErrorState(message: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = "Error: $message",
            color = Color.Red,
            fontSize = 16.sp,
            modifier = Modifier.padding(16.dp)
        )
    }
}
