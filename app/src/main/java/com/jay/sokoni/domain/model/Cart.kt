package com.jay.sokoni.domain.model

data class CartItem(
    val productId: String = "",
    val productName: String = "",
    val variantName: String = "",
    val price: Double = 0.0,
    val quantity: Int = 1,
    val imageUrl: String = ""
)

data class Cart(
    val vendorId: String = "",
    val vendorName: String = "",
    val items: List<CartItem> = emptyList(),
    val isDelivery: Boolean = true,
    val deliveryLocation: UserLocation? = null
) {
    val subtotal: Double get() = items.sumOf { it.price * it.quantity }
}
