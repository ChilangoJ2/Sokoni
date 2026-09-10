package com.jay.sokoni.ui.auth.login

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jay.sokoni.ui.auth.AuthUiState
import com.jay.sokoni.ui.auth.AuthViewModel
import com.jay.sokoni.ui.components.SokoniPrimaryButton
import com.jay.sokoni.ui.components.SokoniTextField
import com.jay.sokoni.ui.theme.SokoniTheme

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Success) {
            onLoginSuccess()
        }
    }

    LoginContent(
        uiState = uiState,
        onLoginClick = { email, password -> viewModel.login(email, password) },
        onNavigateToRegister = onNavigateToRegister
    )
}

@Composable
fun LoginContent(
    uiState: AuthUiState,
    onLoginClick: (String, String) -> Unit,
    onNavigateToRegister: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Welcome to Sokoni",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(32.dp))

        SokoniTextField(
            value = email,
            onValueChange = { email = it },
            label = "Email"
        )
        Spacer(modifier = Modifier.height(16.dp))

        SokoniTextField(
            value = password,
            onValueChange = { password = it },
            label = "Password"
        )
        Spacer(modifier = Modifier.height(32.dp))

        if (uiState is AuthUiState.Loading) {
            CircularProgressIndicator()
        } else {
            SokoniPrimaryButton(
                text = "Login",
                onClick = { onLoginClick(email, password) }
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextButton(onClick = onNavigateToRegister) {
                Text("Don't have an account? Register")
            }
        }

        if (uiState is AuthUiState.Error) {
            Text(
                text = uiState.message,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginPreview() {
    SokoniTheme {
        LoginContent(
            uiState = AuthUiState.Idle,
            onLoginClick = { _, _ -> },
            onNavigateToRegister = {}
        )
    }
}
