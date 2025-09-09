package com.example.responsiveness.ui.screens.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anothercalorieapp.database.MealWithDetails
import com.example.responsiveness.database.dao.MealDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Calendar
import java.util.Locale

// Data classes for UI state
data class CalendarDayData(
    val date: LocalDate,
    val dayName: String,
    val dayNumber: String,
    val calories: String
)

data class TimeOfDaySummaryData(
    val calories: Double,
    val mealImageUris: List<String?>,
    val mealDetails: List<MealWithDetails?>
)

data class HomeUiState(
    val morning: TimeOfDaySummaryData = TimeOfDaySummaryData(0.0, listOf(null, null, null), listOf(null, null, null)),
    val afternoon: TimeOfDaySummaryData = TimeOfDaySummaryData(0.0, listOf(null, null, null), listOf(null, null, null)),
    val evening: TimeOfDaySummaryData = TimeOfDaySummaryData(0.0, listOf(null, null, null), listOf(null, null, null))
)

data class HomeState(
    val uiState: HomeUiState = HomeUiState(),
    val calendarData: List<CalendarDayData> = emptyList(),
    val displayedMonth: String = "",
    val todayCalories: Double = 0.0,
    val todayProtein: Double = 0.0,
    val todayCarbs: Double = 0.0,
    val todayFat: Double = 0.0,
    val maxCalories: Double = 2000.0,
    val maxProtein: Double = 150.0,
    val maxCarbs: Double = 300.0,
    val maxFat: Double = 100.0,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

/**
 * ViewModel for Home screen following MVVM conventions.
 * Handles all business logic, data transformations, and state management.
 */
class HomeViewModel(private val mealDao: MealDao) : ViewModel() {

    // Private mutable state
    private val _homeState = MutableStateFlow(HomeState())
    val homeState: StateFlow<HomeState> = _homeState.asStateFlow()

    // Legacy exposed states for backward compatibility
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _calendarCaloriesByDate = MutableStateFlow<Map<LocalDate, String>>(emptyMap())
    val calendarCaloriesByDate: StateFlow<Map<LocalDate, String>> = _calendarCaloriesByDate.asStateFlow()

    private val _todayCalories = MutableStateFlow(0.0)
    val todayCalories: StateFlow<Double> = _todayCalories.asStateFlow()

    private val _todayProtein = MutableStateFlow(0.0)
    val todayProtein: StateFlow<Double> = _todayProtein.asStateFlow()

    private val _todayCarbs = MutableStateFlow(0.0)
    val todayCarbs: StateFlow<Double> = _todayCarbs.asStateFlow()

    private val _todayFat = MutableStateFlow(0.0)
    val todayFat: StateFlow<Double> = _todayFat.asStateFlow()

    private val _maxCalories = MutableStateFlow(2000.0)
    val maxCalories: StateFlow<Double> = _maxCalories.asStateFlow()

    private val _maxProtein = MutableStateFlow(150.0)
    val maxProtein: StateFlow<Double> = _maxProtein.asStateFlow()

    private val _maxCarbs = MutableStateFlow(300.0)
    val maxCarbs: StateFlow<Double> = _maxCarbs.asStateFlow()

    private val _maxFat = MutableStateFlow(100.0)
    val maxFat: StateFlow<Double> = _maxFat.asStateFlow()

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
        observeData()
    }

    /**
     * Observes data changes and updates the home state accordingly
     */
    private fun observeData() {
        viewModelScope.launch {
            combine(
                mealDao.getAllMealsWithDetails(),
                dateFlow
            ) { allMeals, currentDate ->
                processData(allMeals, currentDate)
            }.collect { newState ->
                _homeState.value = newState
                // Update legacy states for backward compatibility
                updateLegacyStates(newState)
            }
        }
    }

    /**
     * Updates legacy state flows for backward compatibility
     */
    private fun updateLegacyStates(homeState: HomeState) {
        _uiState.value = homeState.uiState
        _calendarCaloriesByDate.value = calendarDataToMap(homeState.calendarData)
        _todayCalories.value = homeState.todayCalories
        _todayProtein.value = homeState.todayProtein
        _todayCarbs.value = homeState.todayCarbs
        _todayFat.value = homeState.todayFat
        _maxCalories.value = homeState.maxCalories
        _maxProtein.value = homeState.maxProtein
        _maxCarbs.value = homeState.maxCarbs
        _maxFat.value = homeState.maxFat
    }

    /**
     * Processes meal data and creates the home state
     */
    private suspend fun processData(allMeals: List<MealWithDetails>, currentDate: LocalDate): HomeState {
        try {
            _homeState.value = _homeState.value.copy(isLoading = true, errorMessage = null)

            // Filter today's meals
            val todayMeals = filterMealsByDate(allMeals, currentDate)

            // Calculate nutrition totals
            val nutritionTotals = calculateNutritionTotals(todayMeals)

            // Create time of day summaries
            val timeOfDaySummaries = createTimeOfDaySummaries(todayMeals)

            // Create calendar data
            val calendarData = createCalendarData(allMeals)

            // Get user preferences (default values for now)
            val userPreferences = getUserPreferences()

            val displayedMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault()))

            return HomeState(
                uiState = HomeUiState(
                    morning = timeOfDaySummaries.morning,
                    afternoon = timeOfDaySummaries.afternoon,
                    evening = timeOfDaySummaries.evening
                ),
                calendarData = calendarData,
                displayedMonth = displayedMonth,
                todayCalories = nutritionTotals.calories,
                todayProtein = nutritionTotals.protein,
                todayCarbs = nutritionTotals.carbs,
                todayFat = nutritionTotals.fat,
                maxCalories = userPreferences.maxCalories,
                maxProtein = userPreferences.maxProtein,
                maxCarbs = userPreferences.maxCarbs,
                maxFat = userPreferences.maxFat,
                isLoading = false,
                errorMessage = null
            )
        } catch (e: Exception) {
            return _homeState.value.copy(
                isLoading = false,
                errorMessage = "Failed to load data: ${e.message}"
            )
        }
    }

    /**
     * Filters meals by a specific date
     */
    private fun filterMealsByDate(meals: List<MealWithDetails>, date: LocalDate): List<MealWithDetails> {
        return meals.filter { mealWithDetails ->
            val mealDate = Instant.ofEpochMilli(mealWithDetails.meal.created_at)
                .atZone(ZoneId.systemDefault()).toLocalDate()
            mealDate == date
        }
    }

    /**
     * Calculates total nutrition values for a list of meals
     */
    private fun calculateNutritionTotals(meals: List<MealWithDetails>): NutritionTotals {
        return meals.fold(NutritionTotals()) { totals, meal ->
            val quantity = meal.meal.quantity ?: 1.0
            val nutrition = meal.nutrition
            totals.copy(
                calories = totals.calories + (nutrition?.energy_kcal ?: 0.0) * quantity,
                protein = totals.protein + (nutrition?.protein_g ?: 0.0) * quantity,
                carbs = totals.carbs + (nutrition?.carbohydrates_g ?: 0.0) * quantity,
                fat = totals.fat + (nutrition?.fat_g ?: 0.0) * quantity
            )
        }
    }

    /**
     * Creates time of day summaries by categorizing meals
     */
    private fun createTimeOfDaySummaries(meals: List<MealWithDetails>): TimeOfDaySummaries {
        val morningMeals = mutableListOf<MealWithDetails>()
        val afternoonMeals = mutableListOf<MealWithDetails>()
        val eveningMeals = mutableListOf<MealWithDetails>()

        meals.forEach { meal ->
            val hour = Calendar.getInstance().apply {
                timeInMillis = meal.meal.created_at
            }.get(Calendar.HOUR_OF_DAY)

            when {
                hour in 5..10 || hour in 0..4 -> morningMeals.add(meal)
                hour in 11..16 -> afternoonMeals.add(meal)
                hour in 17..23 -> eveningMeals.add(meal)
            }
        }

        return TimeOfDaySummaries(
            morning = createTimeOfDaySummaryData(morningMeals),
            afternoon = createTimeOfDaySummaryData(afternoonMeals),
            evening = createTimeOfDaySummaryData(eveningMeals)
        )
    }

    /**
     * Creates summary data for a specific time of day
     */
    private fun createTimeOfDaySummaryData(meals: List<MealWithDetails>): TimeOfDaySummaryData {
        val calories = meals.sumOf { (it.nutrition?.energy_kcal ?: 0.0) * (it.meal.quantity ?: 1.0) }
        val sortedMeals = meals.sortedByDescending { it.meal.created_at }
        val images = sortedMeals.map { it.meal.image_path }.take(3)
        val details = sortedMeals.take(3)
        val filledImages = images + List(3 - images.size) { null }
        val filledDetails = details + List(3 - details.size) { null }

        return TimeOfDaySummaryData(calories, filledImages, filledDetails)
    }

    /**
     * Creates calendar data for the last 7 days
     */
    private fun createCalendarData(allMeals: List<MealWithDetails>): List<CalendarDayData> {
        val daysCount = 7
        val today = LocalDate.now()
        val calendarDays = mutableListOf<CalendarDayData>()

        // Create date to calories map
        val caloriesByDate = mutableMapOf<LocalDate, Double>()
        allMeals.forEach { meal ->
            val mealDate = Instant.ofEpochMilli(meal.meal.created_at)
                .atZone(ZoneId.systemDefault()).toLocalDate()
            val calories = (meal.nutrition?.energy_kcal ?: 0.0) * (meal.meal.quantity ?: 1.0)
            caloriesByDate[mealDate] = caloriesByDate.getOrDefault(mealDate, 0.0) + calories
        }

        // Generate calendar data for last 7 days
        for (i in 0 until daysCount) {
            val date = today.minusDays((daysCount - 1 - i).toLong())
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
     * Gets user preferences (placeholder for future implementation)
     */
    private suspend fun getUserPreferences(): UserPreferences {
        // TODO: Implement actual user preferences retrieval
        return UserPreferences(
            maxCalories = 2000.0,
            maxProtein = 150.0,
            maxCarbs = 300.0,
            maxFat = 100.0
        )
    }

    /**
     * Converts calendar data to map for backward compatibility
     */
    private fun calendarDataToMap(calendarData: List<CalendarDayData>): Map<LocalDate, String> {
        return calendarData.associate { it.date to it.calories }
    }

    // Helper data classes
    private data class NutritionTotals(
        val calories: Double = 0.0,
        val protein: Double = 0.0,
        val carbs: Double = 0.0,
        val fat: Double = 0.0
    )

    private data class TimeOfDaySummaries(
        val morning: TimeOfDaySummaryData,
        val afternoon: TimeOfDaySummaryData,
        val evening: TimeOfDaySummaryData
    )

    private data class UserPreferences(
        val maxCalories: Double,
        val maxProtein: Double,
        val maxCarbs: Double,
        val maxFat: Double
    )

    /**
     * Public method to refresh data manually
     */
    fun refreshData() {
        viewModelScope.launch {
            _homeState.value = _homeState.value.copy(isLoading = true)
            // The combine flow will automatically trigger a refresh
        }
    }

    /**
     * Public method to handle errors
     */
    fun clearError() {
        _homeState.value = _homeState.value.copy(errorMessage = null)
    }
}