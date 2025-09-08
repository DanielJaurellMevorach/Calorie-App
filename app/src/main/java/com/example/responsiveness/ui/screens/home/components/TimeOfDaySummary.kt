package com.example.responsiveness.ui.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Plus
import com.example.anothercalorieapp.database.MealWithDetails
import com.example.responsiveness.DatabaseMealDetailRoute
import com.example.responsiveness.ui.theme.DesignTokens
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil.CoilImage

@Composable
fun TimeOfDaySummary(
    mealName: String,
    calories: String,
    icon: ImageVector,
    tokens: DesignTokens.Tokens,
    mealImageUris: List<String?>,
    mealDetails: List<MealWithDetails?>,
    navController: NavController,
    modifier: Modifier = Modifier,
    onAddMealClick: (() -> Unit)? = null
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(tokens.sDp(20.dp)))
            .padding(horizontal = tokens.sDp(16.dp), vertical = tokens.sDp(8.dp))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = mealName,
                    color = Color.Black,
                    fontSize = tokens.calendarTextSize.times(1.2),
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.height(tokens.sDp(8.dp)))
                Row(
                    modifier = Modifier.padding(tokens.sDp(2.dp)),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(tokens.sDp(28.dp))
                            .background(Color.Black, RoundedCornerShape(tokens.sDp(8.dp))),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = "Calories",
                            tint = Color.White,
                            modifier = Modifier.size(tokens.sDp(20.dp))
                        )
                    }
                    Spacer(modifier = Modifier.width(tokens.sDp(8.dp)))
                    Text(
                        text = "${calories.toFloatOrNull()?.toInt() ?: 0} kcal",
                        color = Color.Black,
                        fontSize = tokens.calendarTextSize.times(1.2),
                        fontWeight = FontWeight.SemiBold,
                    )
                    Spacer(modifier = Modifier.width(tokens.sDp(8.dp)))
                }
            }

            val avatar = tokens.sDp(48.dp)
            val overlap = tokens.sDp(16.dp)

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(-overlap)
            ) {
                // Pair URIs with meal details (require meal entity), select newest 3
                val paired = mealImageUris.mapIndexedNotNull { index, uri ->
                    val detail = mealDetails.getOrNull(index)
                    if (uri != null && detail?.meal != null) Triple(uri, detail, detail.meal.created_at) else null
                }

                val selected = paired
                    .sortedByDescending { it.third } // newest first
                    .take(3)
                    .sortedBy { it.third } // oldest->newest for left->right

                val images = selected.map { it.first }
                val details = selected.map { it.second }

                val dummies = List(3 - images.size) { null }
                val previewList = dummies + images
                val previewDetails = dummies + details

                previewList.forEachIndexed { idx, uri ->
                    val mealDetail = previewDetails[idx]
                    if (uri != null && mealDetail != null) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .clip(CircleShape)
                                .background(Color.White, CircleShape)
                                .size(avatar)
                                .clickable(
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() }
                                ) {
                                    navController.navigate(
                                        DatabaseMealDetailRoute(mealDetail.meal.id, source = "home")
                                    )
                                }
                        ) {
                            CoilImage(
                                imageModel = { uri },
                                modifier = Modifier
                                    .size(avatar - 4.dp)
                                    .clip(CircleShape)
                                    .align(Alignment.Center),
                                imageOptions = ImageOptions(
                                    contentScale = ContentScale.Crop,
                                    alignment = Alignment.Center,
                                    contentDescription = "Meal Image",
                                )
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .size(avatar)
                                .background(Color(0xFFFAFAFA), CircleShape)
                                .border(2.dp, Color.White, CircleShape)
                                .align(Alignment.CenterVertically),
                        )
                    }
                }

                // plus button
                Box(
                    modifier = Modifier
                        .size(avatar)
                        .background(Color(0xFFFAFAFA), CircleShape)
                        .border(2.dp, Color.White, CircleShape)
                        .padding(tokens.sDp(8.dp))
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            onAddMealClick?.invoke()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Lucide.Plus,
                        contentDescription = "Add Meal",
                        tint = Color(0xFF6B6B6B),
                        modifier = Modifier.size(tokens.sDp(24.dp))
                    )
                }
            }
        }
    }
}
