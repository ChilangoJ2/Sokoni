package com.jay.sokoni.ui.vendor.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jay.sokoni.domain.model.Vendor
import com.jay.sokoni.domain.model.VendorStatus
import com.jay.sokoni.domain.repository.VendorRepository
import com.jay.sokoni.util.LocationTracker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class OnboardingUiState {
    object Idle : OnboardingUiState()
    object Loading : OnboardingUiState()
    object Success : OnboardingUiState()
    data class Error(val message: String) : OnboardingUiState()
}

@HiltViewModel
class VendorOnboardingViewModel @Inject constructor(
    private val vendorRepository: VendorRepository,
    private val locationTracker: LocationTracker
) : ViewModel() {

    private val _uiState = MutableStateFlow<OnboardingUiState>(OnboardingUiState.Idle)
    val uiState = _uiState.asStateFlow()

    private val _currentVendor = MutableStateFlow(Vendor())
    val currentVendor = _currentVendor.asStateFlow()

    fun updateVendor(update: (Vendor) -> Vendor) {
        _currentVendor.value = update(_currentVendor.value)
    }

    fun captureLocation() {
        viewModelScope.launch {
            _uiState.value = OnboardingUiState.Loading
            val location = locationTracker.getCurrentLocation()
            if (location != null) {
                updateVendor { it.copy(location = location) }
                _uiState.value = OnboardingUiState.Idle
            } else {
                _uiState.value = OnboardingUiState.Error("Could not capture location. Ensure GPS is on.")
            }
        }
    }

    fun submitOnboarding() {
        viewModelScope.launch {
            _uiState.value = OnboardingUiState.Loading
            val vendor = _currentVendor.value.copy(
                status = VendorStatus.SUBMITTED,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            vendorRepository.saveVendor(vendor)
                .onSuccess { _uiState.value = OnboardingUiState.Success }
                .onFailure { _uiState.value = OnboardingUiState.Error(it.message ?: "Submission failed") }
        }
    }
}
