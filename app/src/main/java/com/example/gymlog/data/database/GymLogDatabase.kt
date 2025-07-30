package com.example.gymlog.data.repositories

import com.example.gymlog.data.database.dao.ExerciseDao
import com.example.gymlog.data.database.dao.ProfileDao
import com.example.gymlog.data.database.dao.WorkoutLogDao
import com.example.gymlog.data.database.dao.WorkoutRoutineDao
import com.example.gymlog.models.WorkoutRoutineExerciseCrossRef
import com.example.gymlog.models.Exercise
import com.example.gymlog.models.ProfileData
import com.example.gymlog.models.UserSettings
import com.example.gymlog.models.WorkoutLogEntryWithExercises
import com.example.gymlog.models.WorkoutRoutineWithExercises
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class WorkoutRepositoryImpl(
    private val workoutRoutineDao: WorkoutRoutineDao,
    private val exerciseDao: ExerciseDao,
    private val workoutLogDao: WorkoutLogDao,
    private val profileDao: ProfileDao,
    private val userPreferencesRepository: UserPreferencesRepository
) : WorkoutRepository {
    override fun getWorkoutRoutines(): Flow<List<WorkoutRoutineWithExercises>> {
        return workoutRoutineDao.getWorkoutRoutinesWithExercises()
    }

    override fun getWorkoutRoutineById(id: Int): Flow<WorkoutRoutineWithExercises?> {
        return workoutRoutineDao.getWorkoutRoutineWithExercisesById(id)
    }

    override fun getFavoriteWorkoutRoutines(): Flow<List<WorkoutRoutineWithExercises>> {
        return workoutRoutineDao.getFavoriteWorkoutRoutinesWithExercises()
    }

    override suspend fun toggleFavorite(routineId: Int) {
        val routine = workoutRoutineDao.getWorkoutRoutineWithExercisesById(routineId).first()
        routine?.let {
            workoutRoutineDao.setFavorite(routineId, !it.routine.isFavorite)
        }
    }

    override suspend fun clearFavorites() {
        workoutRoutineDao.clearFavorites()
    }

    override suspend fun saveWorkoutRoutine(routineWithExercises: WorkoutRoutineWithExercises) {
        workoutRoutineDao.insertRoutine(routineWithExercises.routine)
        // If it's an update, we need to remove the old cross refs first
        workoutRoutineDao.deleteCrossRefsByRoutineId(routineWithExercises.routine.id)
        workoutRoutineDao.insertCrossRefs(routineWithExercises.exercises.map {
            WorkoutRoutineExerciseCrossRef(
                routineId = routineWithExercises.routine.id,
                exerciseId = it.id
            )
        })
    }

    override suspend fun deleteWorkoutRoutine(routineId: Int) {
        workoutRoutineDao.deleteRoutineById(routineId)
    }

    override fun getAllExercises(): Flow<List<Exercise>> {
        return exerciseDao.getAllExercises()
    }

    override fun getExerciseById(id: Int): Flow<Exercise?> {
        return exerciseDao.getExerciseById(id)
    }

    override fun getWorkoutLogs(): Flow<List<WorkoutLogEntryWithExercises>> {
        return workoutLogDao.getWorkoutLogs()
    }

    override fun getWorkoutLogById(id: String): Flow<WorkoutLogEntryWithExercises?> {
        return workoutLogDao.getWorkoutLogById(id)
    }

    override suspend fun saveWorkoutLog(logEntry: WorkoutLogEntryWithExercises) {
        workoutLogDao.insertWorkoutLog(logEntry.logEntry)
        workoutLogDao.insertPerformedExercises(logEntry.performedExercises.map { it.performedExercise })
        workoutLogDao.insertPerformedSets(logEntry.performedExercises.flatMap { it.sets })
    }

    override suspend fun updateWorkoutLog(logEntry: WorkoutLogEntryWithExercises) {
        workoutLogDao.updateWorkoutLog(logEntry.logEntry)
        // This should also update the relations, but for now it's fine.
    }

    override suspend fun deleteWorkoutLog(logId: String) {
        workoutLogDao.deleteWorkoutLog(logId)
    }

    override fun getUserProfile(): Flow<ProfileData?> {
        return profileDao.getUserProfile(1) // Assuming user id is 1
    }

    override suspend fun updateUserProfile(profileData: ProfileData) {
        profileDao.updateProfile(profileData)
    }

    override fun getUserSettings(): Flow<UserSettings> {
        return userPreferencesRepository.userPreferencesFlow.map {
            UserSettings(
                isDarkTheme = it.isDarkTheme,
                notificationsEnabled = it.notificationsEnabled,
                restTimerDuration = it.restTimerDuration
            )
        }
    }

    override suspend fun updateDarkThemeSetting(enabled: Boolean) {
        userPreferencesRepository.updateDarkThemeSetting(enabled)
    }

    override suspend fun updateNotificationsSetting(enabled: Boolean) {
        userPreferencesRepository.updateNotificationsSetting(enabled)
    }

    override suspend fun updateRestTimerDuration(seconds: Int) {
        userPreferencesRepository.updateRestTimerDuration(seconds)
    }

    override suspend fun resetUserSettings() {
        userPreferencesRepository.resetUserSettings()
    }
}