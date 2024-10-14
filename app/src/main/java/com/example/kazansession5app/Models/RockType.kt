package com.example.kazansession5app.Models

import kotlinx.serialization.Serializable

@Serializable
data class RockType(
    val name: String,
    val backgroundColor: String,
)