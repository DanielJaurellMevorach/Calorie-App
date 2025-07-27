package com.example.caloriecounter.ui.components.camera

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun CameraOverlayTEMP(
    modifier: Modifier = Modifier,
    cornerColor: Color = Color.White,
    cornerLength: Dp = 32.dp,
    strokeWidth: Dp = 3.dp
) {
    Box(modifier = modifier.fillMaxSize()) {
        val cornerModifier = Modifier
            .width(cornerLength)
            .height(strokeWidth)
            .background(cornerColor)

        val verticalCornerModifier = Modifier
            .width(strokeWidth)
            .height(cornerLength)
            .background(cornerColor)

        val cornerPadding = 32.dp
        val radius = 24.dp

        // Top-left
        Box(
            Modifier
                .offset(cornerPadding, cornerPadding)
        ) {
            Box(modifier = cornerModifier)
            Box(modifier = verticalCornerModifier.align(Alignment.TopStart))
        }

        // Top-right
        Box(
            Modifier
                .offset(x = -(cornerPadding), y = cornerPadding)
                .align(Alignment.TopEnd)
        ) {
            Box(modifier = cornerModifier.align(Alignment.TopEnd))
            Box(modifier = verticalCornerModifier.align(Alignment.TopEnd))
        }

        // Bottom-left
        Box(
            Modifier
                .offset(x = cornerPadding, y = -cornerPadding)
                .align(Alignment.BottomStart)
        ) {
            Box(modifier = cornerModifier.align(Alignment.BottomStart))
            Box(modifier = verticalCornerModifier.align(Alignment.BottomStart))
        }

        // Bottom-right
        Box(
            Modifier
                .offset(x = -cornerPadding, y = -cornerPadding)
                .align(Alignment.BottomEnd)
        ) {
            Box(modifier = cornerModifier.align(Alignment.BottomEnd))
            Box(modifier = verticalCornerModifier.align(Alignment.BottomEnd))
        }
    }
}
