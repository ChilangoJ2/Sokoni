package com.jay.sokoni.ui.customer.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jay.sokoni.domain.model.Rating
import com.jay.sokoni.domain.repository.AuthRepository
import com.jay.sokoni.domain.repository.VendorRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OrderRatingUiState(
    val isLoading: Boolean = false,
    val isSubmitted: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class OrderRatingViewModel @Inject constructor(
    private val vendorRepository: VendorRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OrderRatingUiState())
    val uiState = _uiState.asStateFlow()

    fun submitRating(vendorId: String, orderId: String, rating: Int, review: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val userId = authRepository.currentUser.first()?.uid ?: return@launch
            
            val ratingModel = Rating(
                customerId = userId,
                vendorId = vendorId,
                orderId = orderId,
                rating = rating,
                review = review
            )
            
            vendorRepository.submitRating(ratingModel)
                .onSuccess { 
                    _uiState.update { it.copy(isLoading = false, isSubmitted = true) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }
}
