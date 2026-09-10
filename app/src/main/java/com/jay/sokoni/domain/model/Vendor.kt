package com.jay.sokoni.domain.model

enum class VendorStatus {
    DRAFT, SUBMITTED, UNDER_REVIEW, APPROVED, REJECTED, SUSPENDED
}

data class Vendor(
    val vendorId: String = "",
    val storeName: String = "",
    val businessName: String = "",
    val description: String = "",
    val phone: String = "",
    val email: String = "",
    val category: String = "",
    val location: UserLocation? = null,
    val storeImageUrl: String = "",
    val kycDocuments: Map<String, String> = emptyMap(), // documentName -> imageUrl
    val ownerDetails: Map<String, String> = emptyMap(),
    val payoutProfile: Map<String, String> = emptyMap(),
    val status: VendorStatus = VendorStatus.DRAFT,
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L
)
