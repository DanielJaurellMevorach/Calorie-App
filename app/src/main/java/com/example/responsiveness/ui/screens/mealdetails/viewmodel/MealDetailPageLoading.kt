package com.example.anothercalorieapp.ui.components.scanner.viewmodel

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.composables.icons.lucide.Beef
import com.composables.icons.lucide.ChevronLeft
import com.composables.icons.lucide.Droplets
import com.composables.icons.lucide.Flame
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Wheat
import com.example.anothercalorieapp.database.MealDatabase
import com.example.responsiveness.DatabaseMealDetailRoute
import com.example.responsiveness.database.repository.MealRepository
import com.example.responsiveness.ui.theme.DesignTokens

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun MealDetailPageLoading(
    mealImageUri: Uri,
    onBackClick: () -> Unit,
    navController: NavController, // Add NavController for navigation
    selectedTimeOfDay: String? = null // NEW: pass selected time of day
) {
    val context = LocalContext.current

    // Create ViewModel with proper factory using a dummy mealId (0) since this is for new scans
    val database = remember { MealDatabase.getDatabase(context) }
    val repository = remember { MealRepository(database.mealDao()) }
    val factory = remember { MealDetailsViewModelFactory(repository, 0) } // Use 0 as dummy ID for new scans
    val viewModel: MealDetailsViewModel = viewModel(factory = factory)

    val uiState by viewModel.uiState.collectAsState()

    // Convert URI to file path string
    val imagePath = mealImageUri.path ?: ""

    // Trigger analysis when screen is shown
    LaunchedEffect(imagePath, selectedTimeOfDay) {
        if (imagePath.isNotEmpty()) {
            Log.d("MealDetailPageLoading", "selectedTimeOfDay=$selectedTimeOfDay")
            viewModel.analyzeImage(
                imagePath,
                context,
                selectedTimeOfDay,
                onMealSavedNavigate = { mealId ->
                    navController.navigate(DatabaseMealDetailRoute(mealId, source = "scan"))
                }
            )
        }
    }

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {
        val density = LocalDensity.current
        val tokens = DesignTokens.provideTokens(maxWidth, maxHeight)
        val fullHeight = maxHeight
        val minSheetHeight = tokens.sDp(88.dp)
        val maxSheetHeight = fullHeight * 0.70f
        val sheetInitialHeight = tokens.sDp(200.dp)
        var sheetHeight by remember { mutableStateOf(sheetInitialHeight) }

        // Show image as background
        AsyncImage(
            model = mealImageUri,
            contentDescription = "Meal Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Show loading indicator or handle navigation on success
        when (uiState) {
            is MealDetailsUiState.Loading -> {
//                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//                    androidx.compose.material3.CircularProgressIndicator()
//                }
            }
            is MealDetailsUiState.Success -> {
                val successState = uiState as MealDetailsUiState.Success
                LaunchedEffect(successState) {
                    navController.navigate(
                        DatabaseMealDetailRoute(successState.mealId, source = "scan")
                    )
                }
            }
            is MealDetailsUiState.Error -> {
                val errorState = uiState as MealDetailsUiState.Error
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(errorState.message, color = Color.White)
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = tokens.sDp(48.dp), start = tokens.sDp(16.dp), end = tokens.sDp(16.dp)),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
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
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(fullHeight)
                .offset { IntOffset(x = 0, y = (fullHeight - sheetHeight).roundToPx()) }
                .clip(RoundedCornerShape(topStart = tokens.sDp(40.dp), topEnd = tokens.sDp(40.dp)))
                .background(Color.White)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(tokens.sDp(40.dp))
                    .pointerInput(Unit) {
                        detectVerticalDragGestures { change, dragAmount ->
                            change.consume()
                            val newHeight = sheetHeight - with(density) { dragAmount.toDp() }
                            sheetHeight = newHeight.coerceIn(minSheetHeight, maxSheetHeight)
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Spacer(
                    modifier = Modifier
                        .height(tokens.sDp(4.dp))
                        .width(tokens.sDp(40.dp))
                        .background(Color.Gray.copy(alpha = 0.5f), RoundedCornerShape(tokens.sDp(2.dp)))
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = tokens.innerPadding)
            ) {
                ShimmerMealName(tokens = tokens)
                Spacer(modifier = Modifier.height(tokens.sDp(24.dp)))
                Row (
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(tokens.sDp(8.dp)),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ShimmerNutrientCard(icon = Lucide.Flame, tokens = tokens, modifier = Modifier.weight(1f))
                    ShimmerNutrientCard(icon = Lucide.Wheat, tokens = tokens, modifier = Modifier.weight(1f))
                    ShimmerNutrientCard(icon = Lucide.Beef, tokens = tokens, modifier = Modifier.weight(1f))
                    ShimmerNutrientCard(icon = Lucide.Droplets, tokens = tokens, modifier = Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(tokens.sDp(28.dp)))
                ShimmerHealthScore(tokens = tokens)
                Spacer(modifier = Modifier.height(tokens.sDp(28.dp)))
                ShimmerShareAndQuantity(tokens = tokens)
                Spacer(modifier = Modifier.height(tokens.sDp(28.dp)))
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(tokens.sDp(8.dp))
                ) {
                    items(1) {
                        ShimmerIngredientCard(tokens = tokens)
                    }
                }
            }
        }
    }
}