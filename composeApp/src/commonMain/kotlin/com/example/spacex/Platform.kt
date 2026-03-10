package com.example.spacex

import app.cash.sqldelight.db.SqlDriver
import androidx.compose.runtime.Composable
import io.ktor.client.HttpClient

expect val driverFactory: SqlDriver

@Composable
expect fun BackHandler(enabled: Boolean = true, onBack: () -> Unit)

expect fun createHttpClient(): HttpClient

val client by lazy { createHttpClient() }
