package com.jay.sokoni.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.jay.sokoni.domain.model.Product
import com.jay.sokoni.domain.repository.ProductRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : ProductRepository {

    override fun getProducts(vendorId: String): Flow<List<Product>> = callbackFlow {
        val listener = firestore.collection("products")
            .whereEqualTo("vendorId", vendorId)
            .addSnapshotListener { snapshot, _ ->
                val products = snapshot?.toObjects(Product::class.java) ?: emptyList()
                trySend(products)
            }
        awaitClose { listener.remove() }
    }

    override fun getProduct(productId: String): Flow<Product?> = callbackFlow {
        val listener = firestore.collection("products").document(productId)
            .addSnapshotListener { snapshot, _ ->
                trySend(snapshot?.toObject(Product::class.java))
            }
        awaitClose { listener.remove() }
    }

    override suspend fun saveProduct(product: Product): Result<Unit> {
        return try {
            val docRef = if (product.productId.isEmpty()) {
                firestore.collection("products").document()
            } else {
                firestore.collection("products").document(product.productId)
            }
            val finalProduct = product.copy(productId = docRef.id)
            docRef.set(finalProduct).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteProduct(productId: String): Result<Unit> {
        return try {
            firestore.collection("products").document(productId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
