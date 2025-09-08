package com.example.responsiveness.ui.screens.mealdetails.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import com.example.responsiveness.ui.theme.DesignTokens

@Composable
fun NutrientCard(
    icon: ImageVector,
    value: String,
    unitName: String,
    tokens: DesignTokens.Tokens,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(
                color = Color.Black,
                shape = RoundedCornerShape(tokens.corner)
            )
            .height(tokens.cardHeight),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround
    ) {
        Icon(
            imageVector = icon,
            contentDescription = unitName,
            tint = Color.White,
            modifier = Modifier.size(tokens.iconSize)
        )
        BasicText(
            //text = value, if value is not for calories, display unit 'value_x g'
            text = if (unitName != "Calories") "${value}g" else value,
            style = TextStyle(
                color = Color.White,
                fontSize = tokens.nutrientTextSize,
                fontWeight = FontWeight.Bold,
                lineHeight = tokens.nutrientTextSize,
                lineHeightStyle = LineHeightStyle(
                    alignment = LineHeightStyle.Alignment.Proportional,
                    trim = LineHeightStyle.Trim.None
                )
            )
        )
        BasicText(
            text = unitName,
            style = TextStyle(
                color = Color.White,
                fontSize = tokens.nutrientTextSize,
                fontWeight = FontWeight.Medium,
                lineHeight = tokens.nutrientTextSize,
                lineHeightStyle = LineHeightStyle(
                    alignment = LineHeightStyle.Alignment.Proportional,
                    trim = LineHeightStyle.Trim.None
                )
            )
        )
    }
}
