package com.example.anothercalorieapp.ui.components.scanner.viewmodel

import android.annotation.SuppressLint
import android.net.Uri
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.composables.icons.lucide.Beef
import com.composables.icons.lucide.ChevronLeft
import com.composables.icons.lucide.Droplets
import com.composables.icons.lucide.Flame
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Wheat
import com.example.responsiveness.ui.theme.DesignTokens

/**
 * Shimmer loading UI used specifically during a meal correction request.
 * Unlike MealDetailPageLoading, this does NOT trigger any new analysis or navigation.
 */
@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun MealCorrectionLoading(
    mealImageUri: Uri?,
    onBackClick: () -> Unit,
) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val tokens = DesignTokens.provideTokens(maxWidth, maxHeight)
        val density = LocalDensity.current
        val fullHeight = maxHeight
        val minSheetHeight = tokens.sDp(88.dp)
        val maxSheetHeight = fullHeight * 0.70f
        val sheetInitialHeight = tokens.sDp(200.dp)
        val sheetHeightState = remember { mutableStateOf(sheetInitialHeight) }

        // Background image
        mealImageUri?.let {
            AsyncImage(
                model = it,
                contentDescription = "Meal Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        // Top bar
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

        // Bottom shimmer sheet (copied structurally from MealDetailPageLoading but without analysis logic)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(fullHeight)
                .offset { IntOffset(x = 0, y = (fullHeight - sheetHeightState.value).roundToPx()) }
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
                            val newHeight = sheetHeightState.value - with(density) { dragAmount.toDp() }
                            sheetHeightState.value = newHeight.coerceIn(minSheetHeight, maxSheetHeight)
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
                Row(
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
                    items(3) { // show a few shimmer ingredient placeholders
                        ShimmerIngredientCard(tokens = tokens)
                    }
                }
            }
        }
    }
}

