package com.example.anothercalorieapp.ui.components.home

import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.composables.icons.lucide.*
import com.example.anothercalorieapp.ui.components.mealdetails.HealthScore
import com.example.anothercalorieapp.ui.components.mealdetails.IngredientCard
import com.example.anothercalorieapp.ui.components.mealdetails.NutrientCard
import com.example.anothercalorieapp.ui.components.mealdetails.ShareAndQuantity
import com.example.anothercalorieapp.ui.components.scanner.viewmodel.MealApiResponse
import kotlin.math.roundToInt

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun MealDetailPage(
    mealData: MealApiResponse,
    mealImageUri: Uri,
    onBackClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val density = LocalDensity.current

    // Animation states
    var isContentVisible by remember { mutableStateOf(false) }

    // Trigger animation when component is composed
    LaunchedEffect(Unit) {
        isContentVisible = true
    }

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {
        val fullHeight = with(density) { constraints.maxHeight.toDp() }

        val minSheetHeight = 88.dp
        val maxSheetHeight = fullHeight * 0.70f
        val sheetInitialHeight = 200.dp

        var sheetHeight by remember { mutableStateOf(sheetInitialHeight) }

        // Use Coil library to load the image from the URI
        AsyncImage(
            model = mealImageUri,
            contentDescription = "Meal Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Top bar with back and delete icons, overlaid on the image
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 48.dp, start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .size(44.dp)
                    .background(Color.Black.copy(alpha = 0.9f), shape = RoundedCornerShape(32.dp))
                    .padding(12.dp),
            ) {
                Icon(
                    imageVector = Lucide.ChevronLeft,
                    contentDescription = "Go back",
                    tint = Color.White,
                )
            }

            IconButton(
                onClick = onDeleteClick,
                modifier = Modifier
                    .size(44.dp)
                    .background(Color.White, shape = RoundedCornerShape(32.dp))
                    .padding(12.dp),
            ) {
                Icon(
                    imageVector = Lucide.Trash2,
                    contentDescription = "Delete Meal",
                    tint = Color.Black.copy(alpha = 0.9f),
                )
            }
        }

        // Custom draggable bottom sheet
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(fullHeight)
                .offset { IntOffset(x = 0, y = (fullHeight - sheetHeight).roundToPx()) }
                .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
                .background(Color.White)
        ) {
            // Drag handle and gesture detector
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
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
                        .height(4.dp)
                        .width(40.dp)
                        .background(Color.Gray.copy(alpha = 0.5f), RoundedCornerShape(2.dp))
                )
            }

            // Content below the drag handle with fade-in animations
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                // Meal name with fade-in animation
                AnimatedVisibility(
                    visible = isContentVisible,
                    enter = fadeIn(animationSpec = tween(durationMillis = 800, delayMillis = 0))
                ) {
                    Text(
                        text = mealData.meal_name ?: "Unknown Meal",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Nutrient cards with simultaneous fade-in animation
                AnimatedVisibility(
                    visible = isContentVisible,
                    enter = fadeIn(animationSpec = tween(durationMillis = 800, delayMillis = 0))
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        mealData.nutrition?.let { nutrition ->
                            NutrientCard(
                                icon = Lucide.Flame,
                                value = nutrition.energy_kcal.toString(),
                                unitName = "Calories",
                                modifier = Modifier.weight(1f)
                            )
                            NutrientCard(
                                icon = Lucide.Wheat,
                                value = "${nutrition.carbohydrates_g}g",
                                unitName = "Carbs",
                                modifier = Modifier.weight(1f)
                            )
                            NutrientCard(
                                icon = Lucide.Beef,
                                value = "${nutrition.protein_g}g",
                                unitName = "Protein",
                                modifier = Modifier.weight(1f)
                            )
                            NutrientCard(
                                icon = Lucide.Droplets,
                                value = "${nutrition.fat_g}g",
                                unitName = "Fat",
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Health score with simultaneous fade-in animation
                AnimatedVisibility(
                    visible = isContentVisible,
                    enter = fadeIn(animationSpec = tween(durationMillis = 800, delayMillis = 0))
                ) {
                    HealthScore(
                        healthGrade = mealData.meal_nutrition_score ?: "N/A"
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Share and quantity with simultaneous fade-in animation
                AnimatedVisibility(
                    visible = isContentVisible,
                    enter = fadeIn(animationSpec = tween(durationMillis = 800, delayMillis = 0))
                ) {
                    ShareAndQuantity()
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Ingredients with simultaneous fade-in animation
                AnimatedVisibility(
                    visible = isContentVisible,
                    enter = fadeIn(animationSpec = tween(durationMillis = 800, delayMillis = 0))
                ) {
                    val ingredients = mealData.ingredients ?: emptyList()
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(ingredients.size) { index ->
                            val ingredient = ingredients[index]
                            IngredientCard(
                                ingredientName = ingredient.name,
                                calories = ingredient.calories,
                                quantity = "${ingredient.quantity} ${ingredient.unit}"
                            )

                            if (ingredients.size > 4 && index == 3) {
                                Spacer(modifier = Modifier.height(60.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}
