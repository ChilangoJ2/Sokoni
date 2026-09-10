package com.jay.sokoni.ui.customer.orders

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jay.sokoni.ui.components.RatingBar
import com.jay.sokoni.ui.components.SokoniPrimaryButton
import com.jay.sokoni.ui.components.SokoniTextField
import com.jay.sokoni.ui.theme.SokoniTheme

@Composable
fun OrderRatingScreen(
    vendorId: String,
    orderId: String,
    onNavigateBack: () -> Unit,
    viewModel: OrderRatingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isSubmitted) {
        if (uiState.isSubmitted) {
            onNavigateBack()
        }
    }

    OrderRatingContent(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onSubmit = { rating, review -> viewModel.submitRating(vendorId, orderId, rating, review) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderRatingContent(
    uiState: OrderRatingUiState,
    onNavigateBack: () -> Unit,
    onSubmit: (Int, String) -> Unit
) {
    var rating by remember { mutableIntStateOf(5) }
    var review by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Rate Order") },
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
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "How was your experience?", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(24.dp))
            
            RatingBar(rating = rating, onRatingChange = { rating = it })
            
            Spacer(modifier = Modifier.height(32.dp))
            
            SokoniTextField(
                value = review,
                onValueChange = { review = it },
                label = "Write a review (Optional)",
                modifier = Modifier.height(120.dp)
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            if (uiState.isLoading) {
                CircularProgressIndicator()
            } else {
                SokoniPrimaryButton(
                    text = "Submit Review",
                    onClick = { onSubmit(rating, review) }
                )
            }
            
            uiState.error?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 16.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OrderRatingPreview() {
    SokoniTheme {
        OrderRatingContent(
            uiState = OrderRatingUiState(),
            onNavigateBack = {},
            onSubmit = { _, _ -> }
        )
    }
}
