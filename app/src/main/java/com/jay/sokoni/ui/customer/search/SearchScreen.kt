package com.jay.sokoni.ui.customer.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jay.sokoni.domain.model.Product
import com.jay.sokoni.ui.components.SokoniCard
import com.jay.sokoni.ui.components.SokoniLoading
import com.jay.sokoni.ui.components.SokoniTextField
import com.jay.sokoni.ui.theme.SokoniTheme

@Composable
fun SearchScreen(
    onNavigateBack: () -> Unit,
    onProductClick: (String) -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    SearchContent(
        uiState = uiState,
        onQueryChange = { viewModel.onQueryChange(it) },
        onNavigateBack = onNavigateBack,
        onProductClick = onProductClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchContent(
    uiState: SearchUiState,
    onQueryChange: (String) -> Unit,
    onNavigateBack: () -> Unit,
    onProductClick: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    SokoniTextField(
                        value = uiState.query,
                        onValueChange = onQueryChange,
                        label = "Search products..."
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            if (uiState.isLoading) {
                SokoniLoading()
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(uiState.results) { product ->
                        SearchProductItem(product = product, onClick = { onProductClick(product.productId) })
                    }
                }
            }
        }
    }
}

@Composable
fun SearchProductItem(product: Product, onClick: () -> Unit) {
    SokoniCard(onClick = onClick) {
        Row(modifier = Modifier.padding(12.dp).fillMaxWidth()) {
            Surface(
                modifier = Modifier.size(80.dp),
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {}
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = product.name, style = MaterialTheme.typography.titleMedium)
                Text(text = product.category, style = MaterialTheme.typography.bodySmall)
                if (product.variants.isNotEmpty()) {
                    Text(
                        text = "KSh ${product.variants[0].price}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SearchPreview() {
    SokoniTheme {
        SearchContent(
            uiState = SearchUiState(
                query = "Water",
                results = listOf(Product(name = "20L Water", category = "Water"))
            ),
            onQueryChange = {},
            onNavigateBack = {},
            onProductClick = {}
        )
    }
}
