package com.jay.sokoni.ui.vendor.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jay.sokoni.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VendorOnboardingScreen(
    onComplete: () -> Unit,
    viewModel: VendorOnboardingViewModel = hiltViewModel()
) {
    var currentStep by remember { mutableStateOf(1) }
    val uiState by viewModel.uiState.collectAsState()
    val vendor by viewModel.currentVendor.collectAsState()

    LaunchedEffect(uiState) {
        if (uiState is OnboardingUiState.Success) {
            onComplete()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Vendor Onboarding - Step $currentStep of 3") }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (currentStep) {
                1 -> BusinessDetailsStep(
                    vendor = vendor,
                    onNext = { 
                        viewModel.updateVendor { it.copy(storeName = it.storeName) } // Simple validation check would go here
                        currentStep = 2 
                    },
                    onUpdate = { viewModel.updateVendor { it } }
                )
                2 -> LocationStep(
                    vendor = vendor,
                    onCaptureLocation = { viewModel.captureLocation() },
                    onNext = { currentStep = 3 },
                    onBack = { currentStep = 1 }
                )
                3 -> ReviewStep(
                    vendor = vendor,
                    isLoading = uiState is OnboardingUiState.Loading,
                    onSubmit = { viewModel.submitOnboarding() },
                    onBack = { currentStep = 2 }
                )
            }
            
            if (uiState is OnboardingUiState.Error) {
                Text(
                    text = (uiState as OnboardingUiState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp)
                )
            }
        }
    }
}

@Composable
fun BusinessDetailsStep(
    vendor: com.jay.sokoni.domain.model.Vendor,
    onNext: () -> Unit,
    onUpdate: (com.jay.sokoni.domain.model.Vendor) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        SokoniTextField(
            value = vendor.storeName,
            onValueChange = { onUpdate(vendor.copy(storeName = it)) },
            label = "Store Name"
        )
        Spacer(modifier = Modifier.height(16.dp))
        SokoniTextField(
            value = vendor.businessName,
            onValueChange = { onUpdate(vendor.copy(businessName = it)) },
            label = "Business Name"
        )
        Spacer(modifier = Modifier.height(32.dp))
        SokoniPrimaryButton(text = "Next", onClick = onNext)
    }
}

@Composable
fun LocationStep(
    vendor: com.jay.sokoni.domain.model.Vendor,
    onCaptureLocation: () -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Text("We need your store location for deliveries.")
        Spacer(modifier = Modifier.height(16.dp))
        SokoniSecondaryButton(text = "Capture GPS Location", onClick = onCaptureLocation)
        
        vendor.location?.let {
            Spacer(modifier = Modifier.height(16.dp))
            Text("Location Captured: ${it.latitude}, ${it.longitude}")
            Text("Accuracy: ${it.accuracy}m")
        }
        
        Spacer(modifier = Modifier.weight(1f))
        Row(modifier = Modifier.fillMaxWidth()) {
            SokoniSecondaryButton(text = "Back", onClick = onBack, modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.width(16.dp))
            SokoniPrimaryButton(text = "Next", onClick = onNext, modifier = Modifier.weight(1f), enabled = vendor.location != null)
        }
    }
}

@Composable
fun ReviewStep(
    vendor: com.jay.sokoni.domain.model.Vendor,
    isLoading: Boolean,
    onSubmit: () -> Unit,
    onBack: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Text("Review your details", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Store: ${vendor.storeName}")
        Text("Business: ${vendor.businessName}")
        Text("Location: ${vendor.location?.latitude}, ${vendor.location?.longitude}")
        
        Spacer(modifier = Modifier.weight(1f))
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            Row(modifier = Modifier.fillMaxWidth()) {
                SokoniSecondaryButton(text = "Back", onClick = onBack, modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.width(16.dp))
                SokoniPrimaryButton(text = "Submit for Approval", onClick = onSubmit, modifier = Modifier.weight(1f))
            }
        }
    }
}
