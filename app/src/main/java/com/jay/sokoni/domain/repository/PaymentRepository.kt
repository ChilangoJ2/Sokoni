package com.jay.sokoni.domain.repository

import com.jay.sokoni.domain.model.Payment
import kotlinx.coroutines.flow.Flow

interface PaymentRepository {
    fun getPayment(paymentId: String): Flow<Payment?>
    suspend fun initiateMpesaPush(orderId: String, phoneNumber: String, amount: Double): Result<String> // Returns paymentId
    suspend fun verifyPayment(paymentId: String): Result<Payment>
}
