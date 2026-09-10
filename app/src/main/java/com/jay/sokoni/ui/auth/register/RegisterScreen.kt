package com.jay.sokoni.ui.auth.register

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jay.sokoni.domain.model.User
import com.jay.sokoni.ui.auth.AuthUiState
import com.jay.sokoni.ui.auth.AuthViewModel
import com.jay.sokoni.ui.components.SokoniPrimaryButton
import com.jay.sokoni.ui.components.SokoniTextField

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Success) {
            onRegisterSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Create Account",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(32.dp))

        SokoniTextField(
            value = name,
            onValueChange = { name = it },
            label = "Full Name"
        )
        Spacer(modifier = Modifier.height(16.dp))

        SokoniTextField(
            value = email,
            onValueChange = { email = it },
            label = "Email"
        )
        Spacer(modifier = Modifier.height(16.dp))

        SokoniTextField(
            value = phone,
            onValueChange = { phone = it },
            label = "Phone Number (M-Pesa enabled)"
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
                text = "Register",
                onClick = {
                    val user = User(
                        name = name,
                        email = email,
                        phone = phone
                    )
                    viewModel.register(user, password)
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextButton(onClick = onNavigateToLogin) {
                Text("Already have an account? Login")
            }
        }

        if (uiState is AuthUiState.Error) {
            Text(
                text = (uiState as AuthUiState.Error).message,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}
