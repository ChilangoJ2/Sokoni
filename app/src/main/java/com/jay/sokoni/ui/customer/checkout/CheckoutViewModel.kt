package com.jay.sokoni.ui.customer.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jay.sokoni.domain.model.*
import com.jay.sokoni.domain.repository.AuthRepository
import com.jay.sokoni.domain.repository.CartRepository
import com.jay.sokoni.domain.repository.OrderRepository
import com.jay.sokoni.domain.repository.PaymentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CheckoutUiState(
    val isLoading: Boolean = false,
    val cart: Cart? = null,
    val deliveryLocation: UserLocation? = null,
    val deliveryFee: Double = 150.0,
    val total: Double = 0.0,
    val mpesaNumber: String = "",
    val paymentId: String? = null,
    val paymentStatus: PaymentStatus = PaymentStatus.PENDING,
    val isOrderPlaced: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val authRepository: AuthRepository,
    private val orderRepository: OrderRepository,
    private val paymentRepository: PaymentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CheckoutUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadCheckoutData()
    }

    private fun loadCheckoutData() {
        viewModelScope.launch {
            combine(
                cartRepository.getCart(),
                authRepository.currentUser
            ) { cart, user ->
                if (cart != null) {
                    val fee = if (cart.isDelivery) 150.0 else 0.0
                    _uiState.update { 
                        it.copy(
                            cart = cart,
                            deliveryLocation = cart.deliveryLocation ?: user?.location,
                            mpesaNumber = it.mpesaNumber.ifEmpty { user?.phone ?: "" },
                            deliveryFee = fee,
                            total = cart.subtotal + fee
                        ) 
                    }
                }
            }.collect()
        }
    }

    fun onMpesaNumberChange(number: String) {
        _uiState.update { it.copy(mpesaNumber = number) }
    }

    fun placeOrder() {
        val cart = _uiState.value.cart ?: return
        val location = if (cart.isDelivery) _uiState.value.deliveryLocation else null
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            // 1. Create Order (Validated by Backend)
            val order = Order(
                vendorId = cart.vendorId,
                vendorName = cart.vendorName,
                items = cart.items.map { 
                    OrderItem(
                        productId = it.productId,
                        productName = it.productName,
                        variantName = it.variantName,
                        quantity = it.quantity,
                        unitPrice = it.price,
                        lineTotal = it.price * it.quantity
                    )
                },
                isDelivery = cart.isDelivery,
                deliveryLocation = location,
                subtotal = cart.subtotal,
                totalAmount = _uiState.value.total
            )

            orderRepository.createOrder(order)
                .onSuccess { orderId ->
                    // 2. Initiate M-Pesa STK Push
                    initiatePayment(orderId)
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }

    private fun initiatePayment(orderId: String) {
        viewModelScope.launch {
            paymentRepository.initiateMpesaPush(
                orderId = orderId,
                phoneNumber = _uiState.value.mpesaNumber,
                amount = _uiState.value.total
            ).onSuccess { paymentId ->
                _uiState.update { it.copy(paymentId = paymentId) }
                observePaymentStatus(paymentId)
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    private fun observePaymentStatus(paymentId: String) {
        viewModelScope.launch {
            paymentRepository.getPayment(paymentId).collect { payment ->
                if (payment != null) {
                    _uiState.update { it.copy(paymentStatus = payment.status) }
                    if (payment.status == PaymentStatus.PAID) {
                        _uiState.update { it.copy(isLoading = false, isOrderPlaced = true) }
                        cartRepository.clearCart()
                    } else if (payment.status == PaymentStatus.FAILED) {
                        _uiState.update { it.copy(isLoading = false, error = "Payment failed. Please try again.") }
                    }
                }
            }
        }
    }
}
