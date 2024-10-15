package com.example.kazansession5app.Models

import kotlinx.serialization.Serializable

@Serializable
data class Well(
    val id: Int,
    val wellTypeId: Int,
    val wellName: String,
    val gasOilDepth: Int,
    val capacity: Int,
    val wellLayers: List<WellLayer> = listOf(),
    val wellTypeName: String,
)

