package com.example.mobile_hw2

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MaterialTheme {
        var showSecondScreen by remember { mutableStateOf(false) }

        if (showSecondScreen) {
            SecondScreen(onBackClick = { showSecondScreen = false })
        } else {
            FirstScreen(onNextClick = { showSecondScreen = true })
        }
    }
}