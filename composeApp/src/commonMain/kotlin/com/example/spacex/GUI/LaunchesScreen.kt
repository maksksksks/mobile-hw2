package com.example.spacex.GUI

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.example.spacex.Data.Launch
import com.example.spacex.Data.LaunchFilter
import com.example.spacex.ViewModel.MainViewModel
import com.example.spacex.ViewModel.MainViewModelFactory
import com.example.spacex.shareText

@Composable
fun MainScreen(
    viewModel: MainViewModel = viewModel(factory = MainViewModelFactory),
    onMissionClick: (Launch) -> Unit
) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background) // Фон экрана
    ) {

        OutlinedTextField(
            value = state.searchQuery,
            onValueChange = { viewModel.onSearchQueryChanged(it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            placeholder = {
                Text(
                    "Поиск по названию или году...",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            leadingIcon = {
                Text("🔍", fontSize = 18.sp)
            },
            trailingIcon = {
                if (state.searchQuery.isNotEmpty()) {
                    TextButton(onClick = { viewModel.onSearchQueryChanged("") }) {
                        Text(
                            "✕",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 18.sp
                        )
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.outline,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedTextColor = MaterialTheme.colorScheme.onBackground,
                unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                cursorColor = MaterialTheme.colorScheme.primary
            )
        )

        ScrollableTabRow(
            selectedTabIndex = state.currentFilter.ordinal,
            contentColor = MaterialTheme.colorScheme.onBackground,
            edgePadding = 16.dp,
            divider = {},
            containerColor = Color.Transparent
        ) {
            LaunchFilter.entries.forEach { filter ->
                Tab(
                    selected = state.currentFilter == filter,
                    onClick = { viewModel.onFilterSelected(filter) },
                    text = {
                        Text(
                            text = filter.title,
                            color = if (state.currentFilter == filter)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                )
            }
        }

        if (state.isLoading) {
            Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else {
            if (state.launches.isEmpty()) {
                Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (state.searchQuery.isNotEmpty()) "Ничего не найдено" else "Нет данных",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 16.sp
                    )
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(
                        items = state.launches,
                        key = { launch -> launch.flight_number ?: launch.hashCode() }
                    ) { launch ->
                        LaunchItem(
                            launch = launch,
                            onClick = { onMissionClick(launch) },
                            onFavoriteClick = {
                                viewModel.onFavoriteClicked(launch.flight_number!!)
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsMissionScreen(
    launch: Launch,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        launch.mission_name ?: "Mission Details",
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text("←", fontSize = 24.sp, color = MaterialTheme.colorScheme.onBackground)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        val shareMsg = """
                            🚀 SpaceX Mission: ${launch.mission_name}
                            Date: ${launch.launch_date_utc}
                            Result: ${if (launch.launch_success == true) "Success" else "Failed"}
                            Details: ${launch.details}
                        """.trimIndent()
                        shareText(shareMsg)
                    }) {
                        Text(
                            text = "↗",
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
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
                val patchUrl = launch.links?.mission_patch
                if (!patchUrl.isNullOrEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = patchUrl,
                            contentDescription = "Mission Patch",
                            modifier = Modifier
                                .size(180.dp)
                                .clip(RoundedCornerShape(16.dp)),
                            contentScale = ContentScale.Fit
                        )
                    }
                }
            }

            item {
                Text(
                    text = launch.details ?: "Описание миссии отсутствует.",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            item {
                DetailCard("Запуск") {
                    DetailRow("Номер полета", launch.flight_number?.toString())
                    DetailRow("Год", launch.launch_year)
                    DetailRow("Дата (UTC)", launch.launch_date_utc)
                    DetailRow("Стартовая площадка", launch.launch_site?.site_name_long)

                    val statusText = when {
                        launch.upcoming == true -> "Запланирован"
                        launch.launch_success == true -> "Успешен"
                        else -> "Неудача"
                    }
                    val statusColor = when {
                        launch.upcoming == true -> MaterialTheme.colorScheme.tertiary
                        launch.launch_success == true -> Color(0xFF4CAF50)
                        else -> MaterialTheme.colorScheme.error
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Статус", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
                        Text(statusText, color = statusColor, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }

                    if (launch.launch_success == false && launch.launch_failure_details != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Причина неудачи: ${launch.launch_failure_details?.reason}",
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            item {
                launch.rocket?.let { rocket ->
                    DetailCard("Ракета") {
                        DetailRow("Название", rocket.rocket_name)
                        DetailRow("Тип", rocket.rocket_type)

                        rocket.first_stage?.cores?.firstOrNull()?.let { core ->
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Ядро первой ступени:", fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                            DetailRow("Серийный номер", core.core_serial)
                            DetailRow("Повторное использование", if (core.reused == true) "Да" else "Нет")
                            DetailRow("Посадка", if (core.land_success == true) "Успешна" else (if (core.landing_intent == true) "Неудача" else "Не планировалась"))
                            DetailRow("Тип посадки", core.landing_type)
                        }
                    }
                }
            }

            item {
                launch.rocket?.second_stage?.payloads?.let { payloads ->
                    DetailCard("Полезная нагрузка (${payloads.size})") {
                        payloads.forEachIndexed { index, payload ->
                            if (index > 0) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(1.dp)
                                        .background(MaterialTheme.colorScheme.outlineVariant)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                            DetailRow("ID", payload.payload_id)
                            DetailRow("Тип", payload.payload_type)
                            payload.customers?.firstOrNull()?.let { DetailRow("Заказчик", it) }
                            DetailRow("Масса", "${payload.payload_mass_kg ?: "-"} кг")
                            DetailRow("Орбита", payload.orbit)
                        }
                    }
                }
            }

            item {
                launch.links?.let { links ->
                    DetailCard("Ссылки") {
                        links.video_link?.let { DetailRow("Видео", "YouTube") }
                        links.article_link?.let { DetailRow("Статья", "Ссылка") }
                        links.wikipedia?.let { DetailRow("Wikipedia", "Ссылка") }
                        links.presskit?.let { DetailRow("Пресс-кит", "Ссылка") }
                        links.reddit_campaign?.let { DetailRow("Reddit", "Обсуждение") }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}