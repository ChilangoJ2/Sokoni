package com.jay.sokoni.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import com.jay.sokoni.domain.model.Payment
import com.jay.sokoni.domain.repository.PaymentRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PaymentRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val functions: FirebaseFunctions
) : PaymentRepository {

    override fun getPayment(paymentId: String): Flow<Payment?> = callbackFlow {
        val listener = firestore.collection("payments").document(paymentId)
            .addSnapshotListener { snapshot, _ ->
                trySend(snapshot?.toObject(Payment::class.java))
            }
        awaitClose { listener.remove() }
    }

    override suspend fun initiateMpesaPush(
        orderId: String,
        phoneNumber: String,
        amount: Double
    ): Result<String> {
        return try {
            val data = hashMapOf(
                "orderId" to orderId,
                "phoneNumber" to phoneNumber,
                "amount" to amount
            )
            
            // Call Cloud Function to handle secure M-Pesa API communication
            val result = functions
                .getHttpsCallable("initiateMpesaPush")
                .call(data)
                .await()
            
            val paymentId = (result.data as? Map<*, *>)?.get("paymentId") as? String
                ?: return Result.failure(Exception("No payment ID returned"))
            
            Result.success(paymentId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun verifyPayment(paymentId: String): Result<Payment> {
        // Backend handles verification via callback, but we can manually poll if needed
        return try {
            val doc = firestore.collection("payments").document(paymentId).get().await()
            val payment = doc.toObject(Payment::class.java) ?: return Result.failure(Exception("Payment not found"))
            Result.success(payment)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
