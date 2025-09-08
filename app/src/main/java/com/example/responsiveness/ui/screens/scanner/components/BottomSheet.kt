package com.example.responsiveness.ui.screens.scanner.components

import android.annotation.SuppressLint
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.waitForUpOrCancellation
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.Camera
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.SwitchCamera
import com.composables.icons.lucide.Zap
import com.composables.icons.lucide.ZapOff
import com.example.responsiveness.ui.theme.DesignTokens
import kotlinx.coroutines.launch

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun CustomBottomSheet(
    onDismiss: () -> Unit,
    tokens: DesignTokens.Tokens,
    isFlashOn: Boolean,
    onSwitchCamera: () -> Unit,
    onToggleFlash: () -> Unit,
    onCapture: () -> Unit,
    instructions: List<String>
) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val scope = rememberCoroutineScope()
        val density = LocalDensity.current
        val initialHeightPx = with(density) { tokens.cameraSheetInitialHeight.toPx() }
        //val minHeightPx = with(density) { tokens.cameraSheetMinHeight.toPx() }
        val minHeightPx = initialHeightPx
        val maxHeightPx = with(density) { tokens.cameraSheetMaxHeight.toPx() }

        // Sheet height animatable (not offset)
        val sheetHeight = remember { Animatable(initialHeightPx) }

        LaunchedEffect(Unit) {
            sheetHeight.snapTo(initialHeightPx)
        }

        // Tap-away background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapOrPress { isTap ->
                        if (isTap) onDismiss()
                    }
                }
        )

        // Sheet container
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset { IntOffset(0, 0) } // Always anchored to bottom
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDrag = { change, dragAmount ->
                            change.consume()
                            scope.launch {
                                val newHeight = (sheetHeight.value - dragAmount.y)
                                    .coerceIn(minHeightPx, maxHeightPx)
                                sheetHeight.snapTo(newHeight)
                            }
                        },
                        onDragEnd = { /* keep at current position */ }
                    )
                }
        ) {
            // Sheet background
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(with(density) { sheetHeight.value.toDp() })
            ) {
                Column(
                    modifier = Modifier
                        .matchParentSize()
                        .clip(RoundedCornerShape(topStart = tokens.sDp(40.dp), topEnd = tokens.sDp(40.dp)))
                        .background(Color.White)
                        .padding(tokens.innerPadding),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Handle bar (copied from MealDetailPageLoading)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(tokens.sDp(40.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Spacer(
                            modifier = Modifier
                                .height(tokens.sDp(4.dp))
                                .width(tokens.sDp(40.dp))
                                .background(Color.Gray.copy(alpha = 0.5f), RoundedCornerShape(tokens.sDp(2.dp)))
                        )
                    }

                    // Camera control row
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onSwitchCamera,
                            modifier = Modifier.size(tokens.cameraControlButtonSize)
                        ) {
                            Icon(
                                Lucide.SwitchCamera,
                                contentDescription = "Switch Camera",
                                modifier = Modifier.size(tokens.cameraIconSize),
                                tint = Color.Black
                            )
                        }
                        FloatingActionButton(
                            onClick = onCapture,
                            modifier = Modifier.size(tokens.cameraCaptureButtonSize),
                            shape = CircleShape,
                            containerColor = Color.Black,
                            contentColor = Color.White
                        ) {
                            Icon(
                                Lucide.Camera,
                                contentDescription = "Take Photo",
                                modifier = Modifier.size(tokens.cameraCaptureIconSize),
                                tint = Color.White
                            )
                        }
                        IconButton(
                            onClick = onToggleFlash,
                            modifier = Modifier.size(tokens.cameraControlButtonSize)
                        ) {
                            Icon(
                                if (isFlashOn) Lucide.ZapOff else Lucide.Zap,
                                contentDescription = if (isFlashOn) "Turn Flash Off" else "Turn Flash On",
                                modifier = Modifier.size(tokens.cameraIconSize),
                                tint = Color.Black
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(tokens.sDp(16.dp)))
                    // Instructions title
                    Text(
                        text = "Camera Instructions",
                        style = TextStyle(
                            fontSize = tokens.cameraInstructionsTitleFontSize,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier.fillMaxWidth().padding(bottom = tokens.cameraInstructionsItemSpacing)
                    )
                    // Scrollable instructions
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        verticalArrangement = Arrangement.spacedBy(tokens.cameraInstructionsItemSpacing)
                    ) {
                        items(instructions) { instruction ->
                            Text(
                                text = "• $instruction",
                                style = TextStyle(
                                    fontSize = tokens.cameraInstructionsFontSize,
                                    lineHeight = tokens.cameraInstructionsLineHeight,
                                    color = Color.Black.copy(alpha = 0.85f),
                                    textAlign = TextAlign.Start
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}



// Helper: quick tap detection (down -> up without movement).
suspend fun PointerInputScope.detectTapOrPress(onResult: (Boolean) -> Unit) {
    awaitPointerEventScope {
        val event = waitForUpOrCancellation()
        if (event != null && !event.pressed) {
            onResult(true)
        } else {
            onResult(false)
        }
    }
}
