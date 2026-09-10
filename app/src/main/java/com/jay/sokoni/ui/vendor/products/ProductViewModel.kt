package com.jay.sokoni.ui.vendor.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jay.sokoni.domain.model.Product
import com.jay.sokoni.domain.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ProductUiState {
    object Idle : ProductUiState()
    object Loading : ProductUiState()
    data class Success(val products: List<Product>) : ProductUiState()
    data class Error(val message: String) : ProductUiState()
}

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val productRepository: ProductRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProductUiState>(ProductUiState.Idle)
    val uiState = _uiState.asStateFlow()

    fun loadProducts(vendorId: String) {
        viewModelScope.launch {
            _uiState.value = ProductUiState.Loading
            productRepository.getProducts(vendorId).collect {
                _uiState.value = ProductUiState.Success(it)
            }
        }
    }

    fun saveProduct(product: Product) {
        viewModelScope.launch {
            _uiState.value = ProductUiState.Loading
            productRepository.saveProduct(product)
                .onFailure { _uiState.value = ProductUiState.Error(it.message ?: "Failed to save") }
                // loadProducts will be triggered by snapshot listener
        }
    }

    fun deleteProduct(productId: String) {
        viewModelScope.launch {
            productRepository.deleteProduct(productId)
        }
    }
}
