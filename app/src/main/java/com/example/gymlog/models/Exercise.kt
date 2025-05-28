package com.example.gymlog.models

import com.example.gymlog.R

data class Exercise(
    val name: String,
    val description: String,
    val sets: Int,
    val reps: Int,
    val weight: Double,
    val exercisePicture: Int
)

val exerciseList = listOf(
    Exercise(
        name = "Supino Reto",
        description = "Exercício de supino reto com barra.",
        sets = 3,
        reps = 12,
        weight = 100.0,
        exercisePicture = R.drawable.supino
    ),

    Exercise(
        name = "Rosca Direta",
        description = "Exercício de rosca direta com barra.",
        sets = 3,
        reps = 12,
        weight = 20.0,
        exercisePicture = R.drawable.rosca_direta

    ),

    Exercise(
        name = "Agachamento",
        description = "Exercício de agachamento com barra.",
        sets = 3,
        reps = 12,
        weight = 50.0,
        exercisePicture = R.drawable.agachamento
    )
)