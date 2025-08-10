package com.example.anothercalorieapp.ui.components.scanner.viewmodel

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Wheat
import com.example.anothercalorieapp.ui.utils.getResponsiveCardHeight
import com.example.anothercalorieapp.ui.utils.getResponsiveCornerRadius
import com.example.anothercalorieapp.ui.utils.getResponsiveFontSize
import com.example.anothercalorieapp.ui.utils.getResponsiveIconSize
import com.example.anothercalorieapp.ui.utils.getResponsivePadding
import com.example.anothercalorieapp.ui.utils.getResponsiveSize
import com.example.anothercalorieapp.ui.utils.getResponsiveSpacing

@Composable
fun shimmerBrush(showShimmer: Boolean = true, targetValue: Float = 1000f): Brush {
    return if (showShimmer) {
        val shimmerColors = listOf(
            Color.LightGray.copy(alpha = 0.6f),
            Color.LightGray.copy(alpha = 0.2f),
            Color.LightGray.copy(alpha = 0.6f),
        )

        val transition = rememberInfiniteTransition(label = "")
        val translateAnimation = transition.animateFloat(
            initialValue = 0f,
            targetValue = targetValue,
            animationSpec = infiniteRepeatable(
                animation = tween(800), repeatMode = RepeatMode.Restart
            ), label = ""
        )
        Brush.linearGradient(
            colors = shimmerColors,
            start = Offset.Zero,
            end = Offset(x = translateAnimation.value, y = translateAnimation.value)
        )
    } else {
        Brush.linearGradient(
            colors = listOf(Color.Transparent, Color.Transparent),
            start = Offset.Zero,
            end = Offset.Zero
        )
    }
}

@Composable
fun shimmerBrushBlack(showShimmer: Boolean = true, targetValue: Float = 1000f): Brush {
    return if (showShimmer) {
        val shimmerColors = listOf(
            Color.Black.copy(alpha = 0.8f),
            Color.Gray.copy(alpha = 0.4f),
            Color.Black.copy(alpha = 0.8f),
        )

        val transition = rememberInfiniteTransition(label = "")
        val translateAnimation = transition.animateFloat(
            initialValue = 0f,
            targetValue = targetValue,
            animationSpec = infiniteRepeatable(
                animation = tween(800), repeatMode = RepeatMode.Restart
            ), label = ""
        )
        Brush.linearGradient(
            colors = shimmerColors,
            start = Offset.Zero,
            end = Offset(x = translateAnimation.value, y = translateAnimation.value)
        )
    } else {
        Brush.linearGradient(
            colors = listOf(Color.Transparent, Color.Transparent),
            start = Offset.Zero,
            end = Offset.Zero
        )
    }
}

// PERFECTLY MATCHING SHIMMER COMPONENTS

@Composable
fun ShimmerNutrientCard(
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(getResponsiveCornerRadius()))
            .background(shimmerBrushBlack())
            .height(getResponsiveCardHeight())
            .padding(getResponsivePadding(8.dp)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(getResponsiveSpacing(8.dp))
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(getResponsiveIconSize())
        )

        // Shimmer for value text - matches Text(fontSize = getResponsiveFontSize(18.sp))
        Box(
            modifier = Modifier
                .width(getResponsiveSize(40.dp))
                .height(getResponsiveFontSize(18.sp, minScale = 0.8f, maxScale = 1.0f).value.dp * 1.2f)
                .clip(RoundedCornerShape(getResponsiveCornerRadius(4.dp)))
                .background(shimmerBrush())
        )

        // Shimmer for unit name text - matches Text(fontSize = getResponsiveFontSize(12.sp))
        Box(
            modifier = Modifier
                .width(getResponsiveSize(50.dp))
                .height(getResponsiveFontSize(12.sp, minScale = 0.8f, maxScale = 1.0f).value.dp * 1.2f)
                .clip(RoundedCornerShape(getResponsiveCornerRadius(4.dp)))
                .background(shimmerBrush())
        )
    }
}

@Composable
fun ShimmerMealName(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(getResponsiveFontSize(20.sp, minScale = 0.8f, maxScale = 1.0f).value.dp * 1.3f)
                .clip(RoundedCornerShape(getResponsiveCornerRadius(4.dp)))
                .background(shimmerBrush())
        )
    }
}

