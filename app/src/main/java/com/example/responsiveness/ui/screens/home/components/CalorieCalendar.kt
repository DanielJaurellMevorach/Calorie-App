import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.responsiveness.ui.theme.DesignTokens
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun CalendarCalories(
    modifier: Modifier = Modifier,
    calendarCaloriesByDate: Map<LocalDate, String>,
    tokens: DesignTokens.Tokens,
    analytics: Boolean = false,
    onDateSelected: (LocalDate) -> Unit = {},
    selectedDate: LocalDate? = null, // NEW PARAM
    highlightedDates: List<LocalDate> = emptyList(), // NEW PARAM
    scrollToDate: LocalDate? = null, // NEW PARAM
    onWeekChanged: (List<LocalDate>) -> Unit = {} // NEW PARAM
) {
    val daysCount = 7
    val today = LocalDate.now()
    val todayIndex = daysCount - 1
    var selectedDayIndex by remember { mutableStateOf(todayIndex) }
    var startDayOffset by remember { mutableStateOf(0) } // For analytics mode, offset for scrolling

    // Helper to get 7 days ending with a given day
    fun getWindowDays(endDate: LocalDate): List<LocalDate> =
        (0 until daysCount).map { endDate.minusDays((daysCount - 1 - it).toLong()) }

    // Scroll to date if requested
    LaunchedEffect(scrollToDate) {
        if (analytics && scrollToDate != null) {
            val offset = today.toEpochDay() - scrollToDate.toEpochDay()
            startDayOffset = offset.coerceAtLeast(0).toInt()
        }
    }

    val visibleDates = if (analytics) {
        getWindowDays(today.minusDays(startDayOffset.toLong()))
    } else {
        getWindowDays(today)
    }

    // Notify parent of week change
    LaunchedEffect(visibleDates) {
        if (analytics) {
            onWeekChanged(visibleDates)
        }
    }

    val parsedDayNames = visibleDates.map { it.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()) }
    val parsedDayNumbers = visibleDates.map { it.dayOfMonth.toString() }
    val parsedCalories = visibleDates.map { date ->
        calendarCaloriesByDate[date]?.toFloatOrNull()?.toInt()?.toString() ?: "0"
    }

    // Use selectedDate if provided (for analytics mode)
    val selectedIndex = selectedDate?.let { visibleDates.indexOf(it) } ?: selectedDayIndex

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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(tokens.sDp(32.dp)),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = visibleDates.last().format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault())),
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = tokens.sSp(14.sp)
                )
                if (analytics) {
                    Row {
                        // Prev week arrow
                        Box(
                            modifier = Modifier
                                .background(Color((0xFFFAFAFA)), RoundedCornerShape(50.dp))
                                .padding(tokens.sDp(8.dp))
                        ) {
                            Icon(
                                imageVector = Lucide.ArrowLeft,
                                contentDescription = "Previous Week",
                                tint = Color(0xFF6B6B6B),
                                modifier = Modifier.clickable(
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() }
                                ) {
                                    startDayOffset += 7
                                }
                            )
                        }
                        Spacer(modifier = Modifier.width(tokens.sDp(8.dp)))
                        // Next week arrow
                        Box(
                            modifier = Modifier
                                .background(Color((0xFFFAFAFA)), RoundedCornerShape(50.dp))
                                .padding(tokens.sDp(8.dp))
                        ) {
                            Icon(
                                imageVector = Lucide.ArrowRight,
                                contentDescription = "Next Week",
                                tint = Color(0xFF6B6B6B),
                                modifier = Modifier.clickable(
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() }
                                ) {
                                    if (startDayOffset > 0) startDayOffset -= 7
                                }
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(tokens.sDp(8.dp)))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .let { m -> if (analytics) m.horizontalScroll(rememberScrollState()) else m },
                horizontalArrangement = Arrangement.spacedBy(tokens.sDp(8.dp)),
                verticalAlignment = Alignment.Top
            ) {
                repeat(daysCount) { index ->
                    val date = visibleDates[index]
                    val isHighlighted = highlightedDates.contains(date)
                    val isSelected = index == selectedIndex

                    // Determine target colors based on state
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

                    // Animate the colors with fade effect
                    val backgroundColor by animateColorAsState(
                        targetValue = targetBackgroundColor,
                        animationSpec = tween(
                            durationMillis = 300,
                            easing = EaseInOut
                        ),
                        label = "day_background_animation_$index"
                    )

                    val textColor by animateColorAsState(
                        targetValue = targetTextColor,
                        animationSpec = tween(
                            durationMillis = 300,
                            easing = EaseInOut
                        ),
                        label = "day_text_animation_$index"
                    )

                    val fontWeight = when {
                        isHighlighted -> FontWeight.Bold
                        isSelected -> FontWeight.SemiBold
                        else -> FontWeight.Normal
                    }

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .background(
                                color = backgroundColor,
                                shape = RoundedCornerShape(tokens.sDp(38.dp))
                            )
                            .padding(
                                top = tokens.sDp(12.dp),
                                bottom = tokens.sDp(12.dp),
                                start = tokens.sDp(4.dp),
                                end = tokens.sDp(4.dp)
                            )
                            .let { mod ->
                                if (analytics) mod.clickable(
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() }
                                ) {
                                    selectedDayIndex = index
                                    onDateSelected(visibleDates[index])
                                } else mod
                            },
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = parsedDayNames[index],
                            color = textColor,
                            fontSize = tokens.calendarTextSize,
                            fontWeight = fontWeight,
                            lineHeight = tokens.calendarTextSize
                        )
                        Spacer(modifier = Modifier.height(tokens.sDp(4.dp)))
                        Text(
                            text = parsedDayNumbers[index],
                            color = textColor,
                            fontSize = tokens.calendarTextSize,
                            fontWeight = FontWeight.Bold,
                            lineHeight = tokens.calendarTextSize
                        )
                        Spacer(modifier = Modifier.height(tokens.sDp(4.dp)))
                        Text(
                            text = parsedCalories[index],
                            color = textColor,
                            fontSize = tokens.calendarTextSize.times(0.8f),
                            fontWeight = fontWeight,
                            lineHeight = tokens.calendarTextSize
                        )
                    }
                }
            }
        }
    }
}