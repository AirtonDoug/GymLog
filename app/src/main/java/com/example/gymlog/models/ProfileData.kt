package com.example.gymlog.models

import com.example.gymlog.R

data class ProfileData(
    val id: Int,
    val name: String,
    val height: Double,
    val weight: Double,
    val profilePicture: Int
)

val profileData = ProfileData(
    id = 1,
    name = "Julia Oliveira",
    height = 170.0,
    weight = 75.0,
    profilePicture = R.drawable.profile
)