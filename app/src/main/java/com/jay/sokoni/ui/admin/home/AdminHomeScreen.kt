package com.jay.sokoni.ui.admin.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jay.sokoni.ui.theme.SokoniTheme

@Composable
fun AdminHomeScreen() {
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Admin Dashboard", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Platform overview and management.")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AdminHomePreview() {
    SokoniTheme {
        AdminHomeScreen()
    }
}
