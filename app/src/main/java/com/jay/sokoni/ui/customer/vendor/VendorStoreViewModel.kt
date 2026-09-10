package com.jay.sokoni.ui.customer.vendor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jay.sokoni.domain.model.Product
import com.jay.sokoni.domain.model.Vendor
import com.jay.sokoni.domain.repository.ProductRepository
import com.jay.sokoni.domain.repository.VendorRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class VendorStoreUiState(
    val isLoading: Boolean = false,
    val vendor: Vendor? = null,
    val products: List<Product> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class VendorStoreViewModel @Inject constructor(
    private val vendorRepository: VendorRepository,
    private val productRepository: ProductRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(VendorStoreUiState())
    val uiState = _uiState.asStateFlow()

    fun loadVendorStore(vendorId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            combine(
                vendorRepository.getVendor(vendorId),
                productRepository.getProducts(vendorId)
            ) { vendor, products ->
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        vendor = vendor,
                        products = products
                    )
                }
            }.catch { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }.collect()
        }
    }
}
