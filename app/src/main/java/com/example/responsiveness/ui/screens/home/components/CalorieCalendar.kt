package com.example.responsiveness.ui.screens.home.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.ArrowLeft
import com.composables.icons.lucide.ArrowRight
import com.composables.icons.lucide.Lucide
import com.example.responsiveness.ui.screens.home.viewmodel.CalendarDayData
import com.example.responsiveness.ui.theme.DesignTokens
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Optimized CalendarCalories component following MVVM principles.
 * Receives processed data from ViewModel and focuses purely on UI rendering.
 */
@Composable
fun CalendarCalories(
    modifier: Modifier = Modifier,
    calendarData: List<CalendarDayData>,
    tokens: DesignTokens.Tokens,
    analytics: Boolean = false,
    onDateSelected: (LocalDate) -> Unit = {},
    selectedDate: LocalDate? = null,
    highlightedDates: List<LocalDate> = emptyList(),
    scrollToDate: LocalDate? = null,
    onWeekChanged: (List<LocalDate>) -> Unit = {},
    onPreviousWeek: (() -> Unit)? = null,
    onNextWeek: (() -> Unit)? = null
) {
    // State for selected day (for analytics mode)
    var selectedDayIndex by remember { mutableStateOf(6) } // Default to today (last day)

    // Current month display - calculated from today for simplicity
    val today = LocalDate.now()
    val currentMonth = today.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault()))

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(tokens.sDp(38.dp)))
            .padding(tokens.sDp(16.dp))
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceAround,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Header with month and navigation (if analytics)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(tokens.sDp(32.dp)),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = currentMonth,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = tokens.sSp(14.sp)
                )

                if (analytics) {
                    Row {
                        // Previous week arrow
                        Box(
                            modifier = Modifier
                                .background(Color(0xFFFAFAFA), RoundedCornerShape(50.dp))
                                .padding(tokens.sDp(8.dp))
                                .clickable(
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() }
                                ) {
                                    onPreviousWeek?.invoke()
                                }
                        ) {
                            Icon(
                                imageVector = Lucide.ArrowLeft,
                                contentDescription = "Previous Week",
                                tint = Color(0xFF6B6B6B)
                            )
                        }

                        Spacer(modifier = Modifier.width(tokens.sDp(8.dp)))

                        // Next week arrow
                        Box(
                            modifier = Modifier
                                .background(Color(0xFFFAFAFA), RoundedCornerShape(50.dp))
                                .padding(tokens.sDp(8.dp))
                                .clickable(
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() }
                                ) {
                                    onNextWeek?.invoke()
                                }
                        ) {
                            Icon(
                                imageVector = Lucide.ArrowRight,
                                contentDescription = "Next Week",
                                tint = Color(0xFF6B6B6B)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(tokens.sDp(8.dp)))

            // Calendar days row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(tokens.sDp(8.dp)),
                verticalAlignment = Alignment.Top
            ) {
                calendarData.forEachIndexed { index, dayData ->
                    CalendarDay(
                        dayData = dayData,
                        isSelected = if (analytics) index == selectedDayIndex else index == 6, // Home always shows today selected
                        isHighlighted = false, // Could be extended for highlighted dates
                        tokens = tokens,
                        onDayClick = {
                            if (analytics) {
                                selectedDayIndex = index
                                // Convert index to LocalDate and notify parent
                                val date = today.minusDays((6 - index).toLong())
                                onDateSelected(date)
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

/**
 * Individual calendar day component
 */
@Composable
private fun CalendarDay(
    dayData: CalendarDayData,
    isSelected: Boolean,
    isHighlighted: Boolean,
    tokens: DesignTokens.Tokens,
    onDayClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Determine colors based on state
    val targetBackgroundColor = when {
        isHighlighted -> Color(0xFF222222)
        isSelected -> Color.Black
        else -> Color.White
    }
    val targetTextColor = when {
        isHighlighted -> Color.White
        isSelected -> Color.White
        else -> Color.Black
    }

    // Animate color transitions
    val backgroundColor by animateColorAsState(
        targetValue = targetBackgroundColor,
        animationSpec = tween(
            durationMillis = 300,
            easing = EaseInOut
        ),
        label = "day_background_animation"
    )

    val textColor by animateColorAsState(
        targetValue = targetTextColor,
        animationSpec = tween(
            durationMillis = 300,
            easing = EaseInOut
        ),
        label = "day_text_animation"
    )

    val fontWeight = when {
        isHighlighted -> FontWeight.Bold
        isSelected -> FontWeight.SemiBold
        else -> FontWeight.Normal
    }

    Column(
        modifier = modifier
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(tokens.sDp(38.dp))
            )
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onDayClick() }
            .padding(
                top = tokens.sDp(12.dp),
                bottom = tokens.sDp(12.dp),
                start = tokens.sDp(4.dp),
                end = tokens.sDp(4.dp)
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Day name (Mon, Tue, etc.)
        Text(
            text = dayData.dayName,
            color = textColor,
            fontSize = tokens.calendarTextSize,
            fontWeight = fontWeight,
            lineHeight = tokens.calendarTextSize
        )

        Spacer(modifier = Modifier.height(tokens.sDp(4.dp)))

        // Day number
        Text(
            text = dayData.dayNumber,
            color = textColor,
            fontSize = tokens.calendarTextSize,
            fontWeight = FontWeight.Bold,
            lineHeight = tokens.calendarTextSize
        )

        Spacer(modifier = Modifier.height(tokens.sDp(4.dp)))

        // Calories
        Text(
            text = dayData.calories,
            color = textColor,
            fontSize = tokens.calendarTextSize.times(0.8f),
            fontWeight = fontWeight,
            lineHeight = tokens.calendarTextSize
        )
    }
}

/**
 * Backward compatibility overload for existing callers
 */
@Composable
fun CalendarCalories(
    modifier: Modifier = Modifier,
    calendarCaloriesByDate: Map<LocalDate, String>,
    tokens: DesignTokens.Tokens,
    analytics: Boolean = false,
    onDateSelected: (LocalDate) -> Unit = {},
    selectedDate: LocalDate? = null,
    highlightedDates: List<LocalDate> = emptyList(),
    scrollToDate: LocalDate? = null,
    onWeekChanged: (List<LocalDate>) -> Unit = {},
    onPreviousWeek: (() -> Unit)? = null,
    onNextWeek: (() -> Unit)? = null
) {
    // Convert map to CalendarDayData list for backward compatibility
    val today = LocalDate.now()
    val calendarData = (0..6).map { i ->
        val date = today.minusDays((6 - i).toLong())
        CalendarDayData(
            dayName = date.dayOfWeek.getDisplayName(java.time.format.TextStyle.SHORT, Locale.getDefault()),
            dayNumber = date.dayOfMonth.toString(),
            calories = calendarCaloriesByDate[date] ?: "0"
        )
    }

    CalendarCalories(
        modifier = modifier,
        calendarData = calendarData,
        tokens = tokens,
        analytics = analytics,
        onDateSelected = onDateSelected,
        selectedDate = selectedDate,
        highlightedDates = highlightedDates,
        scrollToDate = scrollToDate,
        onWeekChanged = onWeekChanged,
        onPreviousWeek = onPreviousWeek,
        onNextWeek = onNextWeek
    )
}
