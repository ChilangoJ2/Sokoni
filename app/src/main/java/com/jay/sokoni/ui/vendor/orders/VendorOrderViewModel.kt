package com.jay.sokoni.ui.vendor.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jay.sokoni.domain.model.Order
import com.jay.sokoni.domain.model.OrderStatus
import com.jay.sokoni.domain.repository.AuthRepository
import com.jay.sokoni.domain.repository.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class VendorOrderUiState(
    val isLoading: Boolean = false,
    val orders: List<Order> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class VendorOrderViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(VendorOrderUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadVendorOrders()
    }

    private fun loadVendorOrders() {
        viewModelScope.launch {
            authRepository.currentUser.flatMapLatest { user ->
                if (user != null) {
                    _uiState.update { it.copy(isLoading = true) }
                    orderRepository.getVendorOrders(user.uid)
                } else {
                    flowOf(emptyList())
                }
            }.collect { orders ->
                _uiState.update { it.copy(isLoading = false, orders = orders.sortedByDescending { o -> o.createdAt }) }
            }
        }
    }

    fun updateStatus(orderId: String, status: OrderStatus) {
        viewModelScope.launch {
            orderRepository.updateOrderStatus(orderId, status.name)
        }
    }
}
