package com.example.caloriecounter.ui.components.openai

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.ChevronLeft
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Menu

@Composable
fun NutritionAnalysisDisplay(
    nutritionResult: NutritionResult?,
    isLoading: Boolean = false,
    onNavigateToHome: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val primaryBlue = Color(0xFF4463DE)
    val borderColor = Color(0xFFA2A2A2).copy(0.15f)

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Custom App Bar for Nutrition Results
            NutritionResultsAppBar(
                onBackClick = onNavigateToHome,
                modifier = Modifier.padding(top = 16.dp, start = 20.dp, end = 20.dp, bottom = 16.dp)
            )

            // Scrollable Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                if (isLoading) {
                    // Loading State
                    LoadingContent(primaryBlue = primaryBlue, borderColor = borderColor)
                } else if (nutritionResult != null) {
                    // Actual Content
                    NutritionContent(
                        nutritionResult = nutritionResult,
                        primaryBlue = primaryBlue,
                        borderColor = borderColor
                    )
                }

                // Add extra space at bottom for sticky button
                Spacer(modifier = Modifier.height(100.dp))
            }
        }

        // Sticky Action Button at Bottom
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            Button(
                onClick = { onNavigateToHome() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = primaryBlue
                ),
                shape = RoundedCornerShape(28.dp),
                enabled = !isLoading
            ) {
                Text(
                    text = if (isLoading) "Analyzing..." else "I ate this!",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.W600
                )
            }
        }
    }
}

@Composable
private fun LoadingContent(
    primaryBlue: Color,
    borderColor: Color
) {
    // Loading Meal Name Card
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = primaryBlue,
                shape = RoundedCornerShape(24.dp)
            )
            .padding(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Analyzing your meal...",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.W600
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Please wait while we process the image",
                    color = Color.White.copy(0.8f),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.W400
                )
            }

            // Loading Indicator
            androidx.compose.material3.CircularProgressIndicator(
                modifier = Modifier.size(56.dp),
                color = Color.White,
                strokeWidth = 3.dp
            )
        }
    }

    Spacer(modifier = Modifier.height(24.dp))

    // Loading Skeleton Cards
    repeat(3) { index ->
        LoadingCard(
            borderColor = borderColor,
            title = when (index) {
                0 -> "Macronutrients"
                1 -> "Additional Info"
                else -> "Ingredients"
            }
        )
        if (index < 2) Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun LoadingCard(
    borderColor: Color,
    title: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color.White,
                shape = RoundedCornerShape(20.dp)
            )
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(20.dp)
            )
            .padding(20.dp)
    ) {
        Column {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.W600,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Loading skeleton rows
            repeat(3) { index ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .height(16.dp)
                            .fillMaxWidth(0.4f)
                            .background(
                                color = Color.Gray.copy(0.2f),
                                shape = RoundedCornerShape(8.dp)
                            )
                    )
                    Box(
                        modifier = Modifier
                            .height(16.dp)
                            .fillMaxWidth(0.3f)
                            .background(
                                color = Color.Gray.copy(0.2f),
                                shape = RoundedCornerShape(8.dp)
                            )
                    )
                }
                if (index < 2) Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun NutritionContent(
    nutritionResult: NutritionResult,
    primaryBlue: Color,
    borderColor: Color
) {
    // Meal Name and Grade Card
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = primaryBlue,
                shape = RoundedCornerShape(24.dp)
            )
            .padding(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = nutritionResult.mealName,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.W600
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${nutritionResult.nutrition?.energyKcal?.toInt() ?: 0} calories",
                    color = Color.White.copy(0.8f),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.W400
                )
            }

            // Grade Badge
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        color = getGradeColor(nutritionResult.mealNutritionScore),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = nutritionResult.mealNutritionScore,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(24.dp))

    // Macronutrients Card
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color.White,
                shape = RoundedCornerShape(20.dp)
            )
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(20.dp)
            )
            .padding(20.dp)
    ) {
        Column {
            Text(
                text = "Macronutrients",
                fontSize = 18.sp,
                fontWeight = FontWeight.W600,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            MacronutrientRow(
                label = "Protein",
                value = "${nutritionResult.nutrition?.proteinG?.toInt() ?: 0}g",
                color = Color(0xFF10B981)
            )

            Spacer(modifier = Modifier.height(12.dp))

            MacronutrientRow(
                label = "Carbohydrates",
                value = "${nutritionResult.nutrition?.carbohydratesG?.toInt() ?: 0}g",
                color = Color(0xFF3B82F6)
            )

            Spacer(modifier = Modifier.height(12.dp))

            MacronutrientRow(
                label = "Fat",
                value = "${nutritionResult.nutrition?.fatG?.toInt() ?: 0}g",
                color = Color(0xFFF59E0B)
            )

            Spacer(modifier = Modifier.height(12.dp))

            MacronutrientRow(
                label = "Fiber",
                value = "${nutritionResult.nutrition?.fiberG?.toInt() ?: 0}g",
                color = Color(0xFF8B5CF6)
            )
        }
    }

    Spacer(modifier = Modifier.height(24.dp))

    // Additional Nutrition Info Card
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color.White,
                shape = RoundedCornerShape(20.dp)
            )
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(20.dp)
            )
            .padding(20.dp)
    ) {
        Column {
            Text(
                text = "Additional Info",
                fontSize = 18.sp,
                fontWeight = FontWeight.W600,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            NutritionDetailRow(
                label = "Sugars",
                value = "${nutritionResult.nutrition?.sugarsG?.toInt() ?: 0}g"
            )

            Spacer(modifier = Modifier.height(12.dp))

            NutritionDetailRow(
                label = "Sodium",
                value = "${nutritionResult.nutrition?.sodiumMg?.toInt() ?: 0}mg"
            )

            Spacer(modifier = Modifier.height(12.dp))

            NutritionDetailRow(
                label = "Cholesterol",
                value = "${nutritionResult.nutrition?.cholesterolMg?.toInt() ?: 0}mg"
            )
        }
    }

    Spacer(modifier = Modifier.height(24.dp))

    // Ingredients Card
    if (nutritionResult.ingredients?.isNotEmpty() == true) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(20.dp)
                )
                .border(
                    width = 1.dp,
                    color = borderColor,
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(20.dp)
        ) {
            Column {
                Text(
                    text = "Ingredients",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.W600,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    nutritionResult.ingredients.forEach { ingredient ->
                        IngredientRow(ingredient = ingredient)
                    }
                }
            }
        }
    }
}

