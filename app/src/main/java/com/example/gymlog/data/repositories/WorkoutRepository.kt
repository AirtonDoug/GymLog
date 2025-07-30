package com.example.gymlog.data.repositories

import com.example.gymlog.models.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import java.util.Date
import java.util.UUID

/**
 * Extended interface for accessing workout data including logs
 */
interface WorkoutRepository {
    // Workout Routines
    fun getWorkoutRoutines(): Flow<List<WorkoutRoutineWithExercises>>
    fun getWorkoutRoutineById(id: Int): Flow<WorkoutRoutineWithExercises?>
    fun getFavoriteWorkoutRoutines(): Flow<List<WorkoutRoutineWithExercises>>
    suspend fun toggleFavorite(routineId: Int)
    suspend fun clearFavorites()
    suspend fun saveWorkoutRoutine(routineWithExercises: WorkoutRoutineWithExercises)
    suspend fun deleteWorkoutRoutine(routineId: Int)

    // Exercises
    fun getAllExercises(): Flow<List<Exercise>>
    fun getExerciseById(id: Int): Flow<Exercise?>

    // Workout Logs
    fun getWorkoutLogs(): Flow<List<WorkoutLogEntryWithExercises>>
    fun getWorkoutLogById(id: String): Flow<WorkoutLogEntryWithExercises?>
    suspend fun saveWorkoutLog(logEntry: WorkoutLogEntryWithExercises)
    suspend fun updateWorkoutLog(logEntry: WorkoutLogEntryWithExercises)
    suspend fun deleteWorkoutLog(logId: String)

    // User Profile
    fun getUserProfile(): Flow<ProfileData?>
    suspend fun updateUserProfile(profileData: ProfileData)

    // User Settings
    fun getUserSettings(): Flow<UserSettings>
    suspend fun updateDarkThemeSetting(enabled: Boolean)
    suspend fun updateNotificationsSetting(enabled: Boolean)
    suspend fun updateRestTimerDuration(seconds: Int)
    suspend fun resetUserSettings()
}

/**
 * User settings data class
 */
data class UserSettings(
    val isDarkTheme: Boolean = false,
    val notificationsEnabled: Boolean = true,
    val restTimerDuration: Int = 60
)

/**
 * Mock implementation of WorkoutRepository using in-memory data.
 */
class MockWorkoutRepository : WorkoutRepository {

    // Workout Routines
    private val _routines = MutableStateFlow(mockWorkoutRoutinesWithExercises)
    private val _favoriteIds = MutableStateFlow(
        mockWorkoutRoutinesWithExercises.filter { it.routine.isFavorite }.map { it.routine.id }.toSet()
    )

    // Exercises
    private val _exercises = MutableStateFlow(exerciseList)

    // Workout Logs
    private val _workoutLogs = MutableStateFlow<List<WorkoutLogEntryWithExercises>>(emptyList())


    init {
        val logId = UUID.randomUUID().toString()
        val performedExerciseId = UUID.randomUUID().toString()
        _workoutLogs.value = listOf(
            WorkoutLogEntryWithExercises(
                logEntry = WorkoutLogEntry(
                    id = logId,
                    routineId = 1,
                    workoutName = "Treino Full Body",
                    startTime = Date(System.currentTimeMillis() - 86400000), // Yesterday
                    endTime = Date(System.currentTimeMillis() - 86400000 + 3600000), // 1 hour later
                    durationMillis = 3600000,
                    notes = "Bom treino, consegui completar todas as séries.",
                    caloriesBurned = 450
                ),
                performedExercises = listOf(
                    PerformedExerciseWithSets(
                        performedExercise = PerformedExercise(
                            id = performedExerciseId,
                            workoutLogId = logId,
                            exerciseId = 1,
                            exerciseName = "Supino Reto",
                            targetSets = 3,
                            targetReps = 12,
                            targetWeight = 100.0
                        ),
                        sets = listOf(
                            PerformedSet(performedExerciseId = performedExerciseId, reps = 12, weight = 100.0, isCompleted = true),
                            PerformedSet(performedExerciseId = performedExerciseId, reps = 10, weight = 100.0, isCompleted = true),
                            PerformedSet(performedExerciseId = performedExerciseId, reps = 8, weight = 100.0, isCompleted = true)
                        )
                    )
                )
            )
        )
    }


    // User Profile
    private val _userProfile = MutableStateFlow(profileData)

    // User Settings
    private val _userSettings = MutableStateFlow(UserSettings())

    // Workout Routines Implementation
    override fun getWorkoutRoutines(): Flow<List<WorkoutRoutineWithExercises>> {
        return _routines.combine(_favoriteIds) { routines, favIds ->
            routines.map { routineWithExercises ->
                routineWithExercises.copy(
                    routine = routineWithExercises.routine.copy(
                        isFavorite = routineWithExercises.routine.id in favIds
                    )
                )
            }
        }
    }

    override fun getWorkoutRoutineById(id: Int): Flow<WorkoutRoutineWithExercises?> {
        return getWorkoutRoutines().map { routines -> routines.find { it.routine.id == id } }
    }

    override fun getFavoriteWorkoutRoutines(): Flow<List<WorkoutRoutineWithExercises>> {
        return getWorkoutRoutines().map { routines -> routines.filter { it.routine.isFavorite } }
    }

    override suspend fun toggleFavorite(routineId: Int) {
        _favoriteIds.update { currentIds ->
            if (routineId in currentIds) {
                currentIds - routineId
            } else {
                currentIds + routineId
            }
        }
        _routines.update { routines ->
            routines.map { routineWithExercises ->
                if (routineWithExercises.routine.id == routineId) {
                    routineWithExercises.copy(
                        routine = routineWithExercises.routine.copy(
                            isFavorite = !routineWithExercises.routine.isFavorite
                        )
                    )
                } else {
                    routineWithExercises
                }
            }
        }
    }

    override suspend fun clearFavorites() {
        _favoriteIds.value = emptySet()
    }

    override suspend fun saveWorkoutRoutine(routineWithExercises: WorkoutRoutineWithExercises) {
        val currentRoutines = _routines.value.toMutableList()
        val index = currentRoutines.indexOfFirst { it.routine.id == routineWithExercises.routine.id }
        if (index != -1) {
            currentRoutines[index] = routineWithExercises
        } else {
            currentRoutines.add(routineWithExercises)
        }
        _routines.value = currentRoutines
    }

    override suspend fun deleteWorkoutRoutine(routineId: Int) {
        _routines.update { routines -> routines.filter { it.routine.id != routineId } }
    }

    // Exercises Implementation
    override fun getAllExercises(): Flow<List<Exercise>> {
        return _exercises
    }

    override fun getExerciseById(id: Int): Flow<Exercise?> {
        return _exercises.map { exercises -> exercises.find { it.id == id } }
    }

    // Workout Logs Implementation
    override fun getWorkoutLogs(): Flow<List<WorkoutLogEntryWithExercises>> {
        return _workoutLogs
    }

    override fun getWorkoutLogById(id: String): Flow<WorkoutLogEntryWithExercises?> {
        return _workoutLogs.map { logs -> logs.find { it.logEntry.id == id } }
    }

    override suspend fun saveWorkoutLog(logEntry: WorkoutLogEntryWithExercises) {
        _workoutLogs.update { currentLogs -> currentLogs + logEntry }
    }

    override suspend fun updateWorkoutLog(logEntry: WorkoutLogEntryWithExercises) {
        _workoutLogs.update { currentLogs ->
            currentLogs.map {
                if (it.logEntry.id == logEntry.logEntry.id) {
                    logEntry
                } else {
                    it
                }
            }
        }
    }

    override suspend fun deleteWorkoutLog(logId: String) {
        _workoutLogs.update { currentLogs -> currentLogs.filter { it.logEntry.id != logId } }
    }

    // User Profile Implementation
    override fun getUserProfile(): Flow<ProfileData> {
        return _userProfile
    }

    override suspend fun updateUserProfile(profileData: ProfileData) {
        _userProfile.value = profileData
    }

    // User Settings Implementation
    override fun getUserSettings(): Flow<UserSettings> {
        return _userSettings
    }

    override suspend fun updateDarkThemeSetting(enabled: Boolean) {
        _userSettings.update { it.copy(isDarkTheme = enabled) }
    }

    override suspend fun updateNotificationsSetting(enabled: Boolean) {
        _userSettings.update { it.copy(notificationsEnabled = enabled) }
    }

    override suspend fun updateRestTimerDuration(seconds: Int) {
        _userSettings.update { it.copy(restTimerDuration = seconds) }
    }

    override suspend fun resetUserSettings() {
        _userSettings.value = UserSettings()
    }
}

// Helper function for combining flows
fun <T1, T2, R> Flow<T1>.combine(flow: Flow<T2>, transform: suspend (T1, T2) -> R): Flow<R> {
    return kotlinx.coroutines.flow.combine(this, flow, transform)
}
