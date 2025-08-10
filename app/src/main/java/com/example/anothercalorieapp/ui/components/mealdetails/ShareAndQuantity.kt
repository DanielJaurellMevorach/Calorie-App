package com.example.anothercalorieapp.ui.components.mealdetails

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Minus
import com.composables.icons.lucide.Plus
import com.composables.icons.lucide.Share
import com.example.anothercalorieapp.ui.utils.getResponsiveCornerRadius
import com.example.anothercalorieapp.ui.utils.getResponsiveFontSize
import com.example.anothercalorieapp.ui.utils.getResponsiveIconSize
import com.example.anothercalorieapp.ui.utils.getResponsivePadding
import com.example.anothercalorieapp.ui.utils.getResponsiveSize

@Composable
fun ShareAndQuantity(
    modifier: Modifier = Modifier,
    initialQuantity: Int = 1,
    onQuantityChange: (Int) -> Unit = {},
    onShareClick: () -> Unit = {}
) {
    var quantity by remember { mutableIntStateOf(initialQuantity) }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Share button
        IconButton(
            onClick = onShareClick,
            modifier = Modifier
                .size(getResponsiveSize(52.dp))
                .background(
                    Color(0xFFF5F5F5),
                    shape = RoundedCornerShape(getResponsiveCornerRadius(26.dp))
                )
        ) {
            Icon(
                imageVector = Lucide.Share,
                contentDescription = "Share Meal",
                tint = Color.Black,
                modifier = Modifier.size(getResponsiveIconSize(22.dp))
            )
        }

        // Quantity control
        Row(
            modifier = Modifier
                .background(
                    Color(0xFFF5F5F5),
                    shape = RoundedCornerShape(getResponsiveCornerRadius(28.dp))
                )
                .padding(
                    vertical = getResponsivePadding(16.dp),
                    horizontal = getResponsivePadding(24.dp)
                )
                .fillMaxWidth(0.4f),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Decrease button
            IconButton(
                onClick = {
                    if (quantity > 1) {
                        quantity--
                        onQuantityChange(quantity)
                    }
                },
                modifier = Modifier
                    .size(getResponsiveSize(16.dp))
                    .background(Color.White, shape = RoundedCornerShape(getResponsiveCornerRadius(22.dp))),
                enabled = quantity > 1
            ) {
                Icon(
                    imageVector = Lucide.Minus,
                    contentDescription = "Decrease Quantity",
                    tint = Color.Black,
                    modifier = Modifier.size(getResponsiveIconSize(20.dp))
                )
            }

            // Quantity display
            Text(
                text = quantity.toString(),
                modifier = Modifier.padding(horizontal = getResponsivePadding(20.dp)),
                color = Color.Black,
                fontSize = getResponsiveFontSize(18.sp, minScale = 0.8f, maxScale = 1.0f),
                fontWeight = FontWeight.SemiBold
            )

            // Increase button
            IconButton(
                onClick = {
                    quantity++
                    onQuantityChange(quantity)
                },
                modifier = Modifier
                    .size(getResponsiveSize(16.dp))
                    .background(Color.Black, shape = RoundedCornerShape(getResponsiveCornerRadius(22.dp)))
            ) {
                Icon(
                    imageVector = Lucide.Plus,
                    contentDescription = "Increase Quantity",
                    tint = Color.White,
                    modifier = Modifier.size(getResponsiveIconSize(20.dp))
                )
            }
        }
    }
}