@Composable
private fun NutritionResultsAppBar(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Back button
        Box(
            modifier = Modifier
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    onBackClick()
                }
                .padding(8.dp)
        ) {
            androidx.compose.material3.Icon(
                imageVector = Lucide.ChevronLeft,
                contentDescription = "Back",
                modifier = Modifier.size(24.dp),
                tint = Color.Black
            )
        }

        // Title
        Text(
            text = "Details",
            fontSize = 20.sp,
            fontWeight = FontWeight.W600,
            color = Color.Black
        )

        // Menu button
        Box(
            modifier = Modifier
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    // TODO: Handle menu click
                }
                .padding(8.dp)
        ) {
            Icon(
                imageVector = Lucide.Menu,
                contentDescription = "Menu",
                modifier = Modifier.size(24.dp),
                tint = Color.Black
            )
        }
    }
}

@Composable
private fun MacronutrientRow(
    label: String,
    value: String,
    color: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(
                        color = color,
                        shape = CircleShape
                    )
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = label,
                fontSize = 16.sp,
                color = Color.Black,
                fontWeight = FontWeight.W400
            )
        }
        Text(
            text = value,
            fontSize = 16.sp,
            color = Color.Black,
            fontWeight = FontWeight.W600
        )
    }
}

@Composable
private fun IngredientRow(ingredient: Ingredient) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color(0xFFF8F9FA),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = ingredient.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.W500,
                color = Color.Black
            )
            Text(
                text = "${ingredient.quantity.toInt()}${ingredient.unit}",
                fontSize = 12.sp,
                fontWeight = FontWeight.W400,
                color = Color.Gray
            )
        }
        Text(
            text = "${ingredient.calories.toInt()} cal",
            fontSize = 14.sp,
            fontWeight = FontWeight.W600,
            color = Color.Black
        )
    }
}

@Composable
private fun NutritionDetailRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 16.sp,
            color = Color.Black,
            fontWeight = FontWeight.W400
        )
        Text(
            text = value,
            fontSize = 16.sp,
            color = Color.Black,
            fontWeight = FontWeight.W600
        )
    }
}

private fun getGradeColor(grade: String): Color {
    return when (grade.uppercase()) {
        "A" -> Color(0xFF10B981) // Green
        "B" -> Color(0xFF3B82F6) // Blue
        "C" -> Color(0xFFF59E0B) // Orange
        "D" -> Color(0xFFEF4444) // Red
        else -> Color.Gray
    }
}
