package com.example.spacex.GUI

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.spacex.Data.CompanyInfo
import com.example.spacex.Data.HistoryEvent
import com.example.spacex.ViewModel.CompanyTab
import com.example.spacex.ViewModel.CompanyViewModel
import com.example.spacex.ViewModel.CompanyViewModelFactory

@Composable
fun CompanyScreen(
    viewModel: CompanyViewModel = viewModel(factory = CompanyViewModelFactory)
) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        ScrollableTabRow(
            selectedTabIndex = state.currentTab.ordinal,
            contentColor = MaterialTheme.colorScheme.onBackground,
            edgePadding = 16.dp,
            divider = {},
            containerColor = Color.Transparent
        ) {
            CompanyTab.entries.forEach { tab ->
                Tab(
                    selected = state.currentTab == tab,
                    onClick = { viewModel.onTabSelected(tab) },
                    text = {
                        Text(
                            text = tab.title,
                            color = if (state.currentTab == tab)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                )
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            when {
                state.error != null -> {
                    ErrorState(message = state.error!!, onRetry = { viewModel.loadData() })
                }
                state.isLoading -> {
                    LoadingState()
                }
                else -> {
                    when (state.currentTab) {
                        CompanyTab.INFO -> CompanyInfoContent(info = state.companyInfo)
                        CompanyTab.HISTORY -> HistoryListContent(history = state.history)
                    }
                }
            }
        }
    }
}

@Composable
fun CompanyInfoContent(info: CompanyInfo?) {
    if (info == null) {
        EmptyState(query = "")
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // ... (Ваш код отображения информации о компании без изменений) ...
        item {
            Text(
                text = info.name ?: "SpaceX",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = info.summary ?: "",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            DetailCard("Основные данные") {
                DetailRow(label = "Основатель", value = info.founder)
                DetailRow(label = "Год основания", value = info.founded?.toString())
                DetailRow(label = "Сотрудники", value = info.employees?.toString())
                DetailRow(label = "Оценка стоимости", value = "$${"%,d".format(info.valuation ?: 0)}")
            }
        }
        // ... остальные карточки ...
        item {
            DetailCard("Руководство") {
                DetailRow(label = "CEO", value = info.ceo)
                DetailRow(label = "CTO", value = info.cto)
                DetailRow(label = "COO", value = info.coo)
            }
        }
    }
}

@Composable
fun HistoryListContent(history: List<HistoryEvent>) {
    if (history.isEmpty()) {
        EmptyState(query = "")
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(history, key = { it.id ?: it.hashCode() }) { event ->
                HistoryItem(event = event)
            }
        }
    }
}

// DetailCard и DetailRow используются из предоставленного вами кода или общих компонентов

@Composable
fun HistoryItem(event: HistoryEvent) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(16.dp)
    ) {
        Text(
            text = event.title ?: "Unknown Event",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(4.dp))

        val dateText = event.event_date_utc?.substringBefore("T") ?: "-"
        Text(
            text = dateText,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = event.details ?: "",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface
        )

        event.links?.article?.let { link ->
            if (link.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Читать статью",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable {
                    }
                )
            }
        }
    }
}