package com.example.responsiveness.ui.screens.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anothercalorieapp.database.MealWithDetails
import com.example.responsiveness.database.dao.MealDao
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.Calendar
import java.util.Locale

sealed class TimeOfDay { object Morning; object Afternoon; object Evening }

data class TimeOfDaySummaryData(
    val calories: Double,
    val mealImageUris: List<String?>,
    val mealDetails: List<MealWithDetails?> // Add meal details for each image
)

data class HomeUiState(
    val morning: TimeOfDaySummaryData = TimeOfDaySummaryData(0.0, listOf(null, null, null), listOf(null, null, null)),
    val afternoon: TimeOfDaySummaryData = TimeOfDaySummaryData(0.0, listOf(null, null, null), listOf(null, null, null)),
    val evening: TimeOfDaySummaryData = TimeOfDaySummaryData(0.0, listOf(null, null, null), listOf(null, null, null))
)

/**
 * ViewModel for Home screen. Manages UI state, calendar calories, and today's nutrition summary.
 */
class HomeViewModel(private val mealDao: MealDao) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _calendarCalories = MutableStateFlow(List(7) { 0.0 })
    val calendarCalories: StateFlow<List<Double>> = _calendarCalories.asStateFlow()

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

    private val _allMeals = MutableStateFlow<List<MealWithDetails>>(emptyList())
    val allMeals: StateFlow<List<MealWithDetails>> = _allMeals.asStateFlow()

    private val _maxCalories = MutableStateFlow(2000.0)
    val maxCalories: StateFlow<Double> = _maxCalories.asStateFlow()
    private val _maxProtein = MutableStateFlow(150.0)
    val maxProtein: StateFlow<Double> = _maxProtein.asStateFlow()
    private val _maxCarbs = MutableStateFlow(300.0)
    val maxCarbs: StateFlow<Double> = _maxCarbs.asStateFlow()
    private val _maxFat = MutableStateFlow(100.0)
    val maxFat: StateFlow<Double> = _maxFat.asStateFlow()

    // Add a Flow that emits the current date and triggers a refresh when the date changes
    private val dateFlow = flow {
        var lastDate = LocalDate.now()
        emit(lastDate)
        while (true) {
            delay(60_000) // check every minute
            val currentDate = LocalDate.now()
            if (currentDate != lastDate) {
                lastDate = currentDate
                emit(currentDate)
            }
        }
    }.distinctUntilChanged()

    init {
        viewModelScope.launch {
            mealDao.getUser(1).collect { user ->
                _maxCalories.value = user?.maxCalories?.toDouble() ?: 2000.0
                _maxProtein.value = user?.maxProtein?.toDouble() ?: 150.0
                _maxCarbs.value = user?.maxCarbs?.toDouble() ?: 300.0
                _maxFat.value = user?.maxFat?.toDouble() ?: 100.0
            }
        }
        viewModelScope.launch {
            combine(
                mealDao.getTodayMealsWithDetailsFlow(),
                dateFlow
            ) { mealsWithDetails, currentDate ->
                // Filter meals defensively by the emitted current date to ensure midnight rollover resets state
                val mealsForCurrentDate = mealsWithDetails.filter { mealWithDetails ->
                    val mealDate = Instant.ofEpochMilli(mealWithDetails.meal.created_at)
                        .atZone(ZoneId.systemDefault()).toLocalDate()
                    mealDate == currentDate
                }

                if (mealsForCurrentDate.isEmpty()) {
                    _todayCalories.value = 0.0
                    _todayProtein.value = 0.0
                    _todayCarbs.value = 0.0
                    _todayFat.value = 0.0
                    HomeUiState() // default empty state when new day starts or no meals
                } else {
                    val morningMeals = mutableListOf<MealWithDetails>()
                    val afternoonMeals = mutableListOf<MealWithDetails>()
                    val eveningMeals = mutableListOf<MealWithDetails>()

                    mealsForCurrentDate.forEach { mealWithDetails ->
                        val hour = Calendar.getInstance().apply { timeInMillis = mealWithDetails.meal.created_at }.get(Calendar.HOUR_OF_DAY)
                        when {
                            hour in 5..10 || hour in 0..4 -> morningMeals.add(mealWithDetails)
                            hour in 11..16 -> afternoonMeals.add(mealWithDetails)
                            hour in 17..23 -> eveningMeals.add(mealWithDetails)
                        }
                    }

                    fun processMeals(meals: List<MealWithDetails>): TimeOfDaySummaryData {
                        val calories = meals.sumOf { (it.nutrition?.energy_kcal ?: 0.0) * (it.meal.quantity ?: 1.0) }
                        val sortedMeals = meals.sortedByDescending { it.meal.created_at }
                        val images = sortedMeals.map { it.meal.image_path }.take(3)
                        val details = sortedMeals.take(3)
                        val filledImages = images + List(3 - images.size) { null }
                        val filledDetails = details + List(3 - details.size) { null }
                        return TimeOfDaySummaryData(calories, filledImages, filledDetails)
                    }

                    // Calculate today's totals for calories, protein, carbs, fat using NutritionEntity only
                    val totalCalories = mealsForCurrentDate.sumOf { (it.nutrition?.energy_kcal ?: 0.0) * (it.meal.quantity ?: 1.0) }
                    val totalProtein = mealsForCurrentDate.sumOf { (it.nutrition?.protein_g ?: 0.0) * (it.meal.quantity ?: 1.0) }
                    val totalCarbs = mealsForCurrentDate.sumOf { (it.nutrition?.carbohydrates_g ?: 0.0) * (it.meal.quantity ?: 1.0) }
                    val totalFat = mealsForCurrentDate.sumOf { (it.nutrition?.fat_g ?: 0.0) * (it.meal.quantity ?: 1.0) }
                    _todayCalories.value = totalCalories
                    _todayProtein.value = totalProtein
                    _todayCarbs.value = totalCarbs
                    _todayFat.value = totalFat

                    HomeUiState(
                        morning = processMeals(morningMeals),
                        afternoon = processMeals(afternoonMeals),
                        evening = processMeals(eveningMeals)
                    )
                }
            }.collect { state ->
                _uiState.value = state
            }
        }
        viewModelScope.launch {
            mealDao.getAllMealsWithDetails()
                .distinctUntilChanged()
                .collect { meals ->
                    _allMeals.value = meals
                }
        }
        fetchCalendarCalories()
    }

    /**
     * Fetches calories for the last 7 days and updates calendarCalories and calendarCaloriesByDate.
     */
    private fun fetchCalendarCalories() {
        val daysCount = 7
        val now = Calendar.getInstance()
        // Calculate startMillis for 7 days ago (00:00:00.000)
        val startCal = now.clone() as Calendar
        startCal.add(Calendar.DAY_OF_YEAR, -(daysCount - 1))
        startCal.set(Calendar.HOUR_OF_DAY, 0)
        startCal.set(Calendar.MINUTE, 0)
        startCal.set(Calendar.SECOND, 0)
        startCal.set(Calendar.MILLISECOND, 0)
        val startMillis = startCal.timeInMillis
        // Calculate endMillis for today (23:59:59.999)
        val endCal = now.clone() as Calendar
        endCal.set(Calendar.HOUR_OF_DAY, 23)
        endCal.set(Calendar.MINUTE, 59)
        endCal.set(Calendar.SECOND, 59)
        endCal.set(Calendar.MILLISECOND, 999)
        val endMillis = endCal.timeInMillis
        viewModelScope.launch {
            mealDao.getMealsWithDetailsForDateRangeFlow(startMillis, endMillis).collect { meals ->
                // Debug logging can be enabled for development only
                // Log.d("CalendarCalories", "Fetched ${meals.size} meals from DB for range $startMillis to $endMillis")
                val caloriesByDay = MutableList(daysCount) { 0.0 }
                for (meal in meals) {
                    val mealCal = Calendar.getInstance().apply { timeInMillis = meal.meal.created_at }
                    val dayDiff = ((mealCal.timeInMillis - startCal.timeInMillis) / (1000 * 60 * 60 * 24)).toInt()
                    // Log.d("CalendarCalories", "Meal id=${meal.meal.id}, created_at=${meal.meal.created_at}, nutrition=${meal.nutrition?.energy_kcal}, assigned to dayDiff=$dayDiff")
                    if (dayDiff in 0 until daysCount) {
                        caloriesByDay[dayDiff] += (meal.nutrition?.energy_kcal ?: 0.0) * (meal.meal.quantity ?: 1.0)
                    }
                }
                // Log.d("CalendarCalories", "Final caloriesByDay: $caloriesByDay")
                _calendarCalories.value = caloriesByDay

                // Build date-to-calorie map for last 7 days
                val dateCaloriesMap = mutableMapOf<LocalDate, String>()
                for (i in 0 until daysCount) {
                    val date = LocalDate.now().minusDays((daysCount - 1 - i).toLong())
                    val calVal = caloriesByDay[i]
                    val calStr = if (calVal % 1.0 == 0.0) calVal.toInt().toString() else String.format(
                        Locale.getDefault(), "%.1f", calVal)
                    dateCaloriesMap[date] = calStr
                }
                _calendarCaloriesByDate.value = dateCaloriesMap
            }
        }
    }
}
