package com.example.spacex.GUI

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.example.spacex.Data.Rockets
import com.example.spacex.ViewModel.RocketFilter
import com.example.spacex.ViewModel.RocketsViewModel
import com.example.spacex.ViewModel.RocketsViewModelFactory
import com.example.spacex.shareText

@Composable
fun RocketsScreen(
    viewModel: RocketsViewModel = viewModel(factory = RocketsViewModelFactory),
    onRocketClick: (Rockets) -> Unit
) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Поиск
        OutlinedTextField(
            value = state.searchQuery,
            onValueChange = { viewModel.onSearchQueryChanged(it) },
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            placeholder = { Text("Поиск по названию ракеты...", color = MaterialTheme.colorScheme.onSurfaceVariant) },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                cursorColor = MaterialTheme.colorScheme.primary
            )
        )

        // Табы
        ScrollableTabRow(
            selectedTabIndex = state.currentFilter.ordinal,
            contentColor = MaterialTheme.colorScheme.onBackground,
            edgePadding = 16.dp,
            divider = {},
            containerColor = Color.Transparent
        ) {
            RocketFilter.entries.forEach { filter ->
                Tab(
                    selected = state.currentFilter == filter,
                    onClick = { viewModel.onFilterSelected(filter) },
                    text = {
                        Text(
                            text = filter.title,
                            color = if (state.currentFilter == filter) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                )
            }
        }

        // Состояния
        Box(modifier = Modifier.fillMaxSize()) {
            when {
                state.errorMessage != null -> {
                    ErrorState(message = state.errorMessage!!, onRetry = { viewModel.loadData() })
                }
                state.isLoading -> {
                    LoadingState()
                }
                state.rockets.isEmpty() -> {
                    EmptyState(query = state.searchQuery)
                }
                else -> {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(state.rockets, key = { it.id ?: it.hashCode() }) { rocket ->
                            RocketItem(
                                rocket = rocket,
                                onClick = { onRocketClick(rocket) },
                                onFavoriteClick = { rocket.id?.let { viewModel.onFavoriteClicked(it) } }
                            )
                        }
                    }
                }
            }
        }
    }
}

// --- Shared UI Components (можно вынести в отдельный файл) ---



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RocketDetailsScreen(
    rocket: Rockets,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        rocket.rocket_name ?: "Rocket Details",
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text("←", fontSize = 24.sp, color = MaterialTheme.colorScheme.onBackground)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                val imageUrl = rocket.flickr_images?.firstOrNull()
                if (!imageUrl.isNullOrEmpty()) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .clip(RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
            }
            item {
                Text(
                    text = rocket.description ?: "No description available.",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                IconButton(onClick = {
                    val shareMsg = """
                        🚀 SpaceX Rocket: ${rocket.rocket_name}    
                        First Launch Date: ${rocket.first_stage}
                        Status: ${rocket.active}
                        More info(wiki): ${rocket.wikipedia}"""
                        .trimIndent()
                    shareText(shareMsg)
                }) {
                    Text(
                        text = "↗",
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            item {
                DetailCard("Основные характеристики") {
                    DetailRow("Активен", if (rocket.active == true) "Да" else "Нет")
                    DetailRow("Первый полёт", rocket.first_flight ?: "-")
                    DetailRow("Страна", rocket.country ?: "-")
                    DetailRow("Стоимость запуска", "$${"%,d".format(rocket.cost_per_launch ?: 0)}")
                    DetailRow("Успешность", "${rocket.success_rate_pct ?: 0}%")
                }
            }
            item {
                DetailCard("Размеры") {
                    DetailRow("Высота", "${rocket.height?.meters ?: "-"} м")
                    DetailRow("Диаметр", "${rocket.diameter?.meters ?: "-"} м")
                    DetailRow("Масса", "${rocket.mass?.kg ?: "-"} кг")
                }
            }
            item {
                DetailCard("Первая ступень") {
                    DetailRow("Двигатели", rocket.first_stage?.engines?.toString())
                    DetailRow("Горючее (тонны)", rocket.first_stage?.fuel_amount_tons?.toString())
                    DetailRow("Время горения (сек)", rocket.first_stage?.burn_time_sec?.toString())
                    DetailRow("Многоразовая", if (rocket.first_stage?.reusable == true) "Да" else "Нет")
                }
            }
            item {
                DetailCard("Вторая ступень") {
                    DetailRow("Двигатели", rocket.second_stage?.engines?.toString())
                    DetailRow("Горючее (тонны)", rocket.second_stage?.fuel_amount_tons?.toString())
                }
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
fun DetailCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(16.dp)
    ) {
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        content()
    }
}

@Composable
fun DetailRow(label: String, value: String?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 14.sp
        )
        Text(
            text = value ?: "-",
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 14.sp
        )
    }
}