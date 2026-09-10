package com.jay.sokoni.domain.repository

import com.jay.sokoni.domain.model.Category
import com.jay.sokoni.domain.model.Product
import com.jay.sokoni.domain.model.Vendor
import kotlinx.coroutines.flow.Flow

interface MarketplaceRepository {
    fun getCategories(): Flow<List<Category>>
    fun getNearbyVendors(latitude: Double, longitude: Double, radiusKm: Double): Flow<List<Vendor>>
    fun searchProducts(query: String): Flow<List<Product>>
    fun getPopularProducts(): Flow<List<Product>>
}
