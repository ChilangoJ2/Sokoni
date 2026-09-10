package com.jay.sokoni.ui.customer.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jay.sokoni.domain.model.Category
import com.jay.sokoni.domain.model.Product
import com.jay.sokoni.domain.model.Vendor
import com.jay.sokoni.domain.repository.AuthRepository
import com.jay.sokoni.domain.repository.MarketplaceRepository
import com.jay.sokoni.util.LocationTracker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val isLoading: Boolean = false,
    val categories: List<Category> = emptyList(),
    val nearbyVendors: List<Vendor> = emptyList(),
    val popularProducts: List<Product> = emptyList(),
    val userAddress: String = "Select Location",
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val marketplaceRepository: MarketplaceRepository,
    private val authRepository: AuthRepository,
    private val locationTracker: LocationTracker
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadHomeData()
    }

    private fun loadHomeData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            // In a real app, we'd use actual user location
            val location = locationTracker.getCurrentLocation()
            val lat = location?.latitude ?: 0.0
            val lon = location?.longitude ?: 0.0
            
            combine(
                marketplaceRepository.getCategories(),
                marketplaceRepository.getNearbyVendors(lat, lon, 10.0),
                marketplaceRepository.getPopularProducts(),
                authRepository.currentUser
            ) { categories, vendors, products, user ->
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        categories = categories,
                        nearbyVendors = vendors,
                        popularProducts = products,
                        userAddress = user?.location?.address ?: "Select Location"
                    )
                }
            }.catch { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }.collect()
        }
    }
}
