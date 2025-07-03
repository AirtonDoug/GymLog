package com.example.gymlog.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymlog.data.repositories.UserPreferences
import com.example.gymlog.data.repositories.UserPreferencesRepository
import com.example.gymlog.data.repositories.WorkoutRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    application: Application,
    private val workoutRepository: WorkoutRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : AndroidViewModel(application) {

    val userPreferencesFlow: StateFlow<UserPreferences> =
        userPreferencesRepository.userPreferencesFlow.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserPreferences(isDarkMode = false, notificationsEnabled = true)
        )

    fun setDarkMode(isDarkMode: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.updateDarkMode(isDarkMode)
        }
    }

    fun setNotificationsEnabled(notificationsEnabled: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.updateNotificationsEnabled(notificationsEnabled)
        }
    }

    fun clearFavorites() {
        viewModelScope.launch {
            workoutRepository.clearFavorites()
        }
    }
}