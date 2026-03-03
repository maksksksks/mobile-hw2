package com.example.spacex.GUI

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.spacex.ViewModel.LoginUiEvent
import com.example.spacex.ViewModel.LoginViewModel
import com.example.spacex.ViewModel.LoginViewModelFactory

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = viewModel(factory = LoginViewModelFactory),
    onLoginSuccess: () -> Unit,
    onCapabilityChange: (Boolean) -> Unit
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is LoginUiEvent.LoginSuccessEvent -> onLoginSuccess()
            }
        }
    }

    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .safeContentPadding()
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Spacer(Modifier.height(40.dp))

        Text(
            text = "Вход в аккаунт",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(Modifier.height(20.dp))

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            TextField(
                value = state.username,
                onValueChange = { viewModel.onUsernameChanged(it) },
                label = { Text("e-mail") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                singleLine = true,
                isError = state.error != null,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    cursorColor = MaterialTheme.colorScheme.primary,
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary
                )
            )

            Spacer(Modifier.height(8.dp))

            TextField(
                visualTransformation = PasswordVisualTransformation(),
                value = state.password,
                onValueChange = { viewModel.onPasswordChanged(it) },
                label = { Text("password") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                singleLine = true,
                isError = state.error != null,
                supportingText = { state.error?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    cursorColor = MaterialTheme.colorScheme.primary,
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary
                )
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Button(
                onClick = { viewModel.onLoginClick() },
                enabled = state.isLoginButtonActive,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                modifier = Modifier
                    .height(50.dp)
                    .width(200.dp)
            ) {
                Text("Войти")
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = { viewModel.onRegisterClick() },
                enabled = state.isLoginButtonActive,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                modifier = Modifier
                    .height(50.dp)
                    .width(200.dp)
            ) {
                Text("Зарегистрироваться")
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = { onCapabilityChange(true) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary
                ),
                modifier = Modifier
                    .height(50.dp)
                    .width(200.dp)
            ) {
                Text("Просмотреть возможности")
            }
        }
        Spacer(Modifier.height(40.dp))
    }
}