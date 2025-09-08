package com.example.anothercalorieapp.ui.components.mealdetails

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Minus
import com.composables.icons.lucide.Plus
import com.composables.icons.lucide.Sparkles
import com.example.responsiveness.ui.theme.DesignTokens

@Composable
fun ShareAndQuantity(
    modifier: Modifier = Modifier,
    initialQuantity: Int = 1,
    onQuantityChange: (Int) -> Unit = {},
    onFixClick: () -> Unit = {},
    tokens: DesignTokens.Tokens // Add tokens for responsiveness
) {
    val quantity = initialQuantity

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Share button
//        IconButton(
//            onClick = onShareClick,
//            modifier = Modifier
//                .size(tokens.sDp(52.dp))
//                .background(
//                    Color(0xFFF5F5F5),
//                    shape = RoundedCornerShape(tokens.sDp(26.dp))
//                )
//        ) {
//            Icon(
//                imageVector = Lucide.Share,
//                contentDescription = "Share Meal",
//                tint = Color.Black,
//                modifier = Modifier.size(tokens.sDp(22.dp))
//            )
//        }

        // Fix button
        IconButton(
            onClick = onFixClick,
            modifier = Modifier
                .size(tokens.sDp(52.dp))
                .background(
                    Color(0xFFF5F5F5),
                    shape = RoundedCornerShape(tokens.sDp(26.dp))
                )
        ) {
            Icon(
                imageVector = Lucide.Sparkles,
                contentDescription = "Prompt meal correction",
                tint = Color.Black,
                modifier = Modifier.size(tokens.sDp(22.dp))
            )
        }

        // Quantity control
        Row(
            modifier = Modifier
                .background(
                    Color(0xFFF5F5F5),
                    shape = RoundedCornerShape(tokens.sDp(28.dp))
                )
                .padding(vertical = tokens.sDp(16.dp), horizontal = tokens.sDp(24.dp))
                .fillMaxWidth(0.4f)
            ,
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Decrease button
            IconButton(
                onClick = {
                    if (quantity > 1) {
                        Log.d("ShareAndQuantity", "Decrease clicked. New quantity: ${quantity - 1}")
                        onQuantityChange(quantity - 1)
                    }
                },
                modifier = Modifier
                    .size(tokens.sDp(16.dp))
                    .background(Color.Transparent, shape = RoundedCornerShape(tokens.sDp(22.dp))),
                enabled = quantity > 1
            ) {
                Icon(
                    imageVector = Lucide.Minus,
                    contentDescription = "Decrease Quantity",
                    tint = Color.Black,
                    modifier = Modifier.size(tokens.sDp(20.dp))
                )
            }

            // Quantity display
            BasicText(
                text = quantity.toString(),
                modifier = Modifier.padding(horizontal = tokens.sDp(20.dp)),
                style = TextStyle(
                    color = Color.Black,
                    fontSize = tokens.nutrientTextSize,
                    fontWeight = FontWeight.SemiBold
                )
            )

            // Increase button
            IconButton(
                onClick = {
                    if (quantity < 15) {
                        Log.d("ShareAndQuantity", "Increase clicked. New quantity: ${quantity + 1}")
                        onQuantityChange(quantity + 1)
                    }
                },
                modifier = Modifier
                    .size(tokens.sDp(16.dp))
                    .background(Color.Transparent, shape = RoundedCornerShape(tokens.sDp(22.dp))),
                enabled = quantity < 15
            ) {
                Icon(
                    imageVector = Lucide.Plus,
                    contentDescription = "Increase Quantity",
                    tint = Color.Black,
                    modifier = Modifier.size(tokens.sDp(20.dp))
                )
            }
        }
    }
}
