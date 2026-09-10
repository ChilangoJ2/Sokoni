package com.jay.sokoni.ui.vendor.products

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jay.sokoni.domain.model.Product
import com.jay.sokoni.domain.model.ProductVariant
import com.jay.sokoni.ui.components.SokoniPrimaryButton
import com.jay.sokoni.ui.components.SokoniTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditProductScreen(
    vendorId: String,
    productId: String? = null,
    onNavigateBack: () -> Unit,
    viewModel: ProductViewModel = hiltViewModel()
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    
    val uiState by viewModel.uiState.collectAsState()

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
                onClick = {
                    val product = Product(
                        productId = productId ?: "",
                        vendorId = vendorId,
                        name = name,
                        description = description,
                        category = category,
                        variants = listOf(ProductVariant(name = "Standard", price = price.toDoubleOrNull() ?: 0.0))
                    )
                    viewModel.saveProduct(product)
                    onNavigateBack()
                }
            )
        }
    }
}
