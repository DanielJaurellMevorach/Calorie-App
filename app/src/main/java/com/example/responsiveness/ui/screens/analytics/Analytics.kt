package com.example.responsiveness.ui.screens.analytics

import android.annotation.SuppressLint
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollFactory
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.responsiveness.DatabaseMealDetailRoute
import com.example.responsiveness.ui.components.general.rememberSafeContentPadding
import com.example.responsiveness.ui.screens.analytics.viewmodel.AnalyticsViewModel
import com.example.responsiveness.ui.screens.home.components.CalendarCalories
import com.example.responsiveness.ui.theme.DesignTokens
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil.CoilImage
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun Analytics(
    navController: NavController,
    viewModel: AnalyticsViewModel,
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAFAFA))
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                focusManager.clearFocus()
                keyboardController?.hide()
            }
    ) {
        val tokens = DesignTokens.provideTokens(availableWidth = maxWidth, availableHeight = maxHeight)

        // Single state collection - all logic moved to ViewModel
        val analyticsState by viewModel.uiState.collectAsState()
        val safePadding = rememberSafeContentPadding(
            includeStatusBar = true,
            includeNavigationBar = true,
            additionalBottomPadding = tokens.navContainerHeight + tokens.navHorizontalPadding * 2 + tokens.sDp(16.dp)
        )

        CompositionLocalProvider(
            LocalOverscrollFactory provides null
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(safePadding)
                    .padding(horizontal = tokens.outerInset),
                verticalArrangement = Arrangement.spacedBy(tokens.sDp(8.dp))
            ) {
                // Calendar component - data and interactions handled by ViewModel
                CalendarCalories(
                    calendarData = analyticsState.calendarData,
                    displayedMonth = analyticsState.displayedMonth,
                    tokens = tokens,
                    analytics = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = tokens.sDp(8.dp)),
                    onDateSelected = viewModel::onDateSelected,
                    selectedDate = analyticsState.filterState.selectedDate,
                    highlightedDates = analyticsState.highlightedDates,
                    onPreviousWeek = viewModel::onPreviousWeek,
                    onNextWeek = viewModel::onNextWeek,
                    isNextWeekEnabled = analyticsState.isNextWeekEnabled
                )

                Spacer(modifier = Modifier.height(tokens.sDp(4.dp)))

                // Search bar - state managed by ViewModel
                SearchBar(
                    tokens = tokens,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = tokens.sDp(8.dp)),
                    searchText = analyticsState.localSearchText,
                    onSearchTextChange = viewModel::onSearchTextChanged,
                    onSearchAction = {
                        focusManager.clearFocus()
                        keyboardController?.hide()
                    }
                )

                Spacer(modifier = Modifier.height(tokens.sDp(8.dp)))

                // Filter toggles - state managed by ViewModel
                ToggleButtonRow(
                    tokens = tokens,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = tokens.sDp(8.dp)),
                    selectedFilter = analyticsState.filterState.selectedFilter,
                    onFilterToggled = viewModel::onFilterToggled
                )

                Spacer(modifier = Modifier.height(tokens.sDp(8.dp)))

                // Meal list - filtered data from ViewModel
                analyticsState.filteredMeals.forEachIndexed { idx, mealWithDetails ->
                    val mealName = mealWithDetails.meal.meal_name
                    val date = java.time.Instant.ofEpochMilli(mealWithDetails.meal.created_at)
                        .atZone(java.time.ZoneId.systemDefault())
                        .toLocalDateTime()
                        .format(DateTimeFormatter.ofPattern("MMM d, yyyy - h:mm a"))
                    val calories = (mealWithDetails.nutrition?.energy_kcal ?: 0.0) * mealWithDetails.meal.quantity
                    val imagePath = mealWithDetails.meal.image_path

                    MealDisplay(
                        tokens = tokens,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = tokens.sDp(8.dp)),
                        mealName = mealName,
                        date = viewModel.formatDateWithWeekday(date), // Use ViewModel method
                        calories = calories.toInt().toString(),
                        imageEmoji = imagePath ?: "\uD83E\uDD57",
                        imagePath = imagePath,
                        mealId = mealWithDetails.meal.id,
                        onClick = {
                            navController.navigate(DatabaseMealDetailRoute(mealWithDetails.meal.id, source = "analytics"))
                        }
                    )
                    if (idx < analyticsState.filteredMeals.size - 1) {
                        Spacer(modifier = Modifier.height(tokens.sDp(8.dp)))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun SearchBar(
    tokens: DesignTokens.Tokens,
    modifier: Modifier = Modifier,
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    onSearchAction: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    TextField(
        value = searchText,
        onValueChange = onSearchTextChange,
        modifier = modifier
            .fillMaxWidth()
            .height(tokens.sDp(54.dp)),
        singleLine = true,
        placeholder = {
            Text(
                text = "Search meals",
                fontSize = tokens.calendarTextSize.times(1.2),
                color = Color(0xFF8A8A8A),
                fontWeight = FontWeight.Normal
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search Icon",
                tint = Color(0xFF5A5A5A),
                modifier = Modifier.size(tokens.sDp(18.dp))
            )
        },
        textStyle = TextStyle(
            fontSize = tokens.sSp(14.sp),
            color = Color(0xFF1A1A1A)
        ),
        shape = CircleShape,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            disabledContainerColor = Color.White,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            cursorColor = Color(0xFF5A5A5A),
            focusedPlaceholderColor = Color(0xFF8A8A8A),
            unfocusedPlaceholderColor = Color(0xFF8A8A8A)
        ),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(
            onSearch = {
                onSearchAction()
                focusManager.clearFocus()
                keyboardController?.hide()
            }
        )
    )
}

