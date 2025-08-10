package com.example.anothercalorieapp.ui.components.mealdetails

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.anothercalorieapp.ui.utils.getResponsiveCardHeight
import com.example.anothercalorieapp.ui.utils.getResponsiveCornerRadius
import com.example.anothercalorieapp.ui.utils.getResponsiveFontSize
import com.example.anothercalorieapp.ui.utils.getResponsiveIconSize
import com.example.anothercalorieapp.ui.utils.getResponsivePadding
import com.example.anothercalorieapp.ui.utils.getResponsiveSpacing

@Composable
fun NutrientCard(
    icon: ImageVector,
    value: String,
    unitName: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(
                color = Color.Black,
                shape = RoundedCornerShape(getResponsiveCornerRadius())
            )
            .height(getResponsiveCardHeight())
            .padding(getResponsivePadding(8.dp)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(getResponsiveSpacing(8.dp))
    ) {
        // Nutrient icon
        Icon(
            imageVector = icon,
            contentDescription = unitName,
            tint = Color.White,
            modifier = Modifier.size(getResponsiveIconSize())
        )

        // Value (e.g., "150", "2g") - conservative scaling for bottom sheet
        Text(
            text = value,
            color = Color.White,
            fontSize = getResponsiveFontSize(18.sp, minScale = 0.8f, maxScale = 1.0f),
            fontWeight = FontWeight.Bold
        )

        // Unit name (e.g., "Calories", "Protein") - conservative scaling for bottom sheet
        Text(
            text = unitName,
            color = Color.White,
            fontSize = getResponsiveFontSize(12.sp, minScale = 0.8f, maxScale = 1.0f),
            fontWeight = FontWeight.Medium
        )
    }
}