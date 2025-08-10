package com.example.anothercalorieapp.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun getScreenWidth(): Dp {
    val configuration = LocalConfiguration.current
    return configuration.screenWidthDp.dp
}

@Composable
fun getResponsiveSize(
    baseSize: Dp,
    minScreenWidth: Dp = 320.dp, // Galaxy A3 2017 equivalent
    maxScreenWidth: Dp = 600.dp, // Max compact size
    minScale: Float = 0.85f,
    maxScale: Float = 1.15f
): Dp {
    val screenWidth = getScreenWidth()
    val clampedWidth = screenWidth.coerceIn(minScreenWidth, maxScreenWidth)
    val progress = (clampedWidth - minScreenWidth) / (maxScreenWidth - minScreenWidth)
    val scale = minScale + (maxScale - minScale) * progress
    return baseSize * scale
}

@Composable
fun getResponsiveFontSize(
    baseFontSize: TextUnit,
    minScreenWidth: Dp = 320.dp,
    maxScreenWidth: Dp = 600.dp,
    minScale: Float = 0.9f,
    maxScale: Float = 1.1f
): TextUnit {
    val screenWidth = getScreenWidth()
    val clampedWidth = screenWidth.coerceIn(minScreenWidth, maxScreenWidth)
    val progress = (clampedWidth - minScreenWidth) / (maxScreenWidth - minScreenWidth)
    val scale = minScale + (maxScale - minScale) * progress
    return baseFontSize * scale
}

@Composable
fun getResponsivePadding(
    basePadding: Dp,
    minScreenWidth: Dp = 320.dp,
    maxScreenWidth: Dp = 600.dp,
    minScale: Float = 0.8f,
    maxScale: Float = 1.2f
): Dp {
    val screenWidth = getScreenWidth()
    val clampedWidth = screenWidth.coerceIn(minScreenWidth, maxScreenWidth)
    val progress = (clampedWidth - minScreenWidth) / (maxScreenWidth - minScreenWidth)
    val scale = minScale + (maxScale - minScale) * progress
    return basePadding * scale
}

// Specific responsive functions for different component types
@Composable
fun getResponsiveCardHeight(baseHeight: Dp = 100.dp): Dp =
    getResponsiveSize(baseHeight, minScale = 0.9f, maxScale = 1.1f)

@Composable
fun getResponsiveIconSize(baseSize: Dp = 24.dp): Dp =
    getResponsiveSize(baseSize, minScale = 0.85f, maxScale = 1.15f)

@Composable
fun getResponsiveCornerRadius(baseRadius: Dp = 12.dp): Dp =
    getResponsiveSize(baseRadius, minScale = 0.8f, maxScale = 1.2f)

@Composable
fun getResponsiveSpacing(baseSpacing: Dp = 16.dp): Dp =
    getResponsivePadding(baseSpacing, minScale = 0.7f, maxScale = 1.3f)
