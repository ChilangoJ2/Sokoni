package com.jay.sokoni.ui.vendor.products

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jay.sokoni.domain.model.Product
import com.jay.sokoni.domain.model.ProductVariant
import com.jay.sokoni.ui.components.SokoniPrimaryButton
import com.jay.sokoni.ui.components.SokoniTextField
import com.jay.sokoni.ui.theme.SokoniTheme

@Composable
fun AddEditProductScreen(
    vendorId: String,
    productId: String? = null,
    onNavigateBack: () -> Unit,
    viewModel: ProductViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    AddEditProductContent(
        productId = productId,
        onNavigateBack = onNavigateBack,
        onSaveProduct = { name, desc, cat, price ->
            val product = Product(
                productId = productId ?: "",
                vendorId = vendorId,
                name = name,
                description = desc,
                category = cat,
                variants = listOf(ProductVariant(name = "Standard", price = price.toDoubleOrNull() ?: 0.0))
            )
            viewModel.saveProduct(product)
            onNavigateBack()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditProductContent(
    productId: String?,
    onNavigateBack: () -> Unit,
    onSaveProduct: (String, String, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (productId == null) "Add Product" else "Edit Product") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            SokoniTextField(value = name, onValueChange = { name = it }, label = "Product Name")
            Spacer(modifier = Modifier.height(16.dp))
            SokoniTextField(value = description, onValueChange = { description = it }, label = "Description")
            Spacer(modifier = Modifier.height(16.dp))
            SokoniTextField(value = category, onValueChange = { category = it }, label = "Category")
            Spacer(modifier = Modifier.height(16.dp))
            SokoniTextField(value = price, onValueChange = { price = it }, label = "Base Price (KSh)")

            Spacer(modifier = Modifier.height(32.dp))

            SokoniPrimaryButton(
                text = "Save Product",
                onClick = { onSaveProduct(name, description, category, price) }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddEditProductPreview() {
    SokoniTheme {
        AddEditProductContent(
            productId = null,
            onNavigateBack = {},
            onSaveProduct = { _, _, _, _ -> }
        )
    }
}