@Composable
fun ToggleButtonRow(
    tokens: DesignTokens.Tokens,
    modifier: Modifier = Modifier,
    selectedFilter: String?,
    onFilterToggled: (String) -> Unit = {}
) {
    val items = listOf("Today", "Last 7 Days", "Last 30 Days")
    val listState = rememberLazyListState()
    val selectedIndex = items.indexOf(selectedFilter)

    LaunchedEffect(selectedIndex) {
        if (selectedIndex >= 0) {
            listState.animateScrollToItem(selectedIndex)
        }
    }

    LazyRow(
        state = listState,
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(tokens.sDp(8.dp), Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items(items.size) { index ->
            val text = items[index]
            val isSelected = selectedFilter == text

            val backgroundColor by animateColorAsState(
                targetValue = if (isSelected) Color.Black else Color.White,
                animationSpec = tween(
                    durationMillis = 300,
                    easing = EaseInOut
                ),
                label = "background_color_animation"
            )

            val textColor by animateColorAsState(
                targetValue = if (isSelected) Color.White else Color.Black,
                animationSpec = tween(
                    durationMillis = 300,
                    easing = EaseInOut
                ),
                label = "text_color_animation"
            )

            Box(
                modifier = Modifier
                    .height(tokens.toggleButtonHeight)
                    .background(
                        color = backgroundColor,
                        shape = CircleShape
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        onFilterToggled(text)
                    },
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier.padding(horizontal = tokens.sDp(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = text,
                        color = Color.Transparent,
                        fontSize = tokens.calendarTextSize.times(1.2),
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1
                    )
                    Text(
                        text = text,
                        color = textColor,
                        fontSize = tokens.calendarTextSize.times(1.2),
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
fun MealDisplay(
    tokens: DesignTokens.Tokens,
    modifier: Modifier = Modifier,
    mealName: String,
    date: String,
    calories: String,
    imageEmoji: String = "\uD83E\uDD57",
    imagePath: String? = null,
    mealId: Long,
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                onClick?.invoke()
            },
        shape = CircleShape,
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(tokens.sDp(8.dp)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(tokens.sDp(8.dp))
        ) {
            val imageSize = tokens.sDp(56.dp)
            val imageShape = RoundedCornerShape(
                topStart = tokens.sDp(28.dp),
                bottomStart = tokens.sDp(28.dp),
                topEnd = tokens.sDp(28.dp),
                bottomEnd = tokens.sDp(28.dp)
            )

            Box(
                modifier = Modifier
                    .size(imageSize)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFFFF6B6B).copy(alpha = 0.15f),
                                Color(0xFF4ECDC4).copy(alpha = 0.15f)
                            )
                        ),
                        shape = imageShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (imagePath != null && imagePath.isNotBlank()) {
                    CoilImage(
                        imageModel = { imagePath },
                        modifier = Modifier
                            .size(imageSize)
                            .clip(imageShape)
                            .background(Color.White, imageShape),
                        imageOptions = ImageOptions(
                            contentScale = ContentScale.Crop,
                            alignment = Alignment.Center
                        )
                    )
                } else {
                    Text(
                        text = imageEmoji,
                        fontSize = tokens.sSp(28.sp)
                    )
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(tokens.sDp(2.dp))
            ) {
                Text(
                    text = mealName,
                    color = Color(0xFF1A1A1A),
                    fontSize = tokens.sSp(14.sp),
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = date,
                    color = Color(0xFF6B7280),
                    fontSize = tokens.sSp(10.sp),
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Box(
                modifier = Modifier
                    .padding(horizontal = tokens.sDp(8.dp), vertical = tokens.sDp(4.dp))
                    .wrapContentWidth()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    BasicText(
                        text = "${calories.toFloatOrNull()?.toInt() ?: 0}",
                        modifier = Modifier.padding(top = tokens.sDp(2.dp), bottom = tokens.sDp(1.dp)),
                        style = TextStyle(
                            color = Color.Black,
                            fontSize = tokens.sSp(11.sp),
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.height(tokens.sDp(4.dp)))
                    BasicText(
                        text = "kcal",
                        modifier = Modifier.padding(bottom = tokens.sDp(2.dp), top = tokens.sDp(1.dp)),
                        style = TextStyle(
                            color = Color.Black.copy(alpha = 0.8f),
                            fontSize = tokens.sSp(7.sp),
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
        }
    }
}