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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Wheat
import com.example.anothercalorieapp.ui.utils.getResponsiveCornerRadius
import com.example.anothercalorieapp.ui.utils.getResponsiveFontSize
import com.example.anothercalorieapp.ui.utils.getResponsiveIconSize
import com.example.anothercalorieapp.ui.utils.getResponsivePadding
import com.example.anothercalorieapp.ui.utils.getResponsiveSize

@Composable
fun IngredientCard(
    modifier: Modifier = Modifier,
    ingredientName: String,
    calories: Int,
    quantity: String,
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
            // Black background image with white icon
            Box(
                modifier = Modifier
                    .size(getResponsiveSize(52.dp))
                    .background(Color.Black, RoundedCornerShape(getResponsiveCornerRadius(8.dp))),
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

            // Text content
            Column(
                modifier = Modifier
                    .height(getResponsiveSize(72.dp))
                    .fillMaxWidth()
                    .padding(vertical = getResponsivePadding(10.dp)),
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
                        fontSize = getResponsiveFontSize(16.sp, minScale = 0.8f, maxScale = 1.0f),
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black
                    )
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFF0F0F0), shape = RoundedCornerShape(getResponsiveCornerRadius(4.dp)))
                    ) {
                        Text(
                            text = quantity,
                            fontSize = getResponsiveFontSize(14.sp, minScale = 0.8f, maxScale = 1.0f),
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Black,
                            modifier = Modifier.padding(horizontal = getResponsivePadding(6.dp), vertical = getResponsivePadding(2.dp))
                        )
                    }
                }

                // Just calories
                Text(
                    text = "$calories Calories",
                    fontSize = getResponsiveFontSize(12.sp, minScale = 0.8f, maxScale = 1.0f),
                    fontWeight = FontWeight.Bold,
                    color = Color(0xF5000000)
                )
            }
        }
    }
}
