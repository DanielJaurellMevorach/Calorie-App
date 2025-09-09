package com.example.responsiveness.ui.screens.analytics.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.responsiveness.database.dao.MealDao

/**
 * Factory for creating AnalyticsViewModel instances with proper dependency injection
 */
class AnalyticsViewModelFactory(private val mealDao: MealDao) : ViewModelProvider.Factory {
    /**
     * Creates a new instance of AnalyticsViewModel.
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AnalyticsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AnalyticsViewModel(mealDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
