package com.jay.sokoni.domain.model

data class Rating(
    val id: String = "",
    val customerId: String = "",
    val vendorId: String = "",
    val orderId: String = "",
    val rating: Int = 0, // 1-5
    val review: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
