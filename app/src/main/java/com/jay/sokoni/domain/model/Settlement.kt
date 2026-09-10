package com.jay.sokoni.domain.model

enum class SettlementStatus {
    PENDING, CALCULATED, PROCESSING, SETTLED, FAILED, REVERSED
}

enum class SettlementStrategy {
    SOKONI_ORCHESTRATED, PROVIDER_SPLIT
}

enum class PaymentFeeMode {
    ABSORB_BY_SOKONI, PASS_TO_VENDOR, PASS_TO_CUSTOMER
}

data class FinancialLedgerEntry(
    val id: String = "",
    val orderId: String = "",
    val customerId: String = "",
    val vendorId: String = "",
    
    // Amounts
    val productSubtotal: Double = 0.0,
    val deliveryFee: Double = 0.0,
    val customerTotal: Double = 0.0,
    
    // Commission
    val commissionRateAtOrder: Double = 0.0,
    val commissionAmount: Double = 0.0,
    
    // Delivery Margin
    val deliveryMarginRateAtOrder: Double = 0.0,
    val deliveryMarginAmount: Double = 0.0,
    
    // Payment
    val paymentProvider: PaymentProvider = PaymentProvider.MPESA,
    val paymentFee: Double = 0.0,
    val paymentFeeMode: PaymentFeeMode = PaymentFeeMode.PASS_TO_VENDOR,
    
    // Vendor Settlement
    val vendorGrossAmount: Double = 0.0,
    val vendorNetSettlement: Double = 0.0,
    val sokoniRevenue: Double = 0.0,
    
    // Statuses
    val paymentStatus: PaymentStatus = PaymentStatus.PENDING,
    val settlementStatus: SettlementStatus = SettlementStatus.PENDING,
    
    val createdAt: Long = 0L,
    val paidAt: Long? = null,
    val settledAt: Long? = null
)
