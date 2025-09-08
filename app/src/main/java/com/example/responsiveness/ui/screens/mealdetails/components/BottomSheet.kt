package com.example.responsiveness.ui.screens.mealdetails.components

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollFactory
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.Beef
import com.composables.icons.lucide.ChevronLeft
import com.composables.icons.lucide.Droplets
import com.composables.icons.lucide.Flame
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Wheat
import com.example.anothercalorieapp.ui.components.mealdetails.HealthScore
import com.example.anothercalorieapp.ui.components.mealdetails.IngredientCard
import com.example.anothercalorieapp.ui.components.mealdetails.ShareAndQuantity
import com.example.anothercalorieapp.ui.components.scanner.viewmodel.MealApiResponse
import com.example.responsiveness.ui.theme.DesignTokens
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.ui.unit.Dp

@OptIn(ExperimentalFoundationApi::class)
@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun CustomBottomSheet(
    onDismiss: () -> Unit,
    tokens: DesignTokens.Tokens,
    mealName: String,
    nutrition: MealApiResponse.Nutrition?,
    healthGrade: String,
    ingredients: List<MealApiResponse.Ingredient>,
    quantity: Int,
    onQuantityChange: (Int) -> Unit,
    isFixingMeal: Boolean,
    correctionMessage: String,
    onCorrectionMessageChange: (String) -> Unit,
    onSubmitCorrection: () -> Unit,
    onFixClick: () -> Unit,
    onCancelFix: () -> Unit,
    isLoading: Boolean,
    // Optional override: if provided the bottom sheet will clamp its max height to this value
    maxSheetHeightOverride: Dp? = null
) {
    // Helper to format nutrient values to max one decimal place
    fun formatNutrient(value: Double?): String {
        return if (value == null) "-"
        else if (value % 1.0 == 0.0) value.toInt().toString()
        else "%.1f".format(value)
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val scope = rememberCoroutineScope()
        val density = LocalDensity.current
        val tokens = tokens
        val fullHeight = maxHeight

        val focusManager = LocalFocusManager.current
        val keyboardController = LocalSoftwareKeyboardController.current

        val minSheetHeight = tokens.sDp(88.dp)
        // Use the override when provided, otherwise fallback to previous behavior
        val maxSheetHeight = maxSheetHeightOverride ?: (fullHeight * 0.925f)
        val sheetInitialHeight = tokens.sDp(200.dp)

        var sheetHeight by remember { mutableStateOf(sheetInitialHeight) }

        // Custom draggable bottom sheet styling (copied from MealDetailPageLoading)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(fullHeight)
                .offset { IntOffset(x = 0, y = (fullHeight - sheetHeight).roundToPx()) }
                .clip(RoundedCornerShape(topStart = tokens.sDp(40.dp), topEnd = tokens.sDp(40.dp)))
                .background(Color.White)
        ) {
            // Drag handle and gesture detector (light gray, matching loading page)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(tokens.sDp(40.dp))
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            val newHeight = sheetHeight - with(density) { dragAmount.y.toDp() }
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

            // Content directly below handle bar
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f) // 👈 ensures consistent height behavior
                    .padding(horizontal = tokens.innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (isFixingMeal) {
                    BasicText(
                        "Meal Correction",
                        style = TextStyle(
                            fontSize = tokens.headerFont,
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    )
                    Spacer(modifier = Modifier.height(tokens.sDp(24.dp)))

                    BasicTextField(
                        value = correctionMessage,
                        onValueChange = onCorrectionMessageChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(tokens.sDp(150.dp))
                            .background(
                                color = Color.LightGray.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(tokens.sDp(16.dp))
                            )
                            .padding(tokens.sDp(16.dp)),
                        textStyle = TextStyle(
                            color = Color.Black,
                            fontSize = tokens.bodyFont
                        ),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                keyboardController?.hide()
                                focusManager.clearFocus()
                            }
                        ),
                        decorationBox = { innerTextField ->
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.TopStart
                            ) {
                                if (correctionMessage.isEmpty()) {
                                    Text(
                                        text = "e.g., 'add 10g of olive oil', 'the portion is actually half of what you see', etc.",
                                        color = Color.Gray.copy(alpha = 0.6f),
                                        fontSize = tokens.bodyFont,
                                    )
                                }
                                innerTextField()
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(tokens.sDp(16.dp)))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = tokens.sDp(8.dp)),
                        horizontalArrangement = Arrangement.spacedBy(tokens.sDp(32.dp))
                    ) {
                        // Dismiss button
                        Box(
                            modifier = Modifier
                                .height(tokens.sDp(52.dp))
                                .width(tokens.sDp(52.dp))
                                .clip(RoundedCornerShape(tokens.sDp(26.dp)))
                                .background(Color(0xFFF5F5F5))
                                .clickable { onCancelFix() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Lucide.ChevronLeft,
                                contentDescription = "Dismiss Correction",
                                tint = Color.Black,
                                modifier = Modifier.size(tokens.sDp(24.dp))
                            )
                        }

                        // Submit button
                        Button(
                            onClick = {
                                onSubmitCorrection()
                                keyboardController?.hide()
                                focusManager.clearFocus()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(tokens.sDp(52.dp)),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                            shape = RoundedCornerShape(tokens.sDp(26.dp))
                        ) {
                            Text("Submit", color = Color.White, fontSize = tokens.bodyFont)
                        }
                    }
                } else {
                    BasicText(
                        mealName,
                        autoSize = TextAutoSize.StepBased(
                            maxFontSize = tokens.headerFont,
                        ),
                        maxLines = 1,
                        style = TextStyle(
                            //fontSize = tokens.headerFont,
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    )

                    Spacer(modifier = Modifier.height(tokens.sDp(24.dp)))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(tokens.sDp(8.dp)),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        NutrientCard(Lucide.Flame, formatNutrient(nutrition?.energy_kcal?.times(quantity)), "Calories", tokens, Modifier.weight(1f))
                        NutrientCard(Lucide.Beef, formatNutrient(nutrition?.protein_g?.times(quantity)), "Protein", tokens, Modifier.weight(1f))
                        NutrientCard(Lucide.Wheat, formatNutrient(nutrition?.carbohydrates_g?.times(quantity)), "Carbs", tokens, Modifier.weight(1f))
                        NutrientCard(Lucide.Droplets, formatNutrient(nutrition?.fat_g?.times(quantity)), "Fat", tokens, Modifier.weight(1f))
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HealthScore(
                        healthGrade = healthGrade,
                        tokens = tokens
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    ShareAndQuantity(
                        initialQuantity = quantity,
                        onQuantityChange = {
                            Log.d("CustomBottomSheet", "onQuantityChange called with: $it")
                            onQuantityChange(it)
                        },
                        onFixClick = onFixClick,
                        tokens = tokens
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    CompositionLocalProvider(
                        LocalOverscrollFactory provides null
                    ) {
                        LazyColumn {
                            items(ingredients.size) { index ->
                                val ingredient = ingredients[index]
                                IngredientCard(
                                    ingredientName = ingredient.name,
                                    quantity = "${formatNutrient(ingredient.quantity * quantity)} ${ingredient.unit}",
                                    calories = ingredient.calories * quantity,
                                    tokens = tokens,
                                )
                                Spacer(modifier = Modifier.height(tokens.sDp(8.dp)))
                            }
                            item {
                                Spacer(modifier = Modifier.height(tokens.sDp(48.dp))) // Add bottom margin so last card is fully scrollable
                            }
                        }
                    }
                }
            }
        }
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.White)
            }
        }
    }
}
