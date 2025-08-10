package com.example.anothercalorieapp.ui.components.scanner.viewmodel

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.anothercalorieapp.HomeRoute
import com.example.anothercalorieapp.ui.components.home.MealDetailPage
import com.example.anothercalorieapp.ui.components.scanner.coroutine.sendPhotoToOpenAI
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

// Data classes to represent the parsed JSON from OpenAI
data class MealApiResponse(
    val meal_name: String?,
    val ingredients: List<Ingredient>?,
    val nutrition: Nutrition?,
    val meal_nutrition_score: String?,
    val error: String?
)

data class Ingredient(
    val name: String,
    val quantity: Double,
    val unit: String,
    val calories: Int
)

data class Nutrition(
    val energy_kcal: Int,
    val protein_g: Double,
    val carbohydrates_g: Double,
    val fat_g: Double,
    val fiber_g: Double,
    val sugars_g: Double,
    val sodium_mg: Int,
    val cholesterol_mg: Int
)

// UI State sealed interface to represent the different states of the screen
sealed interface MealDetailsUiState {
    object Loading : MealDetailsUiState
    data class Success(val mealDetails: MealApiResponse, val imageUri: Uri) : MealDetailsUiState
    data class Error(val message: String) : MealDetailsUiState
}

class MealDetailsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<MealDetailsUiState>(MealDetailsUiState.Loading)
    val uiState = _uiState.asStateFlow()

    fun analyzeImage(imageUri: Uri, context: Context) {
        // Set state to loading immediately
        _uiState.value = MealDetailsUiState.Loading

        viewModelScope.launch {
            try {
                // Handle both content:// and file:// URIs
                val photoFile = when (imageUri.scheme) {
                    "content" -> {
                        // Create a temporary file for content URIs
                        val tempFile = File.createTempFile("meal_image", ".jpg", context.cacheDir)
                        context.contentResolver.openInputStream(imageUri)?.use { inputStream ->
                            FileOutputStream(tempFile).use { outputStream ->
                                inputStream.copyTo(outputStream)
                            }
                        }
                        tempFile
                    }
                    "file" -> {
                        File(imageUri.path!!)
                    }
                    else -> {
                        throw IllegalArgumentException("Unsupported URI scheme: ${imageUri.scheme}")
                    }
                }

                // Call the suspend function to get the response string
                val responseJson = sendPhotoToOpenAI(photoFile)

                // Parse the JSON string into our data classes using Gson
                val parsedResponse = Gson().fromJson(responseJson, MealApiResponse::class.java)

                // Clean up temporary file if it was created
                if (imageUri.scheme == "content") {
                    photoFile.delete()
                }

                if (parsedResponse.error != null) {
                    _uiState.value = MealDetailsUiState.Error(parsedResponse.error)
                } else {
                    _uiState.value = MealDetailsUiState.Success(parsedResponse, imageUri)
                }

            } catch (e: Exception) {
                // If anything goes wrong, set the error state
                _uiState.value = MealDetailsUiState.Error("Failed to analyze image: ${e.message}")
            }
        }
    }
}

// This composable is the entry point for the Meal Details screen.
// It handles the ViewModel logic and UI state.
@Composable
fun MealDetailScreenDestination(
    imageUriString: String,
    navController: NavController,
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
                onBackClick = { navController.navigate(HomeRoute) },
                onDeleteClick = { /* TODO: Handle delete action */ }
            )
        }
        is MealDetailsUiState.Success -> {
            // On success, show the MealDetailPage with the data from the API
            MealDetailPage(
                mealData = state.mealDetails,
                mealImageUri = state.imageUri,
                onBackClick = { navController.navigate(HomeRoute) },
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
