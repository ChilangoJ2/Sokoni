package com.jay.sokoni.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.jay.sokoni.domain.model.Category
import com.jay.sokoni.domain.model.Product
import com.jay.sokoni.domain.model.Vendor
import com.jay.sokoni.domain.model.VendorStatus
import com.jay.sokoni.domain.repository.MarketplaceRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MarketplaceRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : MarketplaceRepository {

    override fun getCategories(): Flow<List<Category>> = callbackFlow {
        val listener = firestore.collection("categories")
            .addSnapshotListener { snapshot, _ ->
                trySend(snapshot?.toObjects(Category::class.java) ?: emptyList())
            }
        awaitClose { listener.remove() }
    }

    override fun getNearbyVendors(latitude: Double, longitude: Double, radiusKm: Double): Flow<List<Vendor>> = callbackFlow {
        // In a real app, we'd use GeoFirestore here. 
        // For now, we'll fetch approved vendors and filter on client (or prepare for GeoQueries)
        val listener = firestore.collection("vendors")
            .whereEqualTo("status", VendorStatus.APPROVED.name)
            .addSnapshotListener { snapshot, _ ->
                val vendors = snapshot?.toObjects(Vendor::class.java) ?: emptyList()
                // Simple filtering logic if needed, or return all approved for now
                trySend(vendors)
            }
        awaitClose { listener.remove() }
    }

    override fun searchProducts(query: String): Flow<List<Product>> = callbackFlow {
        val listener = firestore.collection("products")
            .whereEqualTo("isActive", true)
            .orderBy("name")
            .startAt(query)
            .endAt(query + "\uf8ff")
            .addSnapshotListener { snapshot, _ ->
                trySend(snapshot?.toObjects(Product::class.java) ?: emptyList())
            }
        awaitClose { listener.remove() }
    }

    override fun getPopularProducts(): Flow<List<Product>> = callbackFlow {
        val listener = firestore.collection("products")
            .whereEqualTo("isActive", true)
            .limit(10)
            .addSnapshotListener { snapshot, _ ->
                trySend(snapshot?.toObjects(Product::class.java) ?: emptyList())
            }
        awaitClose { listener.remove() }
    }
}
