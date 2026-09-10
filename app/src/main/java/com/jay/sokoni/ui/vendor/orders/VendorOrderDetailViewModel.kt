package com.jay.sokoni.ui.vendor.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jay.sokoni.domain.model.Order
import com.jay.sokoni.domain.model.OrderStatus
import com.jay.sokoni.domain.model.RiderDetails
import com.jay.sokoni.domain.repository.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class VendorOrderDetailUiState(
    val isLoading: Boolean = false,
    val order: Order? = null,
    val error: String? = null,
    val isUpdating: Boolean = false
)

@HiltViewModel
class VendorOrderDetailViewModel @Inject constructor(
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(VendorOrderDetailUiState())
    val uiState = _uiState.asStateFlow()

    fun loadOrder(orderId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            orderRepository.getOrder(orderId).collect { order ->
                _uiState.update { it.copy(isLoading = false, order = order) }
            }
        }
    }

    fun acceptOrder() {
        updateStatus(OrderStatus.ACCEPTED)
    }

    fun confirmItem(productId: String, variantName: String, isConfirmed: Boolean) {
        val orderId = _uiState.value.order?.orderId ?: return
        viewModelScope.launch {
            orderRepository.confirmOrderItem(orderId, productId, variantName, isConfirmed)
        }
    }

    fun markAsReady() {
        val order = _uiState.value.order ?: return
        if (order.items.all { it.isConfirmedByVendor }) {
            updateStatus(OrderStatus.READY)
        } else {
            _uiState.update { it.copy(error = "Please confirm all items first") }
        }
    }

    fun dispatchOrder(riderName: String, riderPhone: String, plateNumber: String) {
        val orderId = _uiState.value.order?.orderId ?: return
        if (riderName.isBlank() || riderPhone.isBlank() || plateNumber.isBlank()) {
            _uiState.update { it.copy(error = "Rider name, phone, and vehicle registration are mandatory") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isUpdating = true) }
            val rider = RiderDetails(name = riderName, phone = riderPhone, vehicleRegistration = plateNumber)
            orderRepository.assignRider(orderId, rider).onSuccess {
                updateStatus(OrderStatus.OUT_FOR_DELIVERY)
            }.onFailure { e ->
                _uiState.update { it.copy(isUpdating = false, error = e.message) }
            }
        }
    }

    fun completeOrder() {
        updateStatus(OrderStatus.COMPLETED)
    }

    fun cancelOrder(reason: String) {
        val orderId = _uiState.value.order?.orderId ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isUpdating = true) }
            orderRepository.cancelOrder(orderId, reason, "VENDOR").onSuccess {
                _uiState.update { it.copy(isUpdating = false) }
            }.onFailure { e ->
                _uiState.update { it.copy(isUpdating = false, error = e.message) }
            }
        }
    }

    private fun updateStatus(status: OrderStatus) {
        val orderId = _uiState.value.order?.orderId ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isUpdating = true) }
            orderRepository.updateOrderStatus(orderId, status).onSuccess {
                _uiState.update { it.copy(isUpdating = false) }
            }.onFailure { e ->
                _uiState.update { it.copy(isUpdating = false, error = e.message) }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
