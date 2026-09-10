package com.jay.sokoni.ui.customer.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jay.sokoni.domain.model.Category
import com.jay.sokoni.domain.model.Product
import com.jay.sokoni.domain.model.Vendor
import com.jay.sokoni.ui.components.SokoniCard
import com.jay.sokoni.ui.components.SokoniLoading
import com.jay.sokoni.ui.theme.SokoniTheme

@Composable
fun CustomerHomeScreen(
    onNavigateToSearch: () -> Unit,
    onNavigateToVendor: (String) -> Unit,
    onNavigateToProduct: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    HomeContent(
        uiState = uiState,
        onNavigateToSearch = onNavigateToSearch,
        onNavigateToVendor = onNavigateToVendor,
        onNavigateToProduct = onNavigateToProduct
    )
}

@Composable
fun HomeContent(
    uiState: HomeUiState,
    onNavigateToSearch: () -> Unit,
    onNavigateToVendor: (String) -> Unit,
    onNavigateToProduct: (String) -> Unit
) {
    Scaffold(
        topBar = {
            HomeTopBar(address = uiState.userAddress)
        }
    ) { padding ->
        if (uiState.isLoading) {
            SokoniLoading()
        } else {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                SearchBarPlaceholder(onClick = onNavigateToSearch)

                SectionHeader(title = "Categories")
                CategoryList(categories = uiState.categories)

                SectionHeader(title = "Nearby Vendors")
                VendorList(vendors = uiState.nearbyVendors, onVendorClick = onNavigateToVendor)

                SectionHeader(title = "Popular Products")
                ProductList(products = uiState.popularProducts, onProductClick = onNavigateToProduct)

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun HomeTopBar(address: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = address, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun SearchBarPlaceholder(onClick: () -> Unit) {
    SokoniCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray)
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = "Search for water, gas, groceries...", color = Color.Gray)
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier.padding(16.dp)
    )
}

@Composable
fun CategoryList(categories: List<Category>) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(categories) { category ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Surface(
                    modifier = Modifier.size(64.dp),
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {}
                Text(text = category.name, style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}

@Composable
fun VendorList(vendors: List<Vendor>, onVendorClick: (String) -> Unit) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(vendors) { vendor ->
            SokoniCard(
                modifier = Modifier.width(280.dp),
                onClick = { onVendorClick(vendor.vendorId) }
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = vendor.storeName, style = MaterialTheme.typography.titleMedium)
                    Text(text = vendor.category, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
fun ProductList(products: List<Product>, onProductClick: (String) -> Unit) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(products) { product ->
            SokoniCard(
                modifier = Modifier.width(160.dp),
                onClick = { onProductClick(product.productId) }
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Surface(
                        modifier = Modifier.fillMaxWidth().height(100.dp),
                        shape = MaterialTheme.shapes.small,
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {}
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = product.name, style = MaterialTheme.typography.titleSmall, maxLines = 1)
                    if (product.variants.isNotEmpty()) {
                        Text(text = "KSh ${product.variants[0].price}", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomePreview() {
    SokoniTheme {
        HomeContent(
            uiState = HomeUiState(
                categories = listOf(Category(name = "Water"), Category(name = "Gas")),
                nearbyVendors = listOf(Vendor(storeName = "MJC Store", category = "Water")),
                popularProducts = listOf(Product(name = "20L Water"))
            ),
            onNavigateToSearch = {},
            onNavigateToVendor = {},
            onNavigateToProduct = {}
        )
    }
}
