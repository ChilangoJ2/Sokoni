package com.jay.sokoni.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import com.jay.sokoni.domain.model.Order
import com.jay.sokoni.domain.repository.OrderRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrderRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val functions: FirebaseFunctions
) : OrderRepository {

    override fun getOrder(orderId: String): Flow<Order?> = callbackFlow {
        val listener = firestore.collection("orders").document(orderId)
            .addSnapshotListener { snapshot, _ ->
                trySend(snapshot?.toObject(Order::class.java))
            }
        awaitClose { listener.remove() }
    }

    override fun getCustomerOrders(customerId: String): Flow<List<Order>> = callbackFlow {
        val listener = firestore.collection("orders")
            .whereEqualTo("customerId", customerId)
            .addSnapshotListener { snapshot, _ ->
                trySend(snapshot?.toObjects(Order::class.java) ?: emptyList())
            }
        awaitClose { listener.remove() }
    }

    override fun getVendorOrders(vendorId: String): Flow<List<Order>> = callbackFlow {
        val listener = firestore.collection("orders")
            .whereEqualTo("vendorId", vendorId)
            .addSnapshotListener { snapshot, _ ->
                trySend(snapshot?.toObjects(Order::class.java) ?: emptyList())
            }
        awaitClose { listener.remove() }
    }

    override suspend fun createOrder(order: Order): Result<String> {
        return try {
            // Validation and pricing logic MUST happen on backend
            val data = hashMapOf(
                "vendorId" to order.vendorId,
                "items" to order.items.map { 
                    hashMapOf(
                        "productId" to it.productId,
                        "variantName" to it.variantName,
                        "quantity" to it.quantity
                    )
                },
                "isDelivery" to order.isDelivery,
                "deliveryLocation" to order.deliveryLocation
            )

            val result = functions
                .getHttpsCallable("createOrder")
                .call(data)
                .await()

            val orderId = (result.data as? Map<*, *>)?.get("orderId") as? String
                ?: return Result.failure(Exception("Order creation failed"))

            Result.success(orderId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateOrderStatus(orderId: String, status: String): Result<Unit> {
        return try {
            firestore.collection("orders").document(orderId)
                .update("status", status).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
