package com.example.anothercalorieapp.ui.components.scanner.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
