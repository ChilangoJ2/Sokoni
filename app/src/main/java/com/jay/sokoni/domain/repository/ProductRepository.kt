package com.jay.sokoni.domain.repository

import com.jay.sokoni.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun getProducts(vendorId: String): Flow<List<Product>>
    fun getProduct(productId: String): Flow<Product?>
    suspend fun saveProduct(product: Product): Result<Unit>
    suspend fun deleteProduct(productId: String): Result<Unit>
}
