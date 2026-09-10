package com.jay.sokoni.ui.customer.cart

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jay.sokoni.domain.model.Cart
import com.jay.sokoni.domain.model.CartItem
import com.jay.sokoni.ui.components.SokoniPrimaryButton
import com.jay.sokoni.ui.components.SokoniLoading
import com.jay.sokoni.ui.components.SokoniCard
import com.jay.sokoni.ui.theme.SokoniTheme

@Composable
fun CartScreen(
    onNavigateBack: () -> Unit,
    onCheckout: () -> Unit,
    viewModel: CartViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    CartContent(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onCheckout = onCheckout,
        onUpdateQuantity = { id, variant, qty -> viewModel.updateQuantity(id, variant, qty) },
        onSetDeliveryMode = { viewModel.setDeliveryMode(it) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartContent(
    uiState: CartUiState,
    onNavigateBack: () -> Unit,
    onCheckout: () -> Unit,
    onUpdateQuantity: (String, String, Int) -> Unit,
    onSetDeliveryMode: (Boolean) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Your Cart") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            uiState.cart?.let {
                Surface(tonalElevation = 8.dp) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total", style = MaterialTheme.typography.titleLarge)
                            Text("KSh ${uiState.total}", style = MaterialTheme.typography.titleLarge)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        SokoniPrimaryButton(text = "Checkout", onClick = onCheckout)
                    }
                }
            }
        }
    ) { padding ->
        if (uiState.isLoading) {
            SokoniLoading()
        } else if (uiState.cart == null || uiState.cart.items.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Your cart is empty")
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding),
                contentPadding = PaddingValues(16.dp)
            ) {
                item {
                    Text(text = "Order from ${uiState.cart.vendorName}", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(16.dp))
                }

                items(uiState.cart.items) { item ->
                    CartItemRow(
                        item = item,
                        onIncrease = { onUpdateQuantity(item.productId, item.variantName, item.quantity + 1) },
                        onDecrease = { onUpdateQuantity(item.productId, item.variantName, item.quantity - 1) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(text = "Fulfillment", style = MaterialTheme.typography.titleMedium)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = uiState.cart.isDelivery, onClick = { onSetDeliveryMode(true) })
                        Text("Delivery")
                        Spacer(modifier = Modifier.width(16.dp))
                        RadioButton(selected = !uiState.cart.isDelivery, onClick = { onSetDeliveryMode(false) })
                        Text("Pickup")
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    SummaryRow(label = "Subtotal", value = uiState.cart.subtotal)
                    SummaryRow(label = "Delivery Fee", value = uiState.deliveryFee)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    SummaryRow(label = "Total", value = uiState.total, style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}

@Composable
fun CartItemRow(
    item: CartItem,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit
) {
    SokoniCard {
        Row(modifier = Modifier.padding(12.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = item.productName, style = MaterialTheme.typography.bodyLarge)
                Text(text = item.variantName, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                Text(text = "KSh ${item.price}", style = MaterialTheme.typography.bodyMedium)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onDecrease) { Icon(Icons.Default.Remove, contentDescription = "Decrease") }
                Text(text = item.quantity.toString())
                IconButton(onClick = onIncrease) { Icon(Icons.Default.Add, contentDescription = "Increase") }
            }
        }
    }
}

@Composable
fun SummaryRow(label: String, value: Double, style: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.bodyMedium) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = label, style = style)
        Text(text = "KSh $value", style = style)
    }
}

@Preview(showBackground = true)
@Composable
fun CartPreview() {
    SokoniTheme {
        CartContent(
            uiState = CartUiState(
                cart = Cart(
                    vendorName = "MJC Store",
                    items = listOf(CartItem(productName = "20L Water", variantName = "Refill", price = 150.0, quantity = 2))
                ),
                deliveryFee = 150.0,
                total = 450.0
            ),
            onNavigateBack = {},
            onCheckout = {},
            onUpdateQuantity = { _, _, _ -> },
            onSetDeliveryMode = {}
        )
    }
}
