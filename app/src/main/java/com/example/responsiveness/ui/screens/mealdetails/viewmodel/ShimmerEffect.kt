package com.example.anothercalorieapp.ui.components.scanner.viewmodel

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.unit.Dp
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Wheat
import com.example.responsiveness.ui.theme.DesignTokens

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

@Composable
fun ShimmerNutrientCard(
    icon: ImageVector,
    tokens: DesignTokens.Tokens,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(tokens.shimmerCardCorner))
            .background(shimmerBrushBlack())
            .height(tokens.shimmerCardHeight)
            .padding(tokens.shimmerCardPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(tokens.shimmerSpacerHeight)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(tokens.shimmerIconSize)
        )
        Box(
            modifier = Modifier
                .width(tokens.shimmerTextWidthSmall)
                .height(tokens.shimmerTextHeightMedium)
                .clip(RoundedCornerShape(tokens.shimmerCardCorner))
                .background(shimmerBrush())
        )
        Box(
            modifier = Modifier
                .width(tokens.shimmerTextWidthMedium)
                .height(tokens.shimmerTextHeightSmall)
                .clip(RoundedCornerShape(tokens.shimmerCardCorner))
                .background(shimmerBrush())
        )
    }
}

@Composable
fun ShimmerMealName(
    tokens: DesignTokens.Tokens,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.35f)
                .height(tokens.shimmerTextHeightLarge)
                .clip(RoundedCornerShape(tokens.shimmerCardCorner))
                .background(shimmerBrush())
        )
    }
}

@Composable
fun ShimmerText(
    width: Dp,
    tokens: DesignTokens.Tokens,
    height: Dp = tokens.shimmerTextHeightSmall,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .width(width)
            .height(height)
            .clip(RoundedCornerShape(tokens.shimmerCardCorner))
            .background(shimmerBrush())
    )
}

@Composable
fun ShimmerHealthScore(
    tokens: DesignTokens.Tokens,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Row() {
            Box(
                modifier = Modifier
                    .width(tokens.shimmerTextWidthLarge)
                    .height(tokens.shimmerTextHeightSmall)
                    .clip(RoundedCornerShape(tokens.shimmerCardCorner))
                    .background(shimmerBrush())
            )
//            Spacer(modifier = Modifier.width(tokens.shimmerSpacerWidth))
//            Box(
//                modifier = Modifier
//                    .width(tokens.shimmerTextWidthMedium)
//                    .height(tokens.shimmerTextHeightSmall)
//                    .clip(RoundedCornerShape(tokens.shimmerCardCorner))
//                    .background(shimmerBrush())
//            )
        }
        Spacer(modifier = Modifier.height(tokens.shimmerSpacerHeight))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                //.padding(horizontal = tokens.shimmerCardPadding)
                .height(tokens.shimmerProgressBarHeight)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(tokens.shimmerTextHeightMedium)
                    .clip(RoundedCornerShape(tokens.shimmerProgressBarCorner))
                    .background(shimmerBrush())
                    .align(Alignment.Center)
            )
        }
    }
}

@Composable
fun ShimmerShareAndQuantity(
    tokens: DesignTokens.Tokens,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(tokens.shimmerShareButtonSize)
                .clip(RoundedCornerShape(tokens.shimmerShareButtonCorner))
                .background(shimmerBrush())
        )
        Box(
            modifier = Modifier
                .height(tokens.shimmerQuantityControlHeight)
                .fillMaxWidth(0.4f)
                .clip(RoundedCornerShape(tokens.shimmerQuantityControlCorner))
                .background(shimmerBrush())
        )
    }
}

@Composable
fun ShimmerIngredientCard(
    tokens: DesignTokens.Tokens,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(tokens.shimmerIngredientCardHeight)
            .background(Color.White, RoundedCornerShape(tokens.shimmerCardCorner))
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(tokens.shimmerShareButtonSize)
                    .clip(RoundedCornerShape(tokens.shimmerIngredientIconCorner))
                    .background(shimmerBrushBlack()),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Lucide.Wheat,
                    contentDescription = "Ingredient Icon",
                    tint = Color.White,
                    modifier = Modifier.size(tokens.shimmerIngredientIconSize)
                )
            }
            Spacer(modifier = Modifier.width(tokens.shimmerSpacerWidth))
            Column(
                modifier = Modifier
                    .height(tokens.shimmerIngredientCardHeight)
                    .fillMaxWidth()
                    .padding(vertical = tokens.shimmerIngredientTextPadding),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .width(tokens.shimmerIngredientTextWidth)
                            .height(tokens.shimmerIngredientTextHeight)
                            .clip(RoundedCornerShape(tokens.shimmerCardCorner))
                            .background(shimmerBrush())
                    )
                    Box(
                        modifier = Modifier
                            .width(tokens.shimmerTextWidthMedium)
                            .height(tokens.shimmerTextHeightMedium)
                            .clip(RoundedCornerShape(tokens.shimmerCardCorner))
                            .background(shimmerBrush())
                    )
                }
                Box(
                    modifier = Modifier
                        .width(tokens.shimmerTextWidthLarge)
                        .height(tokens.shimmerTextHeightSmall)
                        .clip(RoundedCornerShape(tokens.shimmerCardCorner))
                        .background(shimmerBrush())
                )
            }
        }
    }
}
