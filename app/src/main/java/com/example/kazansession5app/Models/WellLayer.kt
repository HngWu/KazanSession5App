package com.example.kazansession5app.Models

import kotlinx.serialization.Serializable

@Serializable
data class WellLayer(
    val id: Int,
    val wellId: Int,
    val rockTypeId: Int,
    val startPoint: Int,
    val endPoint: Int,
    val rockName: String,
    val backgroundColor: String,
)