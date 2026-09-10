package com.jay.sokoni.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.jay.sokoni.domain.model.Cart
import com.jay.sokoni.domain.model.CartItem
import com.jay.sokoni.domain.repository.CartRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : CartRepository {

    private val userId get() = auth.currentUser?.uid

    override fun getCart(): Flow<Cart?> = callbackFlow {
        val id = userId
        if (id == null) {
            trySend(null)
            close()
            return@callbackFlow
        }
        val listener = firestore.collection("carts").document(id)
            .addSnapshotListener { snapshot, _ ->
                trySend(snapshot?.toObject(Cart::class.java))
            }
        awaitClose { listener.remove() }
    }

    override suspend fun addToCart(vendorId: String, vendorName: String, item: CartItem): Result<Unit> {
        val id = userId ?: return Result.failure(Exception("Not authenticated"))
        return try {
            val docRef = firestore.collection("carts").document(id)
            val existingCart = docRef.get().await().toObject(Cart::class.java)
            
            if (existingCart != null && existingCart.vendorId != vendorId) {
                return Result.failure(DifferentVendorException(vendorId, vendorName))
            }

            val updatedItems = existingCart?.items?.toMutableList() ?: mutableListOf()
            val existingItemIndex = updatedItems.indexOfFirst { it.productId == item.productId && it.variantName == item.variantName }
            
            if (existingItemIndex != -1) {
                val existingItem = updatedItems[existingItemIndex]
                updatedItems[existingItemIndex] = existingItem.copy(quantity = existingItem.quantity + item.quantity)
            } else {
                updatedItems.add(item)
            }

            val newCart = (existingCart ?: Cart(vendorId = vendorId, vendorName = vendorName)).copy(items = updatedItems)
            docRef.set(newCart).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun removeFromCart(productId: String, variantName: String): Result<Unit> {
        val id = userId ?: return Result.failure(Exception("Not authenticated"))
        return try {
            val docRef = firestore.collection("carts").document(id)
            val existingCart = docRef.get().await().toObject(Cart::class.java) ?: return Result.success(Unit)
            
            val updatedItems = existingCart.items.filterNot { it.productId == productId && it.variantName == variantName }
            if (updatedItems.isEmpty()) {
                docRef.delete().await()
            } else {
                docRef.set(existingCart.copy(items = updatedItems)).await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateQuantity(productId: String, variantName: String, quantity: Int): Result<Unit> {
        val id = userId ?: return Result.failure(Exception("Not authenticated"))
        return try {
            val docRef = firestore.collection("carts").document(id)
            val existingCart = docRef.get().await().toObject(Cart::class.java) ?: return Result.success(Unit)
            
            val updatedItems = existingCart.items.map { 
                if (it.productId == productId && it.variantName == variantName) it.copy(quantity = quantity) else it 
            }
            docRef.set(existingCart.copy(items = updatedItems)).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun clearCart(): Result<Unit> {
        val id = userId ?: return Result.failure(Exception("Not authenticated"))
        return try {
            firestore.collection("carts").document(id).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun setDeliveryMode(isDelivery: Boolean): Result<Unit> {
        val id = userId ?: return Result.failure(Exception("Not authenticated"))
        return try {
            firestore.collection("carts").document(id).update("isDelivery", isDelivery).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

class DifferentVendorException(val vendorId: String, val vendorName: String) : Exception("Cart contains items from another vendor")
