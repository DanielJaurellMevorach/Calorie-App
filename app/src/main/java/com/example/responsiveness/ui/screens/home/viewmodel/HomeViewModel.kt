package com.example.responsiveness.ui.screens.home.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anothercalorieapp.database.MealWithDetails
import com.example.anothercalorieapp.database.UserEntity
import com.example.responsiveness.BuildConfig
import com.example.responsiveness.database.dao.MealDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
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
 * ViewModel for Home screen. Observes meals and user preferences (users table) and emits HomeState
 */
class HomeViewModel(private val mealDao: MealDao) : ViewModel() {

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

    private val okHttpClient = OkHttpClient()
    private var lastPingDate: LocalDate? = null

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
        observeAllData()
        // Only use dateFlow to trigger pings, no initial call
        viewModelScope.launch {
            dateFlow.collectLatest {
                tryPingUser()
            }
        }
    }

    /**
     * Observes all data sources (meals, date, user preferences) and updates the home state.
     */
    private fun observeAllData() {
        viewModelScope.launch {
            try {
                combine(
                    mealDao.getAllMealsWithDetails(),
                    dateFlow,
                    mealDao.getUser() // Observe the single user preferences flow
                ) { allMeals, currentDate, userEntity ->
                    // This block is executed whenever any of the flows emit a new value.
                    processData(allMeals, currentDate, userEntity)
                }.collect { newState ->
                    _homeState.value = newState
                    updateLegacyStates(newState)
                    Log.d("HomeViewModel", "Collected new HomeState -> maxCalories=${newState.maxCalories}, maxProtein=${newState.maxProtein}, maxCarbs=${newState.maxCarbs}, maxFat=${newState.maxFat}")
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error observing data: ${e.message}")
                _homeState.value = _homeState.value.copy(isLoading = false, errorMessage = "Failed to load data.")
            }
        }
    }

    /**
     * Updates legacy state flows for backward compatibility.
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
     * Processes all data sources and returns a new HomeState.
     */
    private fun processData(
        allMeals: List<MealWithDetails>,
        currentDate: LocalDate,
        userEntity: UserEntity?
    ): HomeState {
        val todayMeals = filterMealsByDate(allMeals, currentDate)
        val nutritionTotals = calculateNutritionTotals(todayMeals)
        val timeOfDaySummaries = createTimeOfDaySummaries(todayMeals)
        val calendarData = createCalendarData(allMeals)
        val displayedMonth = currentDate.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault()))

        val maxCalories = userEntity?.maxCalories?.toDouble() ?: 2000.0
        val maxProtein = userEntity?.maxProtein?.toDouble() ?: 150.0
        val maxCarbs = userEntity?.maxCarbs?.toDouble() ?: 300.0
        val maxFat = userEntity?.maxFat?.toDouble() ?: 100.0
        val errorMessage = if (userEntity == null) "No user found in database." else null

        Log.d("HomeViewModel", "Processing with User prefs -> maxCalories=$maxCalories, maxProtein=$maxProtein, maxCarbs=$maxCarbs, maxFat=$maxFat")

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
            maxCalories = maxCalories,
            maxProtein = maxProtein,
            maxCarbs = maxCarbs,
            maxFat = maxFat,
            isLoading = false,
            errorMessage = errorMessage
        )
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

            when (hour) {
                in 0..4, in 5..10 -> morningMeals.add(meal)
                in 11..16 -> afternoonMeals.add(meal)
                else -> eveningMeals.add(meal)
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

        val caloriesByDate = allMeals.groupBy(
            { Instant.ofEpochMilli(it.meal.created_at).atZone(ZoneId.systemDefault()).toLocalDate() },
            { (it.nutrition?.energy_kcal ?: 0.0) * (it.meal.quantity ?: 1.0) }
        ).mapValues { it.value.sum() }

        for (i in 0 until daysCount) {
            val date = today.minusDays((daysCount - 1 - i).toLong())
            val dayName = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
            val dayNumber = date.dayOfMonth.toString()
            val calories = caloriesByDate[date] ?: 0.0
            val caloriesStr = if (calories % 1.0 == 0.0) calories.toInt().toString() else String.format(Locale.getDefault(), "%.1f", calories)
            calendarDays.add(CalendarDayData(date, dayName, dayNumber, caloriesStr))
        }

        return calendarDays
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

    private suspend fun tryPingUser() {
        val user = mealDao.getCurrentUser() ?: run {
            Log.d("PingDebug", "No user found in database, skipping ping.")
            return
        }
        val userId = user.id
        val lastPingMillis = user.lastPing
        val lastPingDate = lastPingMillis?.let {
            Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
        }
        val today = LocalDate.now()
        if (lastPingDate == today) {
            Log.d("PingDebug", "Ping already sent today for user $userId. Skipping.")
            return // Already pinged today
        }
        Log.d("PingDebug", "Attempting ping for user $userId on $today (lastPing: $lastPingDate)")
        // Launch async ping with one retry if it fails
        viewModelScope.launch {
            val success = sendPing(userId)
            if (success) {
                Log.d("PingDebug", "Ping successful for user $userId. Updating lastPing in database.")
                mealDao.updateLastPing(userId, System.currentTimeMillis())
            } else {
                Log.d("PingDebug", "Ping failed for user $userId. Retrying once...")
                val retrySuccess = sendPing(userId)
                if (retrySuccess) {
                    Log.d("PingDebug", "Ping retry successful for user $userId. Updating lastPing in database.")
                    mealDao.updateLastPing(userId, System.currentTimeMillis())
                } else {
                    Log.d("PingDebug", "Ping retry failed for user $userId. Giving up for today.")
                }
            }
        }
    }

    private suspend fun sendPing(userId: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val url = BuildConfig.URL
                val json = JSONObject().apply { put("userId", userId) }.toString()
                val body = json.toRequestBody("application/json".toMediaTypeOrNull())
                val request = Request.Builder()
                    .url(url)
                    .addHeader("Authorization", "Bearer " + BuildConfig.BEARER)
                    .addHeader("Content-Type", "application/json")
                    .post(body)
                    .build()
                Log.d("PingDebug", "Sending POST with userId $userId")
                val response = okHttpClient.newCall(request).execute()
                val responseBody = response.body?.string()
                Log.d("PingDebug", "Ping response: code=${response.code}, body=$responseBody")
                response.use { it.isSuccessful }
            } catch (e: Exception) {
                Log.e("PingDebug", "Ping exception: ${e.message}", e)
                false // Fail silently
            }
        }
    }
}