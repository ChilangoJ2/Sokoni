package com.jay.sokoni.ui.vendor.products

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jay.sokoni.domain.model.Product
import com.jay.sokoni.ui.components.SokoniCard
import com.jay.sokoni.ui.components.SokoniLoading

@Composable
fun ProductListScreen(
    vendorId: String,
    onAddProduct: () -> Unit,
    onEditProduct: (Product) -> Unit,
    viewModel: ProductViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(vendorId) {
        viewModel.loadProducts(vendorId)
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onAddProduct) {
                Icon(Icons.Default.Add, contentDescription = "Add Product")
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (uiState) {
                is ProductUiState.Loading -> SokoniLoading()
                is ProductUiState.Success -> {
                    val products = (uiState as ProductUiState.Success).products
                    LazyColumn(contentPadding = PaddingValues(16.dp)) {
                        items(products) { product ->
                            ProductItem(product = product, onClick = { onEditProduct(product) })
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }
                is ProductUiState.Error -> Text((uiState as ProductUiState.Error).message)
                else -> Unit
            }
        }
    }
}

@Composable
fun ProductItem(product: Product, onClick: () -> Unit) {
    SokoniCard(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = product.name, style = MaterialTheme.typography.titleMedium)
            Text(text = product.category, style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(8.dp))
            if (product.variants.isNotEmpty()) {
                Text(text = "From KSh ${product.variants.minOf { it.price }}", color = MaterialTheme.colorScheme.secondary)
            }
        }
    }
}
