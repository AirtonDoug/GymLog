package com.example.gymlog.models





data class Workout(
    val id: Int,
    val name: String,
    val exercises: List<Exercise>
)