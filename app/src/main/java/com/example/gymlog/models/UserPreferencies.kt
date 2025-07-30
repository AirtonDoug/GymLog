package com.example.gymlog.models

data class UserPreferences(
    val isDarkTheme: Boolean = false,
    val notificationsEnabled: Boolean = true,
    val restTimerDuration: Int = 90 // Default rest timer duration in seconds
)