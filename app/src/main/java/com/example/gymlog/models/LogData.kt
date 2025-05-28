package com.example.gymlog.models





data class Workout(
    val id: Int,
    val name: String,
    val exercises: List<Exercise>
)

val workoutList = listOf(
    Workout(
        id = 1,
        name = "Full Body Workout",
        exercises = exerciseList
    )
)