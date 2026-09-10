package com.jay.sokoni.ui.customer.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jay.sokoni.data.repository.DifferentVendorException
import com.jay.sokoni.domain.model.CartItem
import com.jay.sokoni.domain.model.Product
import com.jay.sokoni.domain.model.Vendor
import com.jay.sokoni.domain.repository.CartRepository
import com.jay.sokoni.domain.repository.ProductRepository
import com.jay.sokoni.domain.repository.VendorRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProductDetailUiState(
    val isLoading: Boolean = false,
    val product: Product? = null,
    val vendor: Vendor? = null,
    val error: String? = null,
    val showDifferentVendorDialog: Boolean = false,
    val pendingVendorId: String = "",
    val pendingVendorName: String = ""
)

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val vendorRepository: VendorRepository,
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductDetailUiState())
    val uiState = _uiState.asStateFlow()

    fun loadProduct(productId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            productRepository.getProduct(productId).collect { product ->
                if (product != null) {
                    vendorRepository.getVendor(product.vendorId).collect { vendor ->
                        _uiState.update { it.copy(isLoading = false, product = product, vendor = vendor) }
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = "Product not found") }
                }
            }
        }
    }

    fun addToCart(variantIndex: Int) {
        val product = _uiState.value.product ?: return
        val vendor = _uiState.value.vendor ?: return
        val variant = product.variants.getOrNull(variantIndex) ?: return

        val cartItem = CartItem(
            productId = product.productId,
            productName = product.name,
            variantName = variant.name,
            price = variant.price,
            quantity = 1,
            imageUrl = product.imageUrls.firstOrNull() ?: ""
        )

        viewModelScope.launch {
            cartRepository.addToCart(vendor.vendorId, vendor.storeName, cartItem)
                .onFailure { e ->
                    if (e is DifferentVendorException) {
                        _uiState.update { it.copy(showDifferentVendorDialog = true, pendingVendorId = e.vendorId, pendingVendorName = e.vendorName) }
                    } else {
                        _uiState.update { it.copy(error = e.message) }
                    }
                }
        }
    }

    fun clearCartAndAdd(variantIndex: Int) {
        viewModelScope.launch {
            cartRepository.clearCart()
            _uiState.update { it.copy(showDifferentVendorDialog = false) }
            addToCart(variantIndex)
        }
    }

    fun dismissDialog() {
        _uiState.update { it.copy(showDifferentVendorDialog = false) }
    }
}
