package com.example.mobile_hw2

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.Font
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.PasswordVisualTransformation
import kotlinx.coroutines.launch
import mobilehw2.composeapp.generated.resources.Res
import mobilehw2.composeapp.generated.resources.atziluth_script

@Composable
fun SecondScreen(onBackClick: () -> Unit) {
    BackHandler() {
        onBackClick()
    }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .background(Color(20, 20, 20))
                .fillMaxSize()
                .safeContentPadding()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            var email by remember { mutableStateOf("") }
            var password by remember { mutableStateOf("") }
            val exoticFont = FontFamily(
                Font(
                    resource = Res.font.atziluth_script,
                    weight = FontWeight.Normal
                )
            )
            Spacer(modifier = Modifier.height(50.dp))

            Text(
                text = "CinemaКино",
                fontSize = 60.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontFamily = exoticFont
            )

            Spacer(modifier = Modifier.height(30.dp))

            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("e-mail") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Red,
                    unfocusedIndicatorColor = Color.Gray,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            TextField(
                visualTransformation = PasswordVisualTransformation(),
                value = password,
                onValueChange = { password = it },
                label = { Text("password") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Red,
                    unfocusedIndicatorColor = Color.Gray,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )

            )

            Spacer(modifier = Modifier.height(50.dp))

            Button(
                onClick = {
                    if (!email.isEmpty() && !password.isEmpty()) {
                        scope.launch {
                            snackbarHostState.showSnackbar("Ищу аккаунт...")
                        }
                    } else {
                        scope.launch {
                            snackbarHostState.showSnackbar("Сначала введите данные!")
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red,
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .height(50.dp)
                    .width(250.dp)
            ) {
                Text("Войти")
            }
            Spacer(modifier = Modifier.height(30.dp))

            Text(
                text = "Зарегистрируйтесь, если нет аккаунта",
                color = Color.LightGray,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        scope.launch {
                            snackbarHostState.showSnackbar("Создаю аккаунт...")
                        }
                    }
            )
        }
    }
}
