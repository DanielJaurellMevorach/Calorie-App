package com.example.responsiveness.ui.screens.analytics.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anothercalorieapp.database.MealWithDetails
import com.example.responsiveness.database.dao.MealDao
import com.example.responsiveness.ui.screens.home.viewmodel.CalendarDayData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Calendar
import java.util.Locale

// Data classes for Analytics UI state
data class FilterState(
    val selectedFilter: String? = null,
    val selectedDate: LocalDate? = null,
    val searchText: String = ""
)

data class AnalyticsUiState(
    val filterState: FilterState = FilterState(),
    val filteredMeals: List<MealWithDetails> = emptyList(),
    val highlightedDates: List<LocalDate> = emptyList(),
    val scrollToDate: LocalDate? = null,
    val localSearchText: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val displayDate: LocalDate = LocalDate.now(),
    val calendarData: List<CalendarDayData> = emptyList(),
    val displayedMonth: String = "",
    val isNextWeekEnabled: Boolean = false
)

/**
 * ViewModel for Analytics screen following MVVM conventions.
 * Handles all business logic, filtering, search, and state management.
 */
class AnalyticsViewModel(private val mealDao: MealDao) : ViewModel() {

    private val _uiState = MutableStateFlow(AnalyticsUiState())
    val uiState: StateFlow<AnalyticsUiState> = _uiState.asStateFlow()

    // Legacy exposed states for backward compatibility
    private val _calendarCaloriesByDate = MutableStateFlow<Map<LocalDate, String>>(emptyMap())
    val calendarCaloriesByDate: StateFlow<Map<LocalDate, String>> = _calendarCaloriesByDate.asStateFlow()

    private val _allMeals = MutableStateFlow<List<MealWithDetails>>(emptyList())
    val allMeals: StateFlow<List<MealWithDetails>> = _allMeals.asStateFlow()

    private val today = LocalDate.now()

    // Date flow that emits when date changes for automatic refresh
    private val dateFlow = flow {
        var lastDate = LocalDate.now()
        emit(lastDate)
        while (true) {
            delay(60_000) // Check every minute
            val currentDate = LocalDate.now()
            if (currentDate != lastDate) {
                lastDate = currentDate
                emit(currentDate)
            }
        }
    }.distinctUntilChanged()

    init {
        // Initialize with default "Today" filter
        _uiState.value = _uiState.value.copy(
            filterState = FilterState(selectedFilter = "Today", selectedDate = today)
        )
        observeData()
    }

    /**
     * Observes data changes and updates the analytics state accordingly
     */
    private fun observeData() {
        viewModelScope.launch {
            combine(
                mealDao.getAllMealsWithDetails(),
                dateFlow
            ) { allMeals, _ ->
                allMeals
            }.collect { allMeals ->
                _allMeals.value = allMeals
                val newState = processData(allMeals)
                _uiState.value = newState
                // Update legacy states for backward compatibility
                updateLegacyStates(allMeals)
            }
        }
    }

    /**
     * Updates legacy state flows for backward compatibility
     */
    private fun updateLegacyStates(allMeals: List<MealWithDetails>) {
        _allMeals.value = allMeals
        _calendarCaloriesByDate.value = createCalendarCaloriesMap(allMeals)
    }

    private fun reprocess() {
        val currentMeals = _allMeals.value
        val newState = processData(currentMeals)
        _uiState.value = newState
    }

