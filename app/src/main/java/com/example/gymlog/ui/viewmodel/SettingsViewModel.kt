package com.example.gymlog.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymlog.data.repositories.UserSettings
import com.example.gymlog.data.repositories.WorkoutRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// UI State for Settings Screen
data class SettingsUiState(
    val isDarkTheme: Boolean = false,
    val notificationsEnabled: Boolean = true,
    val restTimerDuration: Int = 60,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val actionSuccess: Boolean = false
)

class SettingsViewModel(private val workoutRepository: WorkoutRepository) : ViewModel() {

    // Mutable state for settings
    private val _uiState = MutableStateFlow(
        SettingsUiState(
            isLoading = true
        )
    )

    // Expose UI state as StateFlow
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            try {
                workoutRepository.getUserSettings().collect { settings ->
                    _uiState.update {
                        it.copy(
                            isDarkTheme = settings.isDarkTheme,
                            notificationsEnabled = settings.notificationsEnabled,
                            restTimerDuration = settings.restTimerDuration,
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Failed to load settings: ${e.message}"
                    )
                }
            }
        }
    }

    // Toggle dark theme
    fun toggleDarkTheme() {
        val newValue = !_uiState.value.isDarkTheme
        _uiState.update { it.copy(isDarkTheme = newValue) }

        viewModelScope.launch {
            try {
                workoutRepository.updateDarkThemeSetting(newValue)
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Failed to update theme setting: ${e.message}") }
            }
        }
    }

    // Toggle notifications
    fun toggleNotifications() {
        val newValue = !_uiState.value.notificationsEnabled
        _uiState.update { it.copy(notificationsEnabled = newValue) }

        viewModelScope.launch {
            try {
                workoutRepository.updateNotificationsSetting(newValue)
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Failed to update notifications setting: ${e.message}") }
            }
        }
    }

    // Update rest timer duration
    fun updateRestTimerDuration(seconds: Int) {
        _uiState.update { it.copy(restTimerDuration = seconds) }

        viewModelScope.launch {
            try {
                workoutRepository.updateRestTimerDuration(seconds)
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Failed to update timer duration: ${e.message}") }
            }
        }
    }

    // Clear all favorites
    fun clearFavorites() {
        viewModelScope.launch {
            try {
                workoutRepository.clearFavorites()
                _uiState.update { it.copy(actionSuccess = true) }
                // Reset success flag after a delay
                kotlinx.coroutines.delay(2000)
                _uiState.update { it.copy(actionSuccess = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Failed to clear favorites: ${e.message}") }
            }
        }
    }

    // Reset all preferences to defaults
    fun resetPreferences() {
        viewModelScope.launch {
            try {
                workoutRepository.resetUserSettings()
                loadSettings() // Reload settings after reset
                _uiState.update { it.copy(actionSuccess = true) }
                // Reset success flag after a delay
                kotlinx.coroutines.delay(2000)
                _uiState.update { it.copy(actionSuccess = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Failed to reset preferences: ${e.message}") }
            }
        }
    }
}

// Factory for creating SettingsViewModel
class SettingsViewModelFactory(private val repository: WorkoutRepository) : androidx.lifecycle.ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            return SettingsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
