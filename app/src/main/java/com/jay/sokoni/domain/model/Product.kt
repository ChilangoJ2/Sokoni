package com.jay.sokoni.domain.model

data class ProductVariant(
    val name: String = "", // e.g. "REFILL", "3KG"
    val price: Double = 0.0,
    val stock: Int? = null, // null if infinite
    val isActive: Boolean = true
)

data class ProductAttribute(
    val key: String = "", // e.g. "Brand"
    val value: String = "" // e.g. "MJC Water"
)

data class Product(
    val productId: String = "",
    val vendorId: String = "",
    val name: String = "",
    val description: String = "",
    val category: String = "",
    val brand: String = "",
    val imageUrls: List<String> = emptyList(),
    val variants: List<ProductVariant> = emptyList(),
    val attributes: List<ProductAttribute> = emptyList(),
    val isActive: Boolean = true,
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L
)

data class Category(
    val id: String = "",
    val name: String = "",
    val imageUrl: String = ""
)
