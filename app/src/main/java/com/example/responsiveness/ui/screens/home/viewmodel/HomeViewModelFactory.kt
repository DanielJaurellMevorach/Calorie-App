package com.example.responsiveness.ui.screens.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.responsiveness.database.dao.MealDao

/**
 * Factory for HomeViewModel. Use with ViewModelProvider when HomeViewModel requires constructor parameters.
 * Consider using dependency injection for better testability.
 */
class HomeViewModelFactory(private val mealDao: MealDao) : ViewModelProvider.Factory {
    /**
     * Creates a new instance of HomeViewModel.
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(mealDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
