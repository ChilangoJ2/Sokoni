package com.jay.sokoni.domain.model

import java.util.Date

enum class UserRole {
    CUSTOMER, VENDOR, SUPER_ADMIN
}

enum class UserStatus {
    ACTIVE, PENDING, BLOCKED
}

data class User(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val role: UserRole = UserRole.CUSTOMER,
    val status: UserStatus = UserStatus.PENDING,
    val profileImageUrl: String = "",
    val location: UserLocation? = null,
    val createdAt: Long = Date().time,
    val updatedAt: Long = Date().time
)
