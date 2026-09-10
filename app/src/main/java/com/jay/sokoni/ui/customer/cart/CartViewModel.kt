package com.jay.sokoni.ui.customer.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jay.sokoni.domain.model.Cart
import com.jay.sokoni.domain.repository.CartRepository
import com.jay.sokoni.domain.repository.ProductRepository
import com.jay.sokoni.domain.repository.VendorRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CartUiState(
    val isLoading: Boolean = false,
    val cart: Cart? = null,
    val deliveryFee: Double = 0.0,
    val total: Double = 0.0,
    val error: String? = null
)

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val vendorRepository: VendorRepository,
    private val productRepository: ProductRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CartUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadCart()
    }

    private fun loadCart() {
        viewModelScope.launch {
            cartRepository.getCart().collect { cart ->
                if (cart != null) {
                    // Revalidate
                    val fee = if (cart.isDelivery) 150.0 else 0.0 // Simplified fee logic
                    _uiState.update { it.copy(cart = cart, deliveryFee = fee, total = cart.subtotal + fee) }
                } else {
                    _uiState.update { it.copy(cart = null, total = 0.0) }
                }
            }
        }
    }

    fun updateQuantity(productId: String, variantName: String, quantity: Int) {
        viewModelScope.launch {
            if (quantity <= 0) {
                cartRepository.removeFromCart(productId, variantName)
            } else {
                cartRepository.updateQuantity(productId, variantName, quantity)
            }
        }
    }

    fun setDeliveryMode(isDelivery: Boolean) {
        viewModelScope.launch {
            cartRepository.setDeliveryMode(isDelivery)
        }
    }
    
    fun clearCart() {
        viewModelScope.launch {
            cartRepository.clearCart()
        }
    }
}