@Composable
fun ShimmerHealthScore(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        // Title row shimmer - matches "Health Score: Grade X" layout exactly
        Row {
            Box(
                modifier = Modifier
                    .width(getResponsiveSize(100.dp))
                    .height(getResponsiveFontSize(14.sp, minScale = 0.8f, maxScale = 1.0f).value.dp * 1.2f)
                    .clip(RoundedCornerShape(getResponsiveCornerRadius(4.dp)))
                    .background(shimmerBrush())
            )

            Spacer(modifier = Modifier.width(getResponsivePadding(8.dp)))

            Box(
                modifier = Modifier
                    .width(getResponsiveSize(60.dp))
                    .height(getResponsiveFontSize(14.sp, minScale = 0.8f, maxScale = 1.0f).value.dp * 1.2f)
                    .clip(RoundedCornerShape(getResponsiveCornerRadius(4.dp)))
                    .background(shimmerBrush())
            )
        }

        Spacer(modifier = Modifier.height(getResponsivePadding(8.dp)))

        // Progress bar shimmer - matches exact BoxWithConstraints structure
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = getResponsivePadding(8.dp))
                .height(getResponsiveSize(20.dp))
        ) {
            val strokeWidth = getResponsiveSize(12.dp)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(strokeWidth)
                    .clip(RoundedCornerShape(strokeWidth / 2))
                    .background(shimmerBrush())
                    .align(Alignment.Center)
            )
        }
    }
}

@Composable
fun ShimmerShareAndQuantity(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Share button shimmer - matches exact size and shape
        Box(
            modifier = Modifier
                .size(getResponsiveSize(52.dp))
                .clip(RoundedCornerShape(getResponsiveCornerRadius(26.dp)))
                .background(shimmerBrush())
        )

        // Quantity controls shimmer - matches the real component structure exactly
        Box(
            modifier = Modifier
                .fillMaxWidth(0.4f)
                .height(getResponsiveSize(52.dp))
                .clip(RoundedCornerShape(getResponsiveCornerRadius(28.dp)))
                .background(shimmerBrush())
        )
    }
}

@Composable
fun ShimmerIngredientCard(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(getResponsiveSize(72.dp))
            .background(Color.White, RoundedCornerShape(getResponsiveCornerRadius()))
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon background shimmer - matches exact size and corner radius
            Box(
                modifier = Modifier
                    .size(getResponsiveSize(52.dp))
                    .clip(RoundedCornerShape(getResponsiveCornerRadius(8.dp)))
                    .background(shimmerBrushBlack()),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Lucide.Wheat,
                    contentDescription = "Ingredient Icon",
                    tint = Color.White,
                    modifier = Modifier.size(getResponsiveIconSize())
                )
            }

            Spacer(modifier = Modifier.width(getResponsivePadding(12.dp)))

            // Text content shimmer - matches the real layout structure exactly
            Column(
                modifier = Modifier
                    .height(getResponsiveSize(72.dp))
                    .fillMaxWidth()
                    .padding(vertical = getResponsivePadding(10.dp)),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top row: ingredient name + quantity
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Ingredient name shimmer - matches Text(fontSize = getResponsiveFontSize(16.sp))
                    Box(
                        modifier = Modifier
                            .width(getResponsiveSize(120.dp))
                            .height(getResponsiveFontSize(16.sp, minScale = 0.8f, maxScale = 1.0f).value.dp * 1.2f)
                            .clip(RoundedCornerShape(getResponsiveCornerRadius(4.dp)))
                            .background(shimmerBrush())
                    )

                    // Quantity shimmer - matches Text(fontSize = getResponsiveFontSize(14.sp))
                    Box(
                        modifier = Modifier
                            .width(getResponsiveSize(60.dp))
                            .height(getResponsiveFontSize(14.sp, minScale = 0.8f, maxScale = 1.0f).value.dp * 1.2f)
                            .clip(RoundedCornerShape(getResponsiveCornerRadius(4.dp)))
                            .background(shimmerBrush())
                    )
                }

                // Calories shimmer (bottom) - matches Text(fontSize = getResponsiveFontSize(12.sp))
                Box(
                    modifier = Modifier
                        .width(getResponsiveSize(80.dp))
                        .height(getResponsiveFontSize(12.sp, minScale = 0.8f, maxScale = 1.0f).value.dp * 1.2f)
                        .clip(RoundedCornerShape(getResponsiveCornerRadius(4.dp)))
                        .background(shimmerBrush())
                )
            }
        }
    }
}
