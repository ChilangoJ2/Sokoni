package com.jay.sokoni.domain.repository

import com.jay.sokoni.domain.model.Order
import com.jay.sokoni.domain.model.OrderStatus
import kotlinx.coroutines.flow.Flow

interface OrderRepository {
    fun getOrder(orderId: String): Flow<Order?>
    fun getCustomerOrders(customerId: String): Flow<List<Order>>
    fun getVendorOrders(vendorId: String): Flow<List<Order>>
    suspend fun createOrder(order: Order): Result<String> // Returns orderId
    suspend fun updateOrderStatus(orderId: String, status: OrderStatus): Result<Unit>
    suspend fun confirmOrderItem(orderId: String, productId: String, variantName: String, isConfirmed: Boolean): Result<Unit>
    suspend fun assignRider(orderId: String, riderDetails: com.jay.sokoni.domain.model.RiderDetails): Result<Unit>
    suspend fun cancelOrder(orderId: String, reason: String, cancelledBy: String): Result<Unit>
}
