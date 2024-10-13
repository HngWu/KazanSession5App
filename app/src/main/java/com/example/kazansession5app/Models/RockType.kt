package com.example.kazansession5app.Models

import kotlinx.serialization.Serializable

@Serializable
data class RockType(
    val id: Int,
    val name: String,
    val backgroundColor: String,
)