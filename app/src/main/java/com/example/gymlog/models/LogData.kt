package com.example.gymlog.models


data class Exercise(
    val name: String,
    val description: String,
    val sets: Int,
)

data class Workout(
    val id: Int,
    val name: String,
    val description: String,
    val exercises: List<Exercise>

)