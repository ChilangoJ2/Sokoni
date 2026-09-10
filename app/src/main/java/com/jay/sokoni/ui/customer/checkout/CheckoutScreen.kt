package com.jay.sokoni.ui.customer.checkout

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jay.sokoni.domain.model.Cart
import com.jay.sokoni.domain.model.PaymentStatus
import com.jay.sokoni.domain.model.UserLocation
import com.jay.sokoni.ui.components.SokoniPrimaryButton
import com.jay.sokoni.ui.components.SokoniLoading
import com.jay.sokoni.ui.components.SokoniCard
import com.jay.sokoni.ui.components.SokoniTextField
import com.jay.sokoni.ui.customer.cart.SummaryRow
import com.jay.sokoni.ui.theme.SokoniTheme

@Composable
fun CheckoutScreen(
    onNavigateBack: () -> Unit,
    onOrderSuccess: () -> Unit,
    viewModel: CheckoutViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isOrderPlaced) {
        if (uiState.isOrderPlaced) {
            onOrderSuccess()
        }
    }

    CheckoutContent(
        uiState = uiState,
        onMpesaNumberChange = { viewModel.onMpesaNumberChange(it) },
        onNavigateBack = onNavigateBack,
        onPlaceOrder = { viewModel.placeOrder() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutContent(
    uiState: CheckoutUiState,
    onMpesaNumberChange: (String) -> Unit,
    onNavigateBack: () -> Unit,
    onPlaceOrder: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Checkout") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            Surface(tonalElevation = 8.dp) {
                Column(modifier = Modifier.padding(16.dp)) {
                    val buttonText = when (uiState.paymentStatus) {
                        PaymentStatus.PROCESSING -> "Processing Payment..."
                        else -> "Pay KSh ${uiState.total}"
                    }
                    
                    SokoniPrimaryButton(
                        text = buttonText,
                        onClick = onPlaceOrder,
                        enabled = !uiState.isLoading && (uiState.cart?.isDelivery == false || uiState.deliveryLocation != null)
                    )
                }
            }
        }
    ) { padding ->
        if (uiState.isLoading && uiState.paymentStatus != PaymentStatus.PROCESSING) {
            SokoniLoading()
        } else {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                if (uiState.paymentStatus == PaymentStatus.PROCESSING) {
                    PaymentProcessingView()
                } else {
                    DeliverySection(uiState)
                    Spacer(modifier = Modifier.height(24.dp))
                    PaymentSection(uiState.mpesaNumber, onMpesaNumberChange)
                    Spacer(modifier = Modifier.height(24.dp))
                    OrderSummarySection(uiState)
                }
                
                uiState.error?.let {
                    Text(text = it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 16.dp))
                }
            }
        }
    }
}

@Composable
fun DeliverySection(uiState: CheckoutUiState) {
    if (uiState.cart?.isDelivery == true) {
        Text(text = "Delivery Address", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(12.dp))
        SokoniCard {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = uiState.deliveryLocation?.address ?: "No location selected")
                }
                Spacer(modifier = Modifier.height(16.dp))
                SokoniTextField(value = uiState.deliveryLocation?.building ?: "", onValueChange = {}, label = "Building/Apartment")
                Spacer(modifier = Modifier.height(8.dp))
                SokoniTextField(value = uiState.deliveryLocation?.houseNumber ?: "", onValueChange = {}, label = "House/Unit Number")
            }
        }
    } else {
        Text(text = "Pickup Order", style = MaterialTheme.typography.titleMedium)
        Text(text = "You will pick up your order from ${uiState.cart?.vendorName}", style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
fun PaymentSection(mpesaNumber: String, onMpesaNumberChange: (String) -> Unit) {
    Text(text = "Payment Method", style = MaterialTheme.typography.titleMedium)
    Spacer(modifier = Modifier.height(12.dp))
    SokoniCard {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "M-Pesa STK Push", style = MaterialTheme.typography.bodyLarge)
            Text(text = "You will receive a prompt on your phone to enter your PIN.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(16.dp))
            SokoniTextField(
                value = mpesaNumber,
                onValueChange = onMpesaNumberChange,
                label = "M-Pesa Phone Number"
            )
        }
    }
}

@Composable
fun OrderSummarySection(uiState: CheckoutUiState) {
    Text(text = "Order Summary", style = MaterialTheme.typography.titleMedium)
    Spacer(modifier = Modifier.height(12.dp))
    SummaryRow(label = "Subtotal", value = uiState.cart?.subtotal ?: 0.0)
    SummaryRow(label = "Delivery Fee", value = uiState.deliveryFee)
    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
    SummaryRow(label = "Total", value = uiState.total, style = MaterialTheme.typography.titleMedium)
}

@Composable
fun PaymentProcessingView() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.secondary)
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Waiting for M-Pesa payment...", style = MaterialTheme.typography.titleMedium)
        Text(text = "Please check your phone for the STK prompt.", style = MaterialTheme.typography.bodySmall, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
    }
}

@Preview(showBackground = true)
@Composable
fun CheckoutPreview() {
    SokoniTheme {
        CheckoutContent(
            uiState = CheckoutUiState(
                cart = Cart(vendorName = "MJC Store", isDelivery = true),
                deliveryLocation = UserLocation(address = "123 Main St, Nairobi"),
                total = 450.0,
                mpesaNumber = "0712345678"
            ),
            onMpesaNumberChange = {},
            onNavigateBack = {},
            onPlaceOrder = {}
        )
    }
}
