package com.example.anothercalorieapp.ui.components.home

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.responsiveness.ui.theme.DesignTokens
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun MealCard(
    modifier: Modifier = Modifier,
    mealName: String,
    calories: Int,
    carbs: Int,
    proteins: Int,
    fat: Int,
    time: String,
    imageRes: Int? = null,
    imageUri: String? = null,
    tokens: DesignTokens.Tokens,
    onClick: () -> Unit = {}
) {
    val configuration = LocalConfiguration.current
    val isSmallScreen = configuration.screenWidthDp < 370

    val textScale = if (isSmallScreen) 0.75f else 1f
    val cardHeight = tokens.sDp(72.dp)
    val imageSize = tokens.sDp(58.dp)
    val timePaddingHorizontal = tokens.sDp(6.dp)
    val timePaddingVertical = tokens.sDp(3.dp)

    // Common text style without font padding
    val noFontPaddingStyle = LocalTextStyle.current.copy(
        platformStyle = PlatformTextStyle(includeFontPadding = false)
    )

    // Helper to check and convert time to 24h format if needed
    fun correctTimeFormat(time: String): String {
        // Try to parse as AM/PM format
        val amPmFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val twentyFourFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        return try {
            // If it parses, convert to 24h
            val date = amPmFormat.parse(time)
            if (date != null) twentyFourFormat.format(date) else time
        } catch (e: ParseException) {
            // If parsing fails, assume it's already 24h or invalid, return as is
            time
        }
    }

    Box(
        modifier = modifier
            .padding(horizontal = tokens.sDp(12.dp))
            .fillMaxWidth()
            .height(cardHeight)
            .background(Color.White, RoundedCornerShape(tokens.sDp(12.dp)))
            .clickable { onClick() }
            .padding(horizontal = tokens.sDp(8.dp), vertical = tokens.sDp(4.dp))
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Image - use AsyncImage for URIs, Image for drawable resources
            if (!imageUri.isNullOrEmpty()) {
                AsyncImage(
                    model = imageUri,
                    contentDescription = "Meal Image",
                    modifier = Modifier
                        .size(imageSize)
                        .clip(RoundedCornerShape(tokens.sDp(8.dp))),
                    contentScale = ContentScale.Crop
                )
            } else if (imageRes != null) {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = "Meal Image",
                    modifier = Modifier
                        .size(imageSize)
                        .clip(RoundedCornerShape(tokens.sDp(8.dp))),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.width(tokens.sDp(8.dp)))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(tokens.sDp(4.dp))
            ) {
                // Top row: name + time
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = mealName,
                        fontSize = tokens.bodyFont,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black,
                        style = noFontPaddingStyle.copy(
                            lineHeight = tokens.bodyFont
                        )
                    )
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFF0F0F0), RoundedCornerShape(tokens.sDp(4.dp)))
                            .padding(horizontal = timePaddingHorizontal, vertical = timePaddingVertical)
                    ) {
                        Text(
                            text = correctTimeFormat(time),
                            fontSize = tokens.nutrientTextSize,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Black,
                            style = noFontPaddingStyle.copy(
                                lineHeight = tokens.nutrientTextSize
                            )
                        )
                    }
                }

                // Calories
                Text(
                    text = "$calories Calories",
                    fontSize = tokens.nutrientTextSize,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xF5000000),
                    style = noFontPaddingStyle.copy(
                        lineHeight = tokens.nutrientTextSize
                    )
                )

                // Macros row
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "$carbs Carbs",
                        fontSize = tokens.nutrientTextSize,
                        color = Color(0xFF888888),
                        style = noFontPaddingStyle.copy(
                            lineHeight = tokens.nutrientTextSize
                        )
                    )
                    Text(" • ", fontSize = tokens.nutrientTextSize, color = Color(0xFF888888), style = noFontPaddingStyle)
                    Text(
                        text = "$proteins Proteins",
                        fontSize = tokens.nutrientTextSize,
                        color = Color(0xFF888888),
                        style = noFontPaddingStyle.copy(
                            lineHeight = tokens.nutrientTextSize
                        )
                    )
                    Text(" • ", fontSize = tokens.nutrientTextSize, color = Color(0xFF888888), style = noFontPaddingStyle)
                    Text(
                        text = "$fat Fat",
                        fontSize = tokens.nutrientTextSize,
                        color = Color(0xFF888888),
                        style = noFontPaddingStyle.copy(
                            lineHeight = tokens.nutrientTextSize
                        )
                    )
                }
            }
        }
    }
}