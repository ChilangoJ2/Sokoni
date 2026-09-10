package com.jay.sokoni.domain.repository

import com.jay.sokoni.domain.model.Vendor
import com.jay.sokoni.domain.model.VendorStatus
import kotlinx.coroutines.flow.Flow

interface VendorRepository {
    fun getVendor(vendorId: String): Flow<Vendor?>
    suspend fun saveVendor(vendor: Vendor): Result<Unit>
    suspend fun updateVendorStatus(vendorId: String, status: VendorStatus): Result<Unit>
}
