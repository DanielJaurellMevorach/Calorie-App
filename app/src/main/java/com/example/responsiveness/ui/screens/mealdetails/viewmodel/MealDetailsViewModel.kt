package com.example.anothercalorieapp.ui.components.scanner.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.anothercalorieapp.database.IngredientEntity
import com.example.anothercalorieapp.database.MealDatabase
import com.example.anothercalorieapp.database.MealWithDetails
import com.example.anothercalorieapp.database.NutritionEntity
import com.example.anothercalorieapp.database.UserEntity
import com.example.responsiveness.database.repository.MealRepository
import com.example.responsiveness.ui.components.general.resendMealDataToOpenAI
import com.example.responsiveness.ui.screens.mealdetails.MealDetailsScreen
import com.example.responsiveness.ui.screens.scanner.components.sendPhotoToOpenAI
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

// Data classes to represent the parsed JSON from OpenAI
data class MealApiResponse(
    val meal_name: String?,
    val meal_quantity: Double?, // Added for correct quantity
    val meal_quantity_unit: String?, // Added for correct unit
    val ingredients: List<Ingredient>?,
    val nutrition: Nutrition?,
    val meal_nutrition_score: String?,
    val error: String?
) {
    data class Ingredient(
        val name: String,
        val quantity: Double,
        val unit: String,
        val calories: Double,
        // Add other nutritional info if available from API
        val protein: Double?,
        val carbs: Double?,
        val fat: Double?
    )

    data class Nutrition(
        val energy_kcal: Double?,
        val protein_g: Double?,
        val carbohydrates_g: Double?,
        val fat_g: Double?,
        val fiber_g: Double?,
        val sugars_g: Double?,
        val sodium_mg: Double?,
        val cholesterol_mg: Double?
    )
}

// UI State sealed interface to represent the different states of the screen
sealed interface MealDetailsUiState {
    object Loading : MealDetailsUiState
    data class Success(val mealDetails: MealApiResponse, val imageUri: Uri, val mealId: Long) :
        MealDetailsUiState

    data class Error(val message: String) : MealDetailsUiState
}

/**
 * ViewModel for Meal Details screen. Handles image analysis, meal saving, and meal details management.
 */
class MealDetailsViewModel(
    private val mealRepository: MealRepository,
    private val mealId: Int
) : ViewModel() {
    init {
        Log.d("MealDetailsViewModel", "ViewModel created: $this")
    }

    private val _uiState = MutableStateFlow<MealDetailsUiState>(MealDetailsUiState.Loading)
    val uiState = _uiState.asStateFlow()

    // New state for database meal details
    private val _databaseMealDetailsState = MutableStateFlow<MealWithDetails?>(null)
    val databaseMealDetailsState = _databaseMealDetailsState.asStateFlow()

    // Track analysis state to prevent duplicate calls
    private var isAnalyzing = false
    private var analysisCompleted = false

    // Quantity state
    private val _quantity = MutableStateFlow(1)
    val quantity = _quantity.asStateFlow()

    // --- MODIFICATION 1 ---
    // Change the function to accept a file path string and selectedTimeOfDay.
    /**
     * Analyzes the image at the given path, saves meal data, and updates UI state.
     * @param imagePath Path to the image file.
     * @param context Android context.
     * @param selectedTimeOfDay Optional time of day for forced timestamp.
     * @param onMealSavedNavigate Optional callback for navigation after saving.
     */
    fun analyzeImage(
        imagePath: String,
        context: Context,
        selectedTimeOfDay: String? = null,
        onMealSavedNavigate: ((Long) -> Unit)? = null // callback for navigation
    ) {
        if (isAnalyzing || analysisCompleted) {
            Log.d("MealDetailsViewModel", "Analysis already in progress or completed.")
            return
        }
        isAnalyzing = true
        _uiState.value = MealDetailsUiState.Loading

        viewModelScope.launch {
            try {
                val photoFile = File(imagePath)
                if (!photoFile.exists()) {
                    throw IOException("Photo file does not exist at path: $imagePath")
                }
                // Fetch the only user (userId = 1) and get their apiKey
                val user: UserEntity? = mealRepository.getUser(1).first()
                val apiKey = user?.apiKey ?: throw IllegalStateException("No API key found for user")
                val responseJson = sendPhotoToOpenAI(photoFile, apiKey)
                Log.d("MealDetailsViewModel", "Raw OpenAI response: $responseJson")
                if (responseJson.isBlank() || responseJson.startsWith("HTTP Error")) {
                    Log.e("MealDetailsViewModel", "OpenAI response is invalid: $responseJson")
                    _uiState.value = MealDetailsUiState.Error("Failed to get data from AI.")
                    return@launch
                }
                val parsedResponse = try {
                    Gson().fromJson(responseJson, MealApiResponse::class.java)
                } catch (e: Exception) {
                    Log.e("MealDetailsViewModel", "Failed to parse OpenAI response", e)
                    _uiState.value = MealDetailsUiState.Error("Failed to parse AI response.")
                    return@launch
                }
                if (parsedResponse.error != null || parsedResponse.meal_name == null) {
                    Log.e(
                        "MealDetailsViewModel",
                        "Response has error or is missing meal name: ${parsedResponse.error}"
                    )
                    _uiState.value =
                        MealDetailsUiState.Error(parsedResponse.error ?: "No meal details found.")
                } else {
                    val database = MealDatabase.getDatabase(context)
                    val mealRepository = MealRepository(database.mealDao())
                    val currentUserId = 1L // Replace with actual user ID
                    val ingredientsMap = parsedResponse.ingredients?.map {
                        mapOf(
                            "name" to (it.name),
                            "quantity" to (it.quantity),
                            "unit" to (it.unit),
                            "calories" to (it.calories),
                            "protein" to (it.protein ?: 0.0),
                            "carbs" to (it.carbs ?: 0.0),
                            "fat" to (it.fat ?: 0.0)
                        )
                    } ?: emptyList()
                    val nutritionMap = parsedResponse.nutrition?.let {
                        mapOf(
                            "energy_kcal" to (it.energy_kcal ?: 0.0),
                            "protein_g" to (it.protein_g ?: 0.0),
                            "carbohydrates_g" to (it.carbohydrates_g ?: 0.0),
                            "fat_g" to (it.fat_g ?: 0.0),
                            "fiber_g" to (it.fiber_g ?: 0.0),
                            "sugars_g" to (it.sugars_g ?: 0.0),
                            "sodium_mg" to (it.sodium_mg ?: 0.0),
                            "cholesterol_mg" to (it.cholesterol_mg ?: 0.0)
                        )
                    }
                    val imageUri = Uri.fromFile(photoFile)
                    // --- TIMESTAMP LOGIC ---
                    val forcedTimestamp = getForcedTimestampIfNeeded(selectedTimeOfDay)
                    Log.d("MealDetailsViewModel", "selectedTimeOfDay=$selectedTimeOfDay, forcedTimestamp=$forcedTimestamp, currentTime=${System.currentTimeMillis()}")
                    val mealId = mealRepository.saveMealFromJson(
                        userId = currentUserId,
                        mealName = parsedResponse.meal_name,
                        ingredients = ingredientsMap,
                        nutrition = nutritionMap,
                        nutritionScore = parsedResponse.meal_nutrition_score,
                        error = null,
                        imagePath = imageUri.toString(),
                        timestamp = forcedTimestamp // Pass forced timestamp if needed
                    )
                    Log.d("MealDetailsViewModel", "Saved meal with ID: $mealId, timestamp used: ${forcedTimestamp ?: System.currentTimeMillis()}")
                    _uiState.value = MealDetailsUiState.Success(parsedResponse, imageUri, mealId)
                    analysisCompleted = true
                    onMealSavedNavigate?.invoke(mealId) // <-- navigate after saving
                }
            } catch (e: Exception) {
                Log.e("MealDetailsViewModel", "Analysis failed", e)
                _uiState.value = MealDetailsUiState.Error("Failed to analyze image: ${e.message}")
            } finally {
                isAnalyzing = false
                Log.d("MealDetailsViewModel", "Analysis process completed.")
            }
        }
    }

    /**
     * Returns forced timestamp if needed based on selected time of day, otherwise null.
     */
    private fun getForcedTimestampIfNeeded(selectedTimeOfDay: String?): Long? {
        if (selectedTimeOfDay == null) return null
        val now = LocalDateTime.now()
        val today = now.toLocalDate()
        val currentHour = now.hour
        val currentMinute = now.minute
        val currentSecond = now.second
        return when (selectedTimeOfDay) {
            "Morning" -> {
                // Morning: 5:00:00 to 10:59:59
                if (currentHour in 5..10) null
                else {
                    val forcedDateTime = LocalDateTime.of(today, LocalTime.of(10, 59, 59))
                    forcedDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                }
            }
            "Afternoon" -> {
                // Afternoon: 11:00:00 to 16:59:59
                if (currentHour in 11..16) null
                else {
                    val forcedDateTime = LocalDateTime.of(today, LocalTime.of(16, 59, 59))
                    forcedDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                }
            }
            "Evening" -> {
                // Evening: 17:00:00 to 23:59:59 OR 0:00:00 to 4:59:59 (spans two days)
                if (currentHour in 17..23) {
                    null // Already in evening window
                } else if (currentHour in 0..4) {
                    // Force to 23:59:59 of previous day
                    val yesterday = today.minusDays(1)
                    val forcedDateTime = LocalDateTime.of(yesterday, LocalTime.of(23, 59, 59))
                    forcedDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                } else {
                    // Force to 23:59:59 of today
                    val forcedDateTime = LocalDateTime.of(today, LocalTime.of(23, 59, 59))
                    forcedDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                }
            }
            else -> null
        }
    }

    /**
     * Fetches meal details by ID from the database and updates state.
     */
    fun fetchMealDetailsById(mealId: Long, context: Context) {
        viewModelScope.launch {
            val database = MealDatabase.getDatabase(context)
            val repository = MealRepository(database.mealDao())
            val mealWithDetails = repository.getMealWithDetails(mealId)
            _databaseMealDetailsState.value = mealWithDetails
        }
    }

    /**
     * Updates the quantity of a meal in the database and refreshes state.
     */
    fun updateMealQuantity(mealId: Long, newQuantity: Double, context: Context) {
        Log.d("MealDetailsViewModel", "updateMealQuantity called with mealId=$mealId, newQuantity=$newQuantity")
        viewModelScope.launch(Dispatchers.IO) {
            val database = MealDatabase.getDatabase(context)
            val repository = MealRepository(database.mealDao())
            Log.d("MealDetailsViewModel", "Calling repository.updateMealQuantity($mealId, $newQuantity)")
            repository.updateMealQuantity(mealId, newQuantity)
            // Optionally refresh local state
            val updatedMeal = repository.getMealWithDetails(mealId)
            Log.d("MealDetailsViewModel", "Updated meal details: $updatedMeal")
            _databaseMealDetailsState.value = updatedMeal
        }
    }

    /**
     * Deletes a meal from the database and invokes callback on completion.
     */
    fun deleteMeal(mealId: Long, context: Context, onDeleted: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val database = MealDatabase.getDatabase(context)
            val mealRepository = MealRepository(database.mealDao())
            mealRepository.deleteMeal(mealId)
            // Switch to main thread for navigation callback
            launch(Dispatchers.Main) {
                onDeleted()
            }
        }
    }

    // New function to set quantity
    /**
     * Sets the quantity for the meal details UI state.
     */
    fun setQuantity(newQuantity: Int) {
        _quantity.value = newQuantity
    }

    /**
     * Updates quantity when meal details are loaded.
     */
    fun setMealDetails(mealDetails: MealApiResponse?) {
        _quantity.value = mealDetails?.meal_quantity?.toInt() ?: 1
        // ...other logic to update UI state if needed...
    }

    // Meal correction feature states
    private val _isFixingMeal = MutableStateFlow(false)
    val isFixingMeal: StateFlow<Boolean> = _isFixingMeal.asStateFlow()

    private val _correctionMessage = MutableStateFlow("")
    val correctionMessage: StateFlow<String> = _correctionMessage.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Separate loading flag for meal corrections (prevents triggering scan logic)
    private val _isCorrectionLoading = MutableStateFlow(false)
    val isCorrectionLoading: StateFlow<Boolean> = _isCorrectionLoading.asStateFlow()

    /**
     * Starts the meal correction process.
     */
    fun startMealFix() {
        _isFixingMeal.value = true
    }

    /**
     * Cancels/ends the meal correction process and clears the message.
     */
    fun cancelMealFix() {
        _isFixingMeal.value = false
        _correctionMessage.value = ""
    }

    /**
     * Updates the correction message.
     */
    fun onCorrectionMessageChange(message: String) {
        _correctionMessage.value = message
    }

    /**
     * Submits the correction for analysis.
     */
    fun submitCorrection(context: Context) {
        viewModelScope.launch {
            _isCorrectionLoading.value = true
            val currentMealWithDetails = _databaseMealDetailsState.value
            val correction = _correctionMessage.value

            if (currentMealWithDetails != null && correction.isNotBlank()) {
                try {
                    val mealDataJson = constructMealDataJson(currentMealWithDetails)
                    Log.d("MealDetailsViewModel", "Sending correction with meal data: $mealDataJson")
                    Log.d("MealDetailsViewModel", "User correction: $correction")
                    // Fetch the only user (userId = 1) and get their apiKey
                    val user: UserEntity? = mealRepository.getUser(1).first()
                    val apiKey = user?.apiKey ?: throw IllegalStateException("No API key found for user")
                    val newAnalysisJson = resendMealDataToOpenAI(mealDataJson, correction, apiKey)
                    Log.d("MealDetailsViewModel", "Received corrected analysis: $newAnalysisJson")
                    val correctedMealData = try {
                        Gson().fromJson(newAnalysisJson, MealApiResponse::class.java)
                    } catch (e: Exception) {
                        Log.e("MealDetailsViewModel", "Failed to parse corrected meal data", e)
                        null
                    }
                    if (correctedMealData != null && correctedMealData.meal_name != null) {
                        updateMealWithCorrectedData(currentMealWithDetails.meal.id, correctedMealData, context)
                        val updatedMeal = mealRepository.getMealWithDetails(currentMealWithDetails.meal.id)
                        _databaseMealDetailsState.value = updatedMeal
                        Log.d("MealDetailsViewModel", "Successfully updated meal with correction")
                    } else {
                        Log.e("MealDetailsViewModel", "Invalid corrected meal data received")
                    }
                } catch (e: Exception) {
                    Log.e("MealDetailsViewModel", "Error submitting correction", e)
                } finally {
                    _isCorrectionLoading.value = false
                    _isFixingMeal.value = false
                    _correctionMessage.value = ""
                }
            } else {
                _isCorrectionLoading.value = false
            }
        }
    }

    /**
     * Updates an existing meal with corrected data from OpenAI
     */
    private suspend fun updateMealWithCorrectedData(mealId: Long, correctedData: MealApiResponse, context: Context) {
        try {
            val database = MealDatabase.getDatabase(context)
            val dao = database.mealDao()

            // Update meal entity
            dao.updateMealDetails(
                mealId = mealId,
                mealName = correctedData.meal_name ?: "Unknown Meal",
                quantity = correctedData.meal_quantity ?: 1.0,
                nutritionScore = correctedData.meal_nutrition_score
            )

            // Delete existing ingredients and nutrition for this meal
            dao.deleteIngredientsForMeal(mealId)
            dao.deleteNutritionForMeal(mealId)

            // Insert new ingredients
            correctedData.ingredients?.forEach { ingredient ->
                dao.insertIngredient(
                    IngredientEntity(
                        meal_id = mealId,
                        name = ingredient.name,
                        quantity = ingredient.quantity,
                        unit = ingredient.unit,
                        calories = ingredient.calories.toDouble(),
                        protein_g = ingredient.protein ?: 0.0,
                        carbohydrates_g = ingredient.carbs ?: 0.0,
                        fat_g = ingredient.fat ?: 0.0
                    )
                )
            }

            // Insert new nutrition
            correctedData.nutrition?.let { nutrition ->
                dao.insertNutrition(
                    NutritionEntity(
                        meal_id = mealId,
                        energy_kcal = nutrition.energy_kcal ?: 0.0,
                        protein_g = nutrition.protein_g ?: 0.0,
                        carbohydrates_g = nutrition.carbohydrates_g ?: 0.0,
                        fat_g = nutrition.fat_g ?: 0.0,
                        fiber_g = nutrition.fiber_g ?: 0.0,
                        sugars_g = nutrition.sugars_g ?: 0.0,
                        sodium_mg = nutrition.sodium_mg ?: 0.0,
                        cholesterol_mg = nutrition.cholesterol_mg ?: 0.0
                    )
                )
            }

            Log.d("MealDetailsViewModel", "Successfully updated meal $mealId with corrected data")
        } catch (e: Exception) {
            Log.e("MealDetailsViewModel", "Failed to update meal with corrected data", e)
            throw e
        }
    }

    private fun constructMealDataJson(mealWithDetails: MealWithDetails): String {
        val mealData = MealApiResponse(
            meal_name = mealWithDetails.meal.meal_name,
            meal_quantity = mealWithDetails.meal.quantity,
            meal_quantity_unit = "portion", // Assuming default, adjust if stored differently
            ingredients = mealWithDetails.ingredients.map {
                MealApiResponse.Ingredient(
                    name = it.name,
                    quantity = it.quantity,
                    unit = it.unit,
                    calories = it.calories,
                    protein = it.protein_g,
                    carbs = it.carbohydrates_g,
                    fat = it.fat_g
                )
            },
            nutrition = mealWithDetails.nutrition?.let {
                MealApiResponse.Nutrition(
                    energy_kcal = it.energy_kcal,
                    protein_g = it.protein_g,
                    carbohydrates_g = it.carbohydrates_g,
                    fat_g = it.fat_g,
                    fiber_g = it.fiber_g,
                    sugars_g = it.sugars_g,
                    sodium_mg = it.sodium_mg,
                    cholesterol_mg = it.cholesterol_mg
                )
            },
            meal_nutrition_score = mealWithDetails.meal.meal_nutrition_score,
            error = mealWithDetails.meal.error
        )
        return Gson().toJson(mealData)
    }
}

// This composable is the entry point for the Meal Details screen.
// It handles the ViewModel logic and UI state.
@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun MealDetailScreenDestination(
    // --- MODIFICATION 3 ---
    // Receive the encoded image path string.
    imagePath: String,
    navController: NavController,
    mealDetailsViewModel: MealDetailsViewModel = viewModel()
) {
    val uiState by mealDetailsViewModel.uiState.collectAsState()
    // Decode the path back to its original form.
    val decodedImagePath = remember { Uri.decode(imagePath) }
    val context = LocalContext.current

    // This LaunchedEffect will trigger the analysis ONCE.
    LaunchedEffect(key1 = decodedImagePath) {
        if (mealDetailsViewModel.uiState.value !is MealDetailsUiState.Success) {
            mealDetailsViewModel.analyzeImage(decodedImagePath, context)
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        when (val state = uiState) {
            is MealDetailsUiState.Loading -> {
                // --- MODIFICATION 4 ---
                // Create a Uri from the file path for display purposes ONLY.
                val imageUriForDisplay = remember { Uri.fromFile(File(decodedImagePath)) }
                MealDetailPageLoading(
                    mealImageUri = imageUriForDisplay,
                    onBackClick = { navController.popBackStack() },
                    navController = navController
                )
            }

            is MealDetailsUiState.Success -> {
                MealDetailsScreen(
                    navController = navController,
                    mealImageUri = state.imageUri.toString(),
                    mealDetails = state.mealDetails,
                    mealId = state.mealId,
                    viewModel = mealDetailsViewModel,
                    onBackClick = { navController.navigate("home") }
                )
            }

            is MealDetailsUiState.Error -> {
                ErrorState(message = state.message)
            }
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
        )
    }
}