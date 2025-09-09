package com.example.anothercalorieapp.ui.components.mealdetails

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow.Companion.Ellipsis
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.ShoppingBasket
import com.composables.icons.lucide.Wheat
import com.example.responsiveness.ui.theme.DesignTokens

@Composable
fun IngredientCard(
    modifier: Modifier = Modifier,
    ingredientName: String,
    quantity: String,
    calories: Double,
    tokens: DesignTokens.Tokens // Add tokens for responsiveness
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(tokens.sDp(72.dp))
            .background(Color.White, RoundedCornerShape(tokens.corner))
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Black background image with white icon
            Box(
                modifier = Modifier
                    .size(tokens.sDp(52.dp))
                    .background(Color.Black, RoundedCornerShape(tokens.sDp(8.dp))),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Lucide.ShoppingBasket,
                    contentDescription = "Ingredient Icon",
                    tint = Color.White,
                    modifier = Modifier.size(tokens.sDp(24.dp))
                )
            }

            Spacer(modifier = Modifier.width(tokens.sDp(12.dp)))

            // Text content
            Column(
                modifier = Modifier
                    .height(tokens.sDp(72.dp))
                    .fillMaxWidth()
                    .padding(vertical = tokens.sDp(10.dp)), // Increased top padding to push content down together
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Ingredient name + quantity in one row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = ingredientName,
                        fontSize = tokens.bodyFont,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black,
                        maxLines = 1,
                        overflow = Ellipsis,
                        modifier = Modifier
                            .width(tokens.sDp(176.dp)) // Increased width for name
                    )
                    Spacer(modifier = Modifier.width(tokens.sDp(8.dp))) // Ensure margin between name and value
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFF0F0F0), shape = RoundedCornerShape(tokens.sDp(4.dp)))
                    ) {
                        Text(
                            text = quantity,
                            fontSize = tokens.nutrientTextSize,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Black,
                            maxLines = 1,
                            modifier = Modifier.padding(horizontal = tokens.sDp(10.dp), vertical = tokens.sDp(2.dp))
                        )
                    }
                }

                // Just calories (removed the nutrition breakdown)
                Text(
                    text = "${calories.toInt()} Calories",
                    fontSize = tokens.nutrientTextSize, // Use nutrientTextSize (TextUnit)
                    fontWeight = FontWeight.Bold,
                    color = Color(0xF5000000)
                    // Removed offset modifier
                )
            }
        }
    }
}
