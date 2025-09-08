package com.example.responsiveness.ui.screens.profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anothercalorieapp.database.UserEntity
import com.example.responsiveness.database.repository.MealRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ProfileViewModel(private val repository: MealRepository) : ViewModel() {
    private val _userSettings = MutableStateFlow<UserEntity?>(null)
    val userSettings: StateFlow<UserEntity?> = _userSettings.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getUser().collectLatest { user ->
                _userSettings.value = user
            }
        }
    }

    fun updateMaxCalories(value: Int) {
        viewModelScope.launch {
            _userSettings.value?.let { repository.updateMaxCalories(it.id, value) }
        }
    }
    fun updateMaxCarbs(value: Int) {
        viewModelScope.launch {
            _userSettings.value?.let { repository.updateMaxCarbs(it.id, value) }
        }
    }
    fun updateMaxProtein(value: Int) {
        viewModelScope.launch {
            _userSettings.value?.let { repository.updateMaxProtein(it.id, value) }
        }
    }
    fun updateMaxFat(value: Int) {
        viewModelScope.launch {
            _userSettings.value?.let { repository.updateMaxFat(it.id, value) }
        }
    }

    fun updateApiKey(value: String) {
        viewModelScope.launch {
            _userSettings.value?.let { repository.updateApiKey(it.id, value) }
        }
    }

    fun submitApiKey(value: String) {
        viewModelScope.launch {
            _userSettings.value?.let { repository.updateApiKey(it.id, value) }
        }
    }
}
