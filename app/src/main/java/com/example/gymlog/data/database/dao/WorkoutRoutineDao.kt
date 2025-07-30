package com.example.gymlog.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.gymlog.models.WorkoutRoutine
import com.example.gymlog.models.WorkoutRoutineExerciseCrossRef
import com.example.gymlog.models.WorkoutRoutineWithExercises
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutRoutineDao {
    @Transaction
    @Query("SELECT * FROM workout_routines")
    fun getWorkoutRoutinesWithExercises(): Flow<List<WorkoutRoutineWithExercises>>

    @Transaction
    @Query("SELECT * FROM workout_routines WHERE id = :id")
    fun getWorkoutRoutineWithExercisesById(id: Int): Flow<WorkoutRoutineWithExercises?>

    @Transaction
    @Query("SELECT * FROM workout_routines WHERE isFavorite = 1")
    fun getFavoriteWorkoutRoutinesWithExercises(): Flow<List<WorkoutRoutineWithExercises>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutines(routines: List<WorkoutRoutine>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutine(routine: WorkoutRoutine)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCrossRefs(crossRefs: List<WorkoutRoutineExerciseCrossRef>)

    @Query("DELETE FROM workout_routine_exercise_cross_ref WHERE routineId = :routineId")
    suspend fun deleteCrossRefsByRoutineId(routineId: Int)

    @Query("UPDATE workout_routines SET isFavorite = :isFavorite WHERE id = :routineId")
    suspend fun setFavorite(routineId: Int, isFavorite: Boolean)

    @Query("UPDATE workout_routines SET isFavorite = 0")
    suspend fun clearFavorites()

    @Query("DELETE FROM workout_routines WHERE id = :routineId")
    suspend fun deleteRoutineById(routineId: Int)
}