    /**
     * Processes meal data and creates the analytics state
     */
    private fun processData(allMeals: List<MealWithDetails>): AnalyticsUiState {
        try {
            val currentState = _uiState.value
            val filteredMeals = filterMeals(allMeals, currentState.filterState)
            val highlightedDates = calculateHighlightedDates(currentState.filterState)
            val scrollToDate = calculateScrollToDate(currentState.filterState)
            val calendarData = createCalendarDataForWeek(currentState.displayDate, allMeals)

            val monthFormatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault())
            val startMonth = calendarData.firstOrNull()?.date?.format(monthFormatter) ?: ""
            val endMonth = calendarData.lastOrNull()?.date?.format(monthFormatter) ?: ""
            val finalMonthDisplay = if (startMonth.isNotEmpty() && startMonth != endMonth) {
                "${calendarData.first().date.format(DateTimeFormatter.ofPattern("MMM"))} - ${calendarData.last().date.format(monthFormatter)}"
            } else {
                startMonth
            }

            val isNextWeekEnabled = calendarData.lastOrNull()?.date?.isBefore(LocalDate.now()) ?: false


            return currentState.copy(
                filteredMeals = filteredMeals,
                highlightedDates = highlightedDates,
                scrollToDate = scrollToDate,
                calendarData = calendarData,
                displayedMonth = finalMonthDisplay,
                isNextWeekEnabled = isNextWeekEnabled,
                isLoading = false,
                errorMessage = null
            )
        } catch (e: Exception) {
            return _uiState.value.copy(
                isLoading = false,
                errorMessage = "Failed to load analytics data: ${e.message}"
            )
        }
    }

    /**
     * Generates calendar data for the 7-day period ending on the given displayDate.
     */
    private fun createCalendarDataForWeek(displayDate: LocalDate, allMeals: List<MealWithDetails>): List<CalendarDayData> {
        val daysCount = 7
        val calendarDays = mutableListOf<CalendarDayData>()
        val caloriesByDate = createCalendarCaloriesMap(allMeals).mapValues { it.value.toDoubleOrNull() ?: 0.0 }

        for (i in 0 until daysCount) {
            val date = displayDate.minusDays((daysCount - 1 - i).toLong())
            val dayName = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
            val dayNumber = date.dayOfMonth.toString()
            val calories = caloriesByDate[date] ?: 0.0
            val caloriesStr = if (calories % 1.0 == 0.0) {
                calories.toInt().toString()
            } else {
                String.format(Locale.getDefault(), "%.1f", calories)
            }
            calendarDays.add(CalendarDayData(date, dayName, dayNumber, caloriesStr))
        }
        return calendarDays
    }


    /**
     * Filters meals based on current filter state
     */
    private fun filterMeals(allMeals: List<MealWithDetails>, filterState: FilterState): List<MealWithDetails> {
        return allMeals.filter { mealWithDetails ->
            val mealDate = Instant.ofEpochMilli(mealWithDetails.meal.created_at)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()

            // First check if there's a search text (custom filter)
            if (filterState.searchText.isNotEmpty()) {
                return@filter mealWithDetails.meal.meal_name.contains(filterState.searchText, ignoreCase = true)
            }

            // Then check the selected filter
            when (filterState.selectedFilter) {
                "Today" -> mealDate == today
                "Last 7 Days" -> {
                    val sevenDaysAgo = today.minusDays(6)
                    mealDate in sevenDaysAgo..today
                }
                "Last 30 Days" -> {
                    val thirtyDaysAgo = today.minusDays(29)
                    mealDate in thirtyDaysAgo..today
                }
                null -> {
                    // No filter selected - check if there's a specific date selected
                    if (filterState.selectedDate != null) {
                        mealDate == filterState.selectedDate
                    } else {
                        true // Show all meals
                    }
                }
                else -> true
            }
        }
    }

    /**
     * Calculates highlighted dates based on filter state
     */
    private fun calculateHighlightedDates(filterState: FilterState): List<LocalDate> {
        return when (filterState.selectedFilter) {
            "Last 7 Days" -> List(7) { today.minusDays(it.toLong()) }
            "Last 30 Days" -> List(30) { today.minusDays(it.toLong()) }
            "Today" -> listOf(today)
            null -> filterState.selectedDate?.let { listOf(it) } ?: emptyList()
            else -> filterState.selectedDate?.let { listOf(it) } ?: emptyList()
        }
    }

    /**
     * Calculates scroll to date based on filter state
     */
    private fun calculateScrollToDate(filterState: FilterState): LocalDate? {
        return if (filterState.selectedFilter == "Today" && filterState.selectedDate == today) {
            today
        } else {
            null
        }
    }

    /**
     * Creates calendar calories map for backward compatibility
     */
    private fun createCalendarCaloriesMap(allMeals: List<MealWithDetails>): Map<LocalDate, String> {
        val caloriesByDate = mutableMapOf<LocalDate, Double>()

        allMeals.forEach { meal ->
            val mealDate = Instant.ofEpochMilli(meal.meal.created_at)
                .atZone(ZoneId.systemDefault()).toLocalDate()
            val calories = (meal.nutrition?.energy_kcal ?: 0.0) * (meal.meal.quantity ?: 1.0)
            caloriesByDate[mealDate] = caloriesByDate.getOrDefault(mealDate, 0.0) + calories
        }

        return caloriesByDate.mapValues { (_, calories) ->
            if (calories % 1.0 == 0.0) {
                calories.toInt().toString()
            } else {
                String.format(Locale.getDefault(), "%.1f", calories)
            }
        }
    }

    /**
     * Public methods for UI interactions
     */

    fun onPreviousWeek() {
        viewModelScope.launch {
            val newDate = _uiState.value.displayDate.minusWeeks(1)
            _uiState.value = _uiState.value.copy(displayDate = newDate, isLoading = true)
            reprocess()
        }
    }

    fun onNextWeek() {
        viewModelScope.launch {
            val newDate = _uiState.value.displayDate.plusWeeks(1)
            // Do not allow navigating past today
            if (newDate.isAfter(LocalDate.now())) {
                _uiState.value = _uiState.value.copy(displayDate = LocalDate.now(), isLoading = true)
            } else {
                _uiState.value = _uiState.value.copy(displayDate = newDate, isLoading = true)
            }
            reprocess()
        }
    }

    fun onDateSelected(date: LocalDate) {
        val newFilterState = if (date == today) {
            FilterState(selectedFilter = "Today", selectedDate = today, searchText = "")
        } else {
            FilterState(selectedFilter = null, selectedDate = date, searchText = "")
        }
        updateFilterState(newFilterState)
    }

    fun onWeekChanged(visibleDates: List<LocalDate>) {
        val currentState = _uiState.value.filterState
        val containsToday = visibleDates.contains(today)

        if (!containsToday) {
            // If today is not visible, untoggle filter, but keep selected date if set
            if (currentState.selectedFilter != null) {
                updateFilterState(currentState.copy(selectedFilter = null, searchText = ""))
            }
        } else {
            // If filter was Today and today is visible, keep it
            if (currentState.selectedFilter == "Today") {
                updateFilterState(FilterState(selectedFilter = "Today", selectedDate = today, searchText = ""))
            } else if (currentState.selectedDate != null && visibleDates.contains(currentState.selectedDate)) {
                updateFilterState(FilterState(selectedFilter = null, selectedDate = currentState.selectedDate, searchText = ""))
            }
        }
    }

    fun onFilterToggled(filter: String) {
        val currentState = _uiState.value.filterState
        val newFilterState: FilterState
        val newDisplayDate: LocalDate

        if (currentState.selectedFilter == filter) {
            // If clicking the same filter, deselect it
            newFilterState = FilterState(selectedFilter = null, selectedDate = null, searchText = "")
            newDisplayDate = today // Reset to today's week
        } else {
            // Select the new filter
            newFilterState = FilterState(selectedFilter = filter, selectedDate = today, searchText = "")
            newDisplayDate = today // Teleport to today's week for all filters
        }

        _uiState.value = _uiState.value.copy(displayDate = newDisplayDate)
        updateFilterState(newFilterState)
    }

    fun onSearchTextChanged(searchText: String) {
        _uiState.value = _uiState.value.copy(localSearchText = searchText)
    }

    fun onSearchAction(searchText: String) {
        val newFilterState = _uiState.value.filterState.copy(
            searchText = searchText,
            selectedFilter = if (searchText.isNotEmpty()) null else _uiState.value.filterState.selectedFilter
        )
        updateFilterState(newFilterState)
        // Also update local search text to match
        _uiState.value = _uiState.value.copy(localSearchText = searchText)
    }

    /**
     * Updates filter state and triggers data reprocessing
     */
    private fun updateFilterState(newFilterState: FilterState) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(filterState = newFilterState, isLoading = true)
            // Trigger reprocessing with current meals data
            val currentMeals = _allMeals.value
            val newState = processData(currentMeals)
            _uiState.value = newState
        }
    }

    /**
     * Public method to refresh data manually
     */
    fun refreshData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            // The combine flow will automatically trigger a refresh
        }
    }

    /**
     * Public method to handle errors
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    /**
     * Format date with weekday for meal display
     */
    fun formatDateWithWeekday(dateString: String): String {
        return try {
            val parts = dateString.split(" - ")
            if (parts.size == 2) {
                val datePart = parts[0].trim()
                val timePart = parts[1].trim()

                val inputDateFormatter = java.time.format.DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.getDefault())
                val parsedDate = LocalDate.parse(datePart, inputDateFormatter)

                val weekday = parsedDate.dayOfWeek.getDisplayName(
                    TextStyle.SHORT,
                    Locale.getDefault()
                )
                val dateNoYearFormatter = java.time.format.DateTimeFormatter.ofPattern("MMM d", Locale.getDefault())
                val dateNoYear = parsedDate.format(dateNoYearFormatter)

                val timeFormatter12 = java.time.format.DateTimeFormatter.ofPattern("h:mm a", Locale.getDefault())
                val localTime = java.time.LocalTime.parse(timePart, timeFormatter12)
                val timeFormatter24 = java.time.format.DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault())
                val time24 = localTime.format(timeFormatter24)

                "$weekday, $dateNoYear - $time24"
            } else {
                dateString
            }
        } catch (e: Exception) {
            dateString
        }
    }
}