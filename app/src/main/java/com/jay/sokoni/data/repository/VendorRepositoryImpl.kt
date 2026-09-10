package com.jay.sokoni.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.jay.sokoni.domain.model.Vendor
import com.jay.sokoni.domain.model.VendorStatus
import com.jay.sokoni.domain.repository.VendorRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VendorRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : VendorRepository {

    override fun getVendor(vendorId: String): Flow<Vendor?> = callbackFlow {
        val listener = firestore.collection("vendors").document(vendorId)
            .addSnapshotListener { snapshot, _ ->
                trySend(snapshot?.toObject(Vendor::class.java))
            }
        awaitClose { listener.remove() }
    }

    override suspend fun saveVendor(vendor: Vendor): Result<Unit> {
        return try {
            firestore.collection("vendors").document(vendor.vendorId).set(vendor).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateVendorStatus(vendorId: String, status: VendorStatus): Result<Unit> {
        return try {
            firestore.collection("vendors").document(vendorId)
                .update("status", status.name).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
