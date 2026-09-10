package com.jay.sokoni.domain.repository

import com.jay.sokoni.domain.model.Cart
import com.jay.sokoni.domain.model.CartItem
import kotlinx.coroutines.flow.Flow

interface CartRepository {
    fun getCart(): Flow<Cart?>
    suspend fun addToCart(vendorId: String, vendorName: String, item: CartItem): Result<Unit>
    suspend fun removeFromCart(productId: String, variantName: String): Result<Unit>
    suspend fun updateQuantity(productId: String, variantName: String, quantity: Int): Result<Unit>
    suspend fun clearCart(): Result<Unit>
    suspend fun setDeliveryMode(isDelivery: Boolean): Result<Unit>
}
