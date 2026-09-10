package com.jay.sokoni.domain.model

data class UserLocation(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val accuracy: Float = 0f,
    val timestamp: Long = 0L,
    val address: String = "",
    val building: String = "",
    val houseNumber: String = "",
    val landmark: String = "",
    val deliveryNotes: String = ""
)
