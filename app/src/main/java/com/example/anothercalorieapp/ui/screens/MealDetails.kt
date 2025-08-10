package com.example.anothercalorieapp.ui.components.home

import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.composables.icons.lucide.Beef
import com.composables.icons.lucide.ChevronLeft
import com.composables.icons.lucide.Droplets
import com.composables.icons.lucide.Flame
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Trash2
import com.composables.icons.lucide.Wheat
import com.example.anothercalorieapp.ui.components.mealdetails.HealthScore
import com.example.anothercalorieapp.ui.components.mealdetails.IngredientCard
import com.example.anothercalorieapp.ui.components.mealdetails.NutrientCard
import com.example.anothercalorieapp.ui.components.mealdetails.ShareAndQuantity
import com.example.anothercalorieapp.ui.components.scanner.viewmodel.MealApiResponse
import com.example.anothercalorieapp.ui.utils.getResponsiveCornerRadius
import com.example.anothercalorieapp.ui.utils.getResponsiveFontSize
import com.example.anothercalorieapp.ui.utils.getResponsiveIconSize
import com.example.anothercalorieapp.ui.utils.getResponsivePadding
import com.example.anothercalorieapp.ui.utils.getResponsiveSize
import com.example.anothercalorieapp.ui.utils.getResponsiveSpacing

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

        val minSheetHeight = getResponsiveSize(88.dp)
        val maxSheetHeight = fullHeight * 0.70f
        val sheetInitialHeight = getResponsiveSize(200.dp)

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
                .padding(
                    top = getResponsivePadding(48.dp),
                    start = getResponsivePadding(16.dp),
                    end = getResponsivePadding(16.dp)
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .size(getResponsiveSize(44.dp))
                    .background(Color.Black.copy(alpha = 0.9f), shape = RoundedCornerShape(getResponsiveCornerRadius(32.dp)))
                    .padding(getResponsivePadding(12.dp)),
            ) {
                Icon(
                    imageVector = Lucide.ChevronLeft,
                    contentDescription = "Go back",
                    tint = Color.White,
                    modifier = Modifier.size(getResponsiveIconSize(20.dp))
                )
            }

            IconButton(
                onClick = onDeleteClick,
                modifier = Modifier
                    .size(getResponsiveSize(44.dp))
                    .background(Color.White, shape = RoundedCornerShape(getResponsiveCornerRadius(32.dp)))
                    .padding(getResponsivePadding(12.dp)),
            ) {
                Icon(
                    imageVector = Lucide.Trash2,
                    contentDescription = "Delete Meal",
                    tint = Color.Black.copy(alpha = 0.9f),
                    modifier = Modifier.size(getResponsiveIconSize(20.dp))
                )
            }
        }

        // Custom draggable bottom sheet
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(fullHeight)
                .offset { IntOffset(x = 0, y = (fullHeight - sheetHeight).roundToPx()) }
                .clip(RoundedCornerShape(topStart = getResponsiveCornerRadius(40.dp), topEnd = getResponsiveCornerRadius(40.dp)))
                .background(Color.White)
        ) {
            // Drag handle and gesture detector
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(getResponsiveSize(40.dp))
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
                        .height(getResponsiveSize(4.dp))
                        .width(getResponsiveSize(40.dp))
                        .background(Color.Gray.copy(alpha = 0.5f), RoundedCornerShape(getResponsiveCornerRadius(2.dp)))
                )
            }

            // Content below the drag handle with fade-in animations
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = getResponsivePadding(16.dp))
            ) {
                // Meal name with fade-in animation
                AnimatedVisibility(
                    visible = isContentVisible,
                    enter = fadeIn(animationSpec = tween(durationMillis = 800, delayMillis = 0))
                ) {
                    Text(
                        text = mealData.meal_name ?: "Unknown Meal",
                        fontSize = getResponsiveFontSize(20.sp, minScale = 0.8f, maxScale = 1.0f),
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(getResponsiveSpacing(24.dp)))

                // Nutrient cards with simultaneous fade-in animation
                AnimatedVisibility(
                    visible = isContentVisible,
                    enter = fadeIn(animationSpec = tween(durationMillis = 800, delayMillis = 0))
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(getResponsiveSpacing(16.dp)),
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

                Spacer(modifier = Modifier.height(getResponsiveSpacing(28.dp)))

                // Health score with simultaneous fade-in animation
                AnimatedVisibility(
                    visible = isContentVisible,
                    enter = fadeIn(animationSpec = tween(durationMillis = 800, delayMillis = 0))
                ) {
                    HealthScore(
                        healthGrade = mealData.meal_nutrition_score ?: "N/A"
                    )
                }

                Spacer(modifier = Modifier.height(getResponsiveSpacing(28.dp)))

                // Share and quantity with simultaneous fade-in animation
                AnimatedVisibility(
                    visible = isContentVisible,
                    enter = fadeIn(animationSpec = tween(durationMillis = 800, delayMillis = 0))
                ) {
                    ShareAndQuantity()
                }

                Spacer(modifier = Modifier.height(getResponsiveSpacing(28.dp)))

                // Ingredients with simultaneous fade-in animation
                AnimatedVisibility(
                    visible = isContentVisible,
                    enter = fadeIn(animationSpec = tween(durationMillis = 800, delayMillis = 0))
                ) {
                    val ingredients = mealData.ingredients ?: emptyList()
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(getResponsiveSpacing(8.dp))
                    ) {
                        items(ingredients.size) { index ->
                            val ingredient = ingredients[index]
                            IngredientCard(
                                ingredientName = ingredient.name,
                                calories = ingredient.calories,
                                quantity = "${ingredient.quantity} ${ingredient.unit}"
                            )

                            if (ingredients.size > 4 && index == 3) {
                                Spacer(modifier = Modifier.height(getResponsiveSize(60.dp)))
                            }
                        }
                    }
                }
            }
        }
    }
}
