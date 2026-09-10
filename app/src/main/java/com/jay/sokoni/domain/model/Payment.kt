package com.jay.sokoni.domain.model

enum class PaymentStatus {
    PENDING, PROCESSING, PAID, FAILED, REFUNDED, PARTIALLY_REFUNDED
}

enum class PaymentProvider {
    MPESA, PAYSTACK, PESAPAL, FLUTTERWAVE
}

data class Payment(
    val paymentId: String = "",
    val orderId: String = "",
    val customerId: String = "",
    val amount: Double = 0.0,
    val provider: PaymentProvider = PaymentProvider.MPESA,
    val status: PaymentStatus = PaymentStatus.PENDING,
    val providerReference: String? = null,
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L
)
