package com.jay.sokoni.domain.model

enum class OrderStatus {
    PENDING, ACCEPTED, PREPARING, READY, OUT_FOR_DELIVERY, COMPLETED, CANCELLED
}

enum class DeliveryStatus {
    PENDING, READY_FOR_DELIVERY, OUT_FOR_DELIVERY, DELIVERED
}

enum class PickupStatus {
    PENDING, READY_FOR_COLLECTION, COLLECTED
}

data class OrderItem(
    val productId: String = "",
    val productName: String = "",
    val variantName: String = "",
    val unitPrice: Double = 0.0,
    val quantity: Int = 1,
    val lineTotal: Double = 0.0,
    val isConfirmedByVendor: Boolean = false
)

data class RiderDetails(
    val name: String = "",
    val phone: String = "",
    val vehicleRegistration: String = "",
    val imageUrl: String? = null
)

data class Order(
    val orderId: String = "",
    val customerId: String = "",
    val vendorId: String = "",
    val vendorName: String = "",
    
    val items: List<OrderItem> = emptyList(),
    
    // Financial Snapshot (Immutable after creation)
    val subtotal: Double = 0.0,
    val deliveryFee: Double = 0.0,
    val totalAmount: Double = 0.0,
    
    // Fulfillment
    val isDelivery: Boolean = true,
    val deliveryLocation: UserLocation? = null,
    
    // Statuses
    val status: OrderStatus = OrderStatus.PENDING,
    val paymentStatus: PaymentStatus = PaymentStatus.PENDING,
    val settlementStatus: SettlementStatus = SettlementStatus.PENDING,
    val deliveryStatus: DeliveryStatus? = null,
    val pickupStatus: PickupStatus? = null,
    
    // Delivery Logistics
    val riderDetails: RiderDetails? = null,
    val estimatedArrivalTime: Long? = null,
    
    // Cancellation
    val cancellationReason: String? = null,
    val cancelledBy: String? = null, // "VENDOR" or "CUSTOMER" or "SYSTEM"

    val createdAt: Long = 0L,
    val updatedAt: Long = 0L,
    val acceptedAt: Long? = null,
    val preparedAt: Long? = null,
    val dispatchedAt: Long? = null,
    val completedAt: Long? = null
)
