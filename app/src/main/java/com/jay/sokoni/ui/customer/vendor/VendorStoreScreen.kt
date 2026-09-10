package com.jay.sokoni.ui.customer.vendor

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jay.sokoni.domain.model.Product
import com.jay.sokoni.domain.model.Vendor
import com.jay.sokoni.ui.components.SokoniCard
import com.jay.sokoni.ui.components.SokoniLoading
import com.jay.sokoni.ui.theme.SokoniTheme
import com.jay.sokoni.ui.vendor.products.ProductItem

@Composable
fun VendorStoreScreen(
    vendorId: String,
    onNavigateBack: () -> Unit,
    onProductClick: (String) -> Unit,
    viewModel: VendorStoreViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(vendorId) {
        viewModel.loadVendorStore(vendorId)
    }

    VendorStoreContent(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onProductClick = onProductClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VendorStoreContent(
    uiState: VendorStoreUiState,
    onNavigateBack: () -> Unit,
    onProductClick: (String) -> Unit
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.vendor?.storeName ?: "Vendor Store") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            SokoniLoading()
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding),
                contentPadding = PaddingValues(16.dp)
            ) {
                uiState.vendor?.let { vendor ->
                    item {
                        VendorHeader(
                            vendor = vendor,
                            onCall = {
                                val intent = Intent(Intent.ACTION_DIAL).apply {
                                    data = Uri.parse("tel:${vendor.phone}")
                                }
                                context.startActivity(intent)
                            },
                            onEmail = {
                                val intent = Intent(Intent.ACTION_SENDTO).apply {
                                    data = Uri.parse("mailto:${vendor.email}")
                                }
                                context.startActivity(intent)
                            }
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(text = "Products", style = MaterialTheme.typography.titleLarge)
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                items(uiState.products) { product ->
                    ProductItem(product = product, onClick = { onProductClick(product.productId) })
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
fun VendorHeader(
    vendor: Vendor,
    onCall: () -> Unit,
    onEmail: () -> Unit
) {
    SokoniCard {
        Column(modifier = Modifier.padding(16.dp)) {
            Surface(
                modifier = Modifier.fillMaxWidth().height(160.dp),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {}
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = vendor.storeName, style = MaterialTheme.typography.headlineSmall)
            Text(text = vendor.category, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary)
            Text(text = vendor.description, style = MaterialTheme.typography.bodySmall)

            Spacer(modifier = Modifier.height(16.dp))
            Row {
                IconButton(onClick = onCall) { Icon(Icons.Default.Phone, contentDescription = "Call") }
                IconButton(onClick = onEmail) { Icon(Icons.Default.Email, contentDescription = "Email") }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun VendorStorePreview() {
    SokoniTheme {
        VendorStoreContent(
            uiState = VendorStoreUiState(
                vendor = Vendor(storeName = "MJC Store", category = "Water", description = "Fresh water delivery"),
                products = listOf(Product(name = "20L Water"))
            ),
            onNavigateBack = {},
            onProductClick = {}
        )
    }
}
