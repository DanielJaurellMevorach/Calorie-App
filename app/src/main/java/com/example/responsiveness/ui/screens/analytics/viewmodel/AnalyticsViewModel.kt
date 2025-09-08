package com.example.responsiveness.ui.screens.analytics.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anothercalorieapp.database.MealWithDetails
import com.example.responsiveness.database.dao.MealDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.Calendar
import java.util.Locale

/**
 * ViewModel for Analytics screen. Manages calendar calories and all meals data.
 */
class AnalyticsViewModel(private val mealDao: MealDao) : ViewModel() {
    private val _calendarCaloriesByDate = MutableStateFlow<Map<LocalDate, String>>(emptyMap())
    val calendarCaloriesByDate: StateFlow<Map<LocalDate, String>> = _calendarCaloriesByDate.asStateFlow()

    private val _allMeals = MutableStateFlow<List<MealWithDetails>>(emptyList())
    val allMeals: StateFlow<List<MealWithDetails>> = _allMeals.asStateFlow()

    init {
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
     * Fetches calories for the last 7 days and updates calendarCaloriesByDate.
     */
    private fun fetchCalendarCalories() {
        val daysCount = 7
        val now = Calendar.getInstance()
        val startCal = now.clone() as Calendar
        startCal.add(Calendar.DAY_OF_YEAR, -(daysCount - 1))
        startCal.set(Calendar.HOUR_OF_DAY, 0)
        startCal.set(Calendar.MINUTE, 0)
        startCal.set(Calendar.SECOND, 0)
        startCal.set(Calendar.MILLISECOND, 0)
        val startMillis = startCal.timeInMillis
        val endCal = now.clone() as Calendar
        endCal.set(Calendar.HOUR_OF_DAY, 23)
        endCal.set(Calendar.MINUTE, 59)
        endCal.set(Calendar.SECOND, 59)
        endCal.set(Calendar.MILLISECOND, 999)
        val endMillis = endCal.timeInMillis
        viewModelScope.launch {
            mealDao.getMealsWithDetailsForDateRangeFlow(startMillis, endMillis).collect { meals ->
                val caloriesByDay = MutableList(daysCount) { 0.0 }
                for (meal in meals) {
                    val mealCal = Calendar.getInstance().apply { timeInMillis = meal.meal.created_at }
                    val dayDiff = ((mealCal.timeInMillis - startCal.timeInMillis) / (1000 * 60 * 60 * 24)).toInt()
                    if (dayDiff in 0 until daysCount) {
                        caloriesByDay[dayDiff] += (meal.nutrition?.energy_kcal ?: 0.0) * (meal.meal.quantity ?: 1.0)
                    }
                }
                val dateCaloriesMap = mutableMapOf<LocalDate, String>()
                for (i in 0 until daysCount) {
                    val date = LocalDate.now().minusDays((daysCount - 1 - i).toLong())
                    val calVal = caloriesByDay[i]
                    val calStr = if (calVal % 1.0 == 0.0) calVal.toInt().toString() else String.format(Locale.getDefault(), "%.1f", calVal)
                    dateCaloriesMap[date] = calStr
                }
                _calendarCaloriesByDate.value = dateCaloriesMap
            }
        }
    }
}
