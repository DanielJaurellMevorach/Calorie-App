package com.example.anothercalorieapp.ui.components.scanner.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.responsiveness.database.repository.MealRepository

class MealDetailsViewModelFactory(
    private val mealRepository: MealRepository,
    private val mealId: Int
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MealDetailsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MealDetailsViewModel(mealRepository, mealId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
