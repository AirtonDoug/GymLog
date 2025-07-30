package com.example.gymlog.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.gymlog.models.PerformedExercise
import com.example.gymlog.models.PerformedSet
import com.example.gymlog.models.WorkoutLogEntry
import com.example.gymlog.models.WorkoutLogEntryWithExercises
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutLogDao {
    @Transaction
    @Query("SELECT * FROM workout_log_entries")
    fun getWorkoutLogs(): Flow<List<WorkoutLogEntryWithExercises>>

    @Transaction
    @Query("SELECT * FROM workout_log_entries WHERE id = :id")
    fun getWorkoutLogById(id: String): Flow<WorkoutLogEntryWithExercises?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkoutLog(logEntry: WorkoutLogEntry)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPerformedExercises(exercises: List<PerformedExercise>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPerformedSets(sets: List<PerformedSet>)

    @Update
    suspend fun updateWorkoutLog(logEntry: WorkoutLogEntry)

    @Query("DELETE FROM workout_log_entries WHERE id = :logId")
    suspend fun deleteWorkoutLog(logId: String)
}
