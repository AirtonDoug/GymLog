package com.example.gymlog.data.database.entities

import androidx.room.Entity

@Entity(
    tableName = "workout_routine_exercise_cross_ref",
    primaryKeys = ["routineId", "exerciseId"]
)
data class WorkoutRoutineExerciseCrossRef(
    val routineId: Int,
    val exerciseId: Int
)