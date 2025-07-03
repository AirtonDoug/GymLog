package com.example.gymlog.data.repositories

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class UserPreferencesRepository(context: Context) {

    private val dataStore = context.dataStore

    private object PreferencesKeys {
        val DARK_MODE = booleanPreferencesKey("dark_mode")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
    }

    val userPreferencesFlow: Flow<UserPreferences> = dataStore.data
        .map { preferences ->
            val isDarkMode = preferences[PreferencesKeys.DARK_MODE] ?: false
            val notificationsEnabled = preferences[PreferencesKeys.NOTIFICATIONS_ENABLED] ?: true
            UserPreferences(isDarkMode, notificationsEnabled)
        }

    suspend fun updateDarkMode(isDarkMode: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.DARK_MODE] = isDarkMode
        }
    }

    suspend fun updateNotificationsEnabled(notificationsEnabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.NOTIFICATIONS_ENABLED] = notificationsEnabled
        }
    }
}

data class UserPreferences(
    val isDarkMode: Boolean,
    val notificationsEnabled: Boolean
)