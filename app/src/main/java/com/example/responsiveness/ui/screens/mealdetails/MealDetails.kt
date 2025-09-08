package com.example.responsiveness.ui.screens.mealdetails

import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.composables.icons.lucide.ChevronLeft
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Trash2
import com.example.anothercalorieapp.database.MealDatabase
import com.example.anothercalorieapp.ui.components.scanner.viewmodel.ErrorState
import com.example.anothercalorieapp.ui.components.scanner.viewmodel.MealApiResponse
import com.example.anothercalorieapp.ui.components.scanner.viewmodel.MealCorrectionLoading
import com.example.anothercalorieapp.ui.components.scanner.viewmodel.MealDetailPageLoading
import com.example.anothercalorieapp.ui.components.scanner.viewmodel.MealDetailsUiState
import com.example.anothercalorieapp.ui.components.scanner.viewmodel.MealDetailsViewModel
import com.example.anothercalorieapp.ui.components.scanner.viewmodel.MealDetailsViewModelFactory
import com.example.responsiveness.HomeRoute
import com.example.responsiveness.database.repository.MealRepository
import com.example.responsiveness.ui.screens.mealdetails.components.CustomBottomSheet
import com.example.responsiveness.ui.theme.DesignTokens

@SuppressLint("UnusedBoxWithConstraintsScope", "UnrememberedMutableInteractionSource")
@Composable
fun MealDetailsScreen(
    navController: NavController,
    mealImageUri: String? = null,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    mealDetails: MealApiResponse? = null,
    mealId: Long? = null,
    viewModel: MealDetailsViewModel,
    onBackClick: () -> Unit // <-- Add this parameter
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
    ) {
        val tokens = DesignTokens.provideTokens(availableWidth = maxWidth, availableHeight = maxHeight)
        val showSheet = remember { mutableStateOf(true) }
        val context = LocalContext.current

        // Initialize quantity in ViewModel when mealDetails change
        LaunchedEffect(mealDetails) {
            viewModel.setMealDetails(mealDetails)
        }
        val quantity by viewModel.quantity.collectAsState()
        val isFixingMeal by viewModel.isFixingMeal.collectAsState()
        val correctionMessage by viewModel.correctionMessage.collectAsState()
        val isLoading by viewModel.isLoading.collectAsState()
        val uiState by viewModel.uiState.collectAsState()
        val isCorrectionLoading by viewModel.isCorrectionLoading.collectAsState()

        // Render meal image as background if available
        if (mealImageUri != null) {
            AsyncImage(
                model = mealImageUri,
                contentDescription = "Meal Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Box(modifier = Modifier.fillMaxSize().background(Color.White))
        }

        // Top bar (unchanged)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = tokens.sDp(48.dp), start = tokens.sDp(16.dp), end = tokens.sDp(16.dp)),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick, // <-- Use the callback here
                modifier = Modifier
                    .size(tokens.sDp(44.dp))
                    .background(Color.Black.copy(alpha = 0.9f), shape = RoundedCornerShape(tokens.sDp(32.dp)))
                    .padding(tokens.sDp(12.dp)),
            ) {
                Icon(
                    imageVector = Lucide.ChevronLeft,
                    contentDescription = "Go back",
                    tint = Color.White,
                )
            }

            IconButton(
                onClick = {
                    mealId?.let { id ->
                        viewModel.deleteMeal(id, context) {
                            navController.navigate(HomeRoute)
                        }
                    }
                },
                modifier = Modifier
                    .size(tokens.sDp(44.dp))
                    .background(Color.White, shape = RoundedCornerShape(tokens.sDp(32.dp)))
                    .padding(tokens.sDp(12.dp)),
            ) {
                Icon(
                    imageVector = Lucide.Trash2,
                    contentDescription = "Delete Meal",
                    tint = Color.Black.copy(alpha = 0.9f),
                )
            }
        }

        // Bottom sheet overlays image
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (showSheet.value) {
                // Show shimmer for correction loading first
                if (isCorrectionLoading) {
                    mealImageUri?.let { uriStr ->
                        MealCorrectionLoading(
                            mealImageUri = runCatching { Uri.parse(uriStr) }.getOrNull(),
                            onBackClick = onBackClick
                        )
                    } ?: MealCorrectionLoading(mealImageUri = null, onBackClick = onBackClick)
                } else if (isLoading && !isFixingMeal && uiState !is MealDetailsUiState.Success) {
                    // Only show MealDetailPageLoading for initial scan loading
                    mealImageUri?.let { imageUri ->
                        Uri.parse(imageUri)?.let { parsedUri ->
                            MealDetailPageLoading(
                                mealImageUri = parsedUri,
                                onBackClick = { /* Handled by main screen */ },
                                navController = navController
                            )
                        }
                    }
                } else {
                    CustomBottomSheet(
                        onDismiss = { showSheet.value = false },
                        tokens = tokens,
                        mealName = mealDetails?.meal_name ?: "Meal",
                        nutrition = mealDetails?.nutrition,
                        healthGrade = mealDetails?.meal_nutrition_score ?: "?",
                        ingredients = mealDetails?.ingredients ?: emptyList(),
                        quantity = quantity,
                        onQuantityChange = { newQuantity ->
                            viewModel.setQuantity(newQuantity)
                            mealId?.let { id ->
                                viewModel.updateMealQuantity(id, newQuantity.toDouble(), context)
                            }
                        },
                        isFixingMeal = isFixingMeal,
                        correctionMessage = correctionMessage,
                        onCorrectionMessageChange = viewModel::onCorrectionMessageChange,
                        onSubmitCorrection = {
                            viewModel.submitCorrection(context)
                        },
                        onFixClick = viewModel::startMealFix,
                        onCancelFix = viewModel::cancelMealFix,
                        isLoading = isLoading
                    )
                }
            }
        }
    }
}

@Composable
fun DatabaseMealDetailsScreen(
    mealId: Long,
    navController: NavController,
    mealDetailsViewModel: MealDetailsViewModel? = null,
    source: String? = null
) {
    val context = LocalContext.current

    // Create ViewModel with proper factory if not provided
    val viewModel = mealDetailsViewModel ?: run {
        val database = remember { MealDatabase.getDatabase(context) }
        val repository = remember { MealRepository(database.mealDao()) }
        val factory = remember { MealDetailsViewModelFactory(repository, mealId.toInt()) }
        viewModel(factory = factory)
    }

    val mealWithDetails by viewModel.databaseMealDetailsState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isFixingMeal by viewModel.isFixingMeal.collectAsState()
    val correctionMessage by viewModel.correctionMessage.collectAsState()
    val isCorrectionLoading by viewModel.isCorrectionLoading.collectAsState()

    // Fetch meal details when composable is first shown
    LaunchedEffect(mealId) {
        viewModel.fetchMealDetailsById(mealId, context)
    }

    if (mealWithDetails == null) {
        // Show loading or error
        ErrorState(message = "Meal not found or still loading.")
    } else {
        // If correction loading, show correction shimmer page
        if (isCorrectionLoading) {
            MealCorrectionLoading(
                mealImageUri = mealWithDetails?.meal?.image_path?.let { runCatching { Uri.parse(it) }.getOrNull() },
                onBackClick = {
                    when (source) {
                        "analytics" -> navController.navigateUp()
                        else -> navController.navigate(HomeRoute)
                    }
                }
            )
        } else if (isLoading && !isFixingMeal && mealWithDetails?.meal?.image_path == null) {
            // Initial scan loading state (no data yet)
            mealWithDetails?.meal?.image_path?.let { imagePath ->
                Uri.parse(imagePath)?.let { parsedUri ->
                    MealDetailPageLoading(
                        mealImageUri = parsedUri,
                        onBackClick = { /* Handled by main screen */ },
                        navController = navController
                    )
                }
            }
        } else {
            MealDetailsScreen(
                navController = navController,
                mealImageUri = mealWithDetails?.meal?.image_path,
                mealDetails = MealApiResponse(
                    meal_name = mealWithDetails?.meal?.meal_name,
                    meal_quantity = mealWithDetails?.meal?.quantity,
                    meal_quantity_unit = "portion",
                    ingredients = mealWithDetails?.ingredients?.map {
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
                    nutrition = mealWithDetails?.nutrition?.let {
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
                    meal_nutrition_score = mealWithDetails?.meal?.meal_nutrition_score,
                    error = mealWithDetails?.meal?.error
                ),
                mealId = mealId,
                viewModel = viewModel,
                onBackClick = {
                    when (source) {
                        "analytics" -> navController.navigateUp()
                        else -> navController.navigate(HomeRoute)
                    }
                }
            )
        }
    }
}

// Helper: quick tap detection (down -> up without movement).
suspend fun PointerInputScope.detectTapOrPress(onResult: (Boolean) -> Unit) {
    awaitPointerEventScope {
        val down = awaitFirstDown(requireUnconsumed = false)
        val event = waitForUpOrCancellation()
        if (event != null && !event.pressed) {
            onResult(true)
        } else {
            onResult(false)
        }
    }
}