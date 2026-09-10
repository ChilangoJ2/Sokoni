package com.jay.sokoni.domain.repository

import com.jay.sokoni.domain.model.Order
import kotlinx.coroutines.flow.Flow

interface OrderRepository {
    fun getOrder(orderId: String): Flow<Order?>
    fun getCustomerOrders(customerId: String): Flow<List<Order>>
    fun getVendorOrders(vendorId: String): Flow<List<Order>>
    suspend fun createOrder(order: Order): Result<String> // Returns orderId
    suspend fun updateOrderStatus(orderId: String, status: String): Result<Unit>
}
