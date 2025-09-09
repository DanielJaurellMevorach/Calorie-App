package com.example.responsiveness.ui.screens.analytics

import CalendarCalories
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import com.example.responsiveness.ui.theme.DesignTokens
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil.CoilImage
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

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
        val calendarCaloriesByDate by viewModel.calendarCaloriesByDate.collectAsState()
        val allMeals by viewModel.allMeals.collectAsState()

        // Get proper content padding that accounts for the status bar and floating navigation bar.
        val safePadding = rememberSafeContentPadding(
            includeStatusBar = true,
            includeNavigationBar = true,
            additionalBottomPadding = tokens.navContainerHeight + tokens.navHorizontalPadding * 2 + tokens.sDp(16.dp)
        )

        data class FilterState(val selectedFilter: String?, val date: LocalDate?, val searchText: String)
        val today = LocalDate.now()
        // Use a single source of truth for filter state, and only set default on first launch
        val filterState = rememberSaveable(stateSaver = Saver(
            save = { listOf(it.selectedFilter, it.date?.toString(), it.searchText) },
            restore = {
                val list = it as List<*>
                val filter = list[0] as String?
                val dateStr = list[1] as String?
                val searchText = list[2] as String
                FilterState(filter, dateStr?.let { LocalDate.parse(it) }, searchText)
            }
        )) { mutableStateOf(FilterState("Today", today, "")) }

        var localSearchText by remember(filterState.value.searchText) { mutableStateOf(filterState.value.searchText) }

        // Remove lastSelectedDay and lastSelectedFilter, rely only on filterState

        val highlightedDates by remember(filterState.value) {
            derivedStateOf {
                when (filterState.value.selectedFilter) {
                    "Last 7 Days" -> List(7) { today.minusDays(it.toLong()) }
                    "Last 30 Days" -> List(30) { today.minusDays(it.toLong()) }
                    "Today" -> listOf(today)
                    null -> filterState.value.date?.let { listOf(it) } ?: emptyList() // Only highlight if a date is selected
                    else -> filterState.value.date?.let { listOf(it) } ?: emptyList()
                }
            }
        }
        // Only scroll to date if the filter is "Today" and the date is today, otherwise null
        val scrollToDate by remember(filterState.value) {
            derivedStateOf {
                if (filterState.value.selectedFilter == "Today" && filterState.value.date == today) today else null
            }
        }

        // --- Week change logic ---
        fun handleWeekChanged(visibleDates: List<LocalDate>) {
            val containsToday = visibleDates.contains(today)
            val selected = filterState.value
            if (!containsToday) {
                // If today is not visible, untoggle filter, but keep selected date if set
                if (selected.selectedFilter != null) {
                    filterState.value = selected.copy(selectedFilter = null, date = selected.date, searchText = "")
                }
            } else {
                // If filter was Today and today is visible, keep it
                if (selected.selectedFilter == "Today") {
                    filterState.value = FilterState("Today", today, "")
                } else if (selected.date != null && visibleDates.contains(selected.date)) {
                    filterState.value = FilterState(null, selected.date, "")
                }
            }
        }

        CompositionLocalProvider(
            LocalOverscrollFactory provides null
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(safePadding) // Use the calculated safe padding directly
                    .padding(horizontal = tokens.outerInset), // Apply horizontal padding separately
                verticalArrangement = Arrangement.spacedBy(tokens.sDp(8.dp))
            ) {
                CalendarCalories(
                    calendarCaloriesByDate = calendarCaloriesByDate,
                    tokens = tokens,
                    analytics = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = tokens.sDp(8.dp)),
                    onDateSelected = { date: LocalDate ->
                        if (date == today) {
                            filterState.value = FilterState("Today", today, "")
                        } else {
                            filterState.value = FilterState(null, date, "")
                        }
                    },
                    selectedDate = filterState.value.date,
                    highlightedDates = highlightedDates,
                    scrollToDate = scrollToDate,
                    onWeekChanged = { visibleDates ->
                        handleWeekChanged(visibleDates)
                    }
                )

                Spacer(
                    modifier = Modifier.height(tokens.sDp(4.dp))
                )

                SearchBar(
                    tokens = tokens,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = tokens.sDp(8.dp)),
                    searchText = localSearchText,
                    onSearchTextChange = { newText ->
                        localSearchText = newText
                    },
                    onSearchAction = {
                        filterState.value = filterState.value.copy(
                            searchText = localSearchText,
                            selectedFilter = if (localSearchText.isNotEmpty()) null else filterState.value.selectedFilter
                        )
                        focusManager.clearFocus()
                        keyboardController?.hide()
                    }
                )

                Spacer(
                    modifier = Modifier.height(tokens.sDp(8.dp))
                )

                ToggleButtonRow(
                    tokens = tokens,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = tokens.sDp(8.dp)),
                    selectedFilter = filterState.value.selectedFilter,
                    onFilterToggled = { filter ->
                        filterState.value = if (filterState.value.selectedFilter == filter) {
                            // If clicking the same filter, deselect it (go to "All" mode)
                            FilterState(null, null, "")
                        } else {
                            // Select the new filter
                            val newDate = if (filter == "Today") today else null
                            FilterState(filter, newDate, "")
                        }
                    }
                )

                Spacer(
                    modifier = Modifier.height(tokens.sDp(8.dp))
                )

                val filteredMeals = allMeals.filter { mealWithDetails ->
                    val mealDate = java.time.Instant.ofEpochMilli(mealWithDetails.meal.created_at)
                        .atZone(java.time.ZoneId.systemDefault())
                        .toLocalDate()

                    // First check if there's a search text (custom filter)
                    if (filterState.value.searchText.isNotEmpty()) {
                        return@filter mealWithDetails.meal.meal_name.contains(filterState.value.searchText, ignoreCase = true)
                    }

                    // Then check the selected filter
                    when (filterState.value.selectedFilter) {
                        "Today" -> mealDate == today
                        "Last 7 Days" -> {
                            val sevenDaysAgo = today.minusDays(6)
                            mealDate in sevenDaysAgo..today
                        }
                        "Last 30 Days" -> {
                            val thirtyDaysAgo = today.minusDays(30)
                            mealDate in thirtyDaysAgo..today
                        }
                        null -> {
                            // No filter selected - check if there's a specific date selected
                            if (filterState.value.date != null) {
                                mealDate == filterState.value.date
                            } else {
                                true // Show all meals
                            }
                        }
                        else -> true
                    }
                }

                filteredMeals.forEachIndexed { idx, mealWithDetails ->
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
                        date = date,
                        calories = calories.toInt().toString(),
                        imageEmoji = imagePath ?: "\uD83E\uDD57",
                        imagePath = imagePath,
                        mealId = mealWithDetails.meal.id,
                        onClick = {
                            navController.navigate(DatabaseMealDetailRoute(mealWithDetails.meal.id, source = "analytics"))
                        }
                    )
                    if (idx < filteredMeals.size - 1) {
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
    val sSp = tokens.sSp
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
                //fontSize = sSp(14.sp),
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
            fontSize = sSp(14.sp),
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
    val sDp = tokens.sDp
    val sSp = tokens.sSp

    LaunchedEffect(selectedIndex) {
        if (selectedIndex >= 0) {
            listState.animateScrollToItem(selectedIndex)
        }
    }

    LazyRow(
        state = listState,
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(sDp(8.dp), Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items(items.size) { index ->
            val text = items[index]
            val isSelected = selectedFilter == text

            // Animate the background color with fade effect
            val backgroundColor by animateColorAsState(
                targetValue = if (isSelected) Color.Black else Color.White,
                animationSpec = tween(
                    durationMillis = 300,
                    easing = EaseInOut
                ),
                label = "background_color_animation"
            )

            // Animate the text color
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
                // Use a Box with fixed dimensions to prevent layout shifts
                Box(
                    modifier = Modifier.padding(horizontal = sDp(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    // Invisible text with bold weight to reserve space
                    Text(
                        text = text,
                        color = Color.Transparent,
                        fontSize = tokens.calendarTextSize.times(1.2),
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1
                    )
                    // Visible text with animated properties
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
                topStart = tokens.sDp(28.dp), // large rounding for left corners
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
                    text = formatDateWithWeekday(date),
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
                    Spacer(
                        modifier = Modifier.height(tokens.sDp(4.dp))
                    )
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

private fun formatDateWithWeekday(dateString: String): String {
    return try {
        val parts = dateString.split(" - ")
        if (parts.size == 2) {
            val datePart = parts[0].trim()
            val timePart = parts[1].trim()

            val inputDateFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.getDefault())
            val parsedDate = LocalDate.parse(datePart, inputDateFormatter)

            val weekday = parsedDate.dayOfWeek.getDisplayName(
                java.time.format.TextStyle.SHORT,
                Locale.getDefault()
            )
            val dateNoYearFormatter = DateTimeFormatter.ofPattern("MMM d", Locale.getDefault())
            val dateNoYear = parsedDate.format(dateNoYearFormatter)

            val timeFormatter12 = DateTimeFormatter.ofPattern("h:mm a", Locale.getDefault())
            val localTime = LocalTime.parse(timePart, timeFormatter12)
            val timeFormatter24 = DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault())
            val time24 = localTime.format(timeFormatter24)

            "$weekday, $dateNoYear - $time24"
        } else {
            dateString
        }
    } catch (e: Exception) {
        dateString
    }
}