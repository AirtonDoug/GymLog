package com.example.gymlog.data.repositories

import com.example.gymlog.models.UserPreferences
import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    val userPreferencesFlow: Flow<UserPreferences>

    suspend fun updateDarkThemeSetting(enabled: Boolean)
    suspend fun updateNotificationsSetting(enabled: Boolean)
    suspend fun updateRestTimerDuration(seconds: Int)
    suspend fun resetUserSettings()
}