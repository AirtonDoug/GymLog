package com.example.gymlog.data.repositories

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.gymlog.models.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreferencesRepositoryImpl(
    private val dataStore: DataStore<Preferences>
) : UserPreferencesRepository {

    companion object {
        private val DARK_THEME_KEY = booleanPreferencesKey("dark_theme")
        private val NOTIFICATIONS_KEY = booleanPreferencesKey("notifications")
        private val REST_TIMER_DURATION_KEY = intPreferencesKey("rest_timer_duration")
    }

    override val userPreferencesFlow: Flow<UserPreferences> = dataStore.data
        .map { preferences ->
            UserPreferences(
                isDarkTheme = preferences[DARK_THEME_KEY] ?: false,
                notificationsEnabled = preferences[NOTIFICATIONS_KEY] ?: true,
                restTimerDuration = preferences[REST_TIMER_DURATION_KEY] ?: 90
            )
        }

    override suspend fun updateDarkThemeSetting(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[DARK_THEME_KEY] = enabled
        }
    }

    override suspend fun updateNotificationsSetting(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[NOTIFICATIONS_KEY] = enabled
        }
    }

    override suspend fun updateRestTimerDuration(seconds: Int) {
        dataStore.edit { preferences ->
            preferences[REST_TIMER_DURATION_KEY] = seconds
        }
    }

    override suspend fun resetUserSettings() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}