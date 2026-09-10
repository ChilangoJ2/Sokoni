package com.jay.sokoni.ui.vendor.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jay.sokoni.ui.theme.SokoniTheme

@Composable
fun VendorHomeScreen() {
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Vendor Dashboard", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Manage your orders and products here.")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun VendorHomePreview() {
    SokoniTheme {
        VendorHomeScreen()
    }
}
