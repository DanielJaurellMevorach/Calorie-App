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
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.composables.icons.lucide.Beef
import com.composables.icons.lucide.ChevronLeft
import com.composables.icons.lucide.Droplets
import com.composables.icons.lucide.Flame
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Trash2
import com.composables.icons.lucide.Wheat

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun MealDetailPageLoading(
    mealImageUri: Uri,
    onBackClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val density = LocalDensity.current

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

            // Content below the drag handle - ALL shimmer effects
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                // Shimmer meal name
                ShimmerMealName()

                Spacer(modifier = Modifier.height(24.dp))

                // Shimmer nutrient cards
                Row (
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ShimmerNutrientCard(
                        icon = Lucide.Flame,
                        modifier = Modifier.weight(1f)
                    )
                    ShimmerNutrientCard(
                        icon = Lucide.Wheat,
                        modifier = Modifier.weight(1f)
                    )
                    ShimmerNutrientCard(
                        icon = Lucide.Beef,
                        modifier = Modifier.weight(1f)
                    )
                    ShimmerNutrientCard(
                        icon = Lucide.Droplets,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Shimmer health score
                ShimmerHealthScore()

                Spacer(modifier = Modifier.height(28.dp))

                // Shimmer share and quantity
                ShimmerShareAndQuantity()

                Spacer(modifier = Modifier.height(28.dp))

                // Shimmer ingredients section
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(1) { // Show 3 shimmer ingredient cards
                        ShimmerIngredientCard()
                    }
                }
            }
        }
    }
}
