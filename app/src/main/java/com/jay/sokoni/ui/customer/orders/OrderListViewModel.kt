package com.jay.sokoni.ui.customer.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jay.sokoni.domain.model.Order
import com.jay.sokoni.domain.repository.AuthRepository
import com.jay.sokoni.domain.repository.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OrderListUiState(
    val isLoading: Boolean = false,
    val orders: List<Order> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class OrderListViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OrderListUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadOrders()
    }

    private fun loadOrders() {
        viewModelScope.launch {
            authRepository.currentUser.flatMapLatest { user ->
                if (user != null) {
                    _uiState.update { it.copy(isLoading = true) }
                    orderRepository.getCustomerOrders(user.uid)
                } else {
                    flowOf(emptyList())
                }
            }.collect { orders ->
                _uiState.update { it.copy(isLoading = false, orders = orders.sortedByDescending { o -> o.createdAt }) }
            }
        }
    }
}
