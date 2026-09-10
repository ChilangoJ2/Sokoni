package com.jay.sokoni.ui.customer.product

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jay.sokoni.domain.model.Product
import com.jay.sokoni.domain.model.ProductVariant
import com.jay.sokoni.domain.model.Vendor
import com.jay.sokoni.ui.components.SokoniPrimaryButton
import com.jay.sokoni.ui.components.SokoniLoading
import com.jay.sokoni.ui.theme.SokoniTheme

@Composable
fun ProductDetailScreen(
    productId: String,
    onNavigateBack: () -> Unit,
    viewModel: ProductDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(productId) {
        viewModel.loadProduct(productId)
    }

    ProductDetailContent(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onAddToCart = { viewModel.addToCart(it) },
        onClearCartAndAdd = { viewModel.clearCartAndAdd(it) },
        onDismissDialog = { viewModel.dismissDialog() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailContent(
    uiState: ProductDetailUiState,
    onNavigateBack: () -> Unit,
    onAddToCart: (Int) -> Unit,
    onClearCartAndAdd: (Int) -> Unit,
    onDismissDialog: () -> Unit
) {
    var selectedVariantIndex by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.product?.name ?: "Product Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            uiState.product?.let { product ->
                Surface(tonalElevation = 8.dp) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (product.variants.isNotEmpty()) {
                            Text(
                                text = "KSh ${product.variants[selectedVariantIndex].price}",
                                style = MaterialTheme.typography.headlineSmall
                            )
                        }
                        SokoniPrimaryButton(
                            text = "Add to Cart",
                            onClick = { onAddToCart(selectedVariantIndex) },
                            modifier = Modifier.width(160.dp)
                        )
                    }
                }
            }
        }
    ) { padding ->
        if (uiState.isLoading) {
            SokoniLoading()
        } else {
            uiState.product?.let { product ->
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Surface(
                        modifier = Modifier.fillMaxWidth().height(240.dp),
                        shape = MaterialTheme.shapes.medium,
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {}

                    Spacer(modifier = Modifier.height(24.dp))
                    Text(text = product.name, style = MaterialTheme.typography.headlineMedium)
                    Text(text = "Vendor: ${uiState.vendor?.storeName}", style = MaterialTheme.typography.bodyMedium)

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = product.description, style = MaterialTheme.typography.bodyLarge)

                    if (product.variants.size > 1) {
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(text = "Select Variant", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        product.variants.forEachIndexed { index, variant ->
                            FilterChip(
                                selected = selectedVariantIndex == index,
                                onClick = { selectedVariantIndex = index },
                                label = { Text(variant.name) },
                                modifier = Modifier.padding(end = 8.dp)
                            )
                        }
                    }
                }
            }
        }

        if (uiState.showDifferentVendorDialog) {
            AlertDialog(
                onDismissRequest = onDismissDialog,
                title = { Text("Replace Cart?") },
                text = { Text("Your cart already contains items from another vendor. Would you like to clear it and add this item from ${uiState.vendor?.storeName}?") },
                confirmButton = {
                    TextButton(onClick = { onClearCartAndAdd(selectedVariantIndex) }) {
                        Text("Clear and Add")
                    }
                },
                dismissButton = {
                    TextButton(onClick = onDismissDialog) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProductDetailPreview() {
    SokoniTheme {
        ProductDetailContent(
            uiState = ProductDetailUiState(
                product = Product(
                    name = "20L Water",
                    description = "Fresh drinking water",
                    variants = listOf(ProductVariant(name = "Refill", price = 150.0), ProductVariant(name = "New Bottle", price = 500.0))
                ),
                vendor = Vendor(storeName = "MJC Water")
            ),
            onNavigateBack = {},
            onAddToCart = {},
            onClearCartAndAdd = {},
            onDismissDialog = {}
        )
    }
}
