package com.example.caloriecounter.ui.components.openai

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class NutritionResultsViewModel : ViewModel() {
    private val _nutritionData = MutableStateFlow<String?>(null)
    val nutritionData: StateFlow<String?> = _nutritionData.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun startLoading() {
        _isLoading.value = true
        _nutritionData.value = null
    }

    fun updateNutritionData(data: String) {
        _nutritionData.value = data
        _isLoading.value = false
    }

    fun reset() {
        _nutritionData.value = null
        _isLoading.value = false
    }
}
