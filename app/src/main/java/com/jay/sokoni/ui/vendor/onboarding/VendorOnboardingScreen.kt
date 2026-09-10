package com.jay.sokoni.ui.vendor.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jay.sokoni.domain.model.Vendor
import com.jay.sokoni.ui.components.*
import com.jay.sokoni.ui.theme.SokoniTheme

@Composable
fun VendorOnboardingScreen(
    onComplete: () -> Unit,
    viewModel: VendorOnboardingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val vendor by viewModel.currentVendor.collectAsState()

    LaunchedEffect(uiState) {
        if (uiState is OnboardingUiState.Success) {
            onComplete()
        }
    }

    OnboardingContent(
        uiState = uiState,
        vendor = vendor,
        onUpdateVendor = { viewModel.updateVendor(it) },
        onCaptureLocation = { viewModel.captureLocation() },
        onSubmit = { viewModel.submitOnboarding() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingContent(
    uiState: OnboardingUiState,
    vendor: Vendor,
    onUpdateVendor: ((Vendor) -> Vendor) -> Unit,
    onCaptureLocation: () -> Unit,
    onSubmit: () -> Unit
) {
    var currentStep by remember { mutableIntStateOf(1) }

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
                    onNext = { currentStep = 2 },
                    onUpdate = { onUpdateVendor { it } }
                )
                2 -> LocationStep(
                    vendor = vendor,
                    onCaptureLocation = onCaptureLocation,
                    onNext = { currentStep = 3 },
                    onBack = { currentStep = 1 }
                )
                3 -> ReviewStep(
                    vendor = vendor,
                    isLoading = uiState is OnboardingUiState.Loading,
                    onSubmit = onSubmit,
                    onBack = { currentStep = 2 }
                )
            }

            if (uiState is OnboardingUiState.Error) {
                Text(
                    text = uiState.message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp)
                )
            }
        }
    }
}

@Composable
fun BusinessDetailsStep(
    vendor: Vendor,
    onNext: () -> Unit,
    onUpdate: (Vendor) -> Unit
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
    vendor: Vendor,
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
    vendor: Vendor,
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

@Preview(showBackground = true)
@Composable
fun OnboardingPreview() {
    SokoniTheme {
        OnboardingContent(
            uiState = OnboardingUiState.Idle,
            vendor = Vendor(storeName = "MJC Store"),
            onUpdateVendor = {},
            onCaptureLocation = {},
            onSubmit = {}
        )
    }
}
