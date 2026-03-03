package com.example.spacex.GUI

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.spacex.ViewModel.AppTheme
import com.example.spacex.ViewModel.ProfileTab
import com.example.spacex.ViewModel.ProfileViewModel
import com.example.spacex.ViewModel.ProfileViewModelFactory
import androidx.compose.ui.graphics.Color


@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = viewModel(factory = ProfileViewModelFactory),
    onLogout: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = state.user?.username ?: "Гость",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Button(
                onClick = {
                    onLogout()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text("Выход")
            }
        }

        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            Text(
                "Тема приложения",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                AppTheme.entries.forEach { theme ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(
                                if (state.selectedTheme == theme)
                                    MaterialTheme.colorScheme.primary
                                else
                                    Color.Transparent,
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { viewModel.onThemeSelected(theme) }
                            .padding(horizontal = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = theme.title,
                            color = if (state.selectedTheme == theme)
                                MaterialTheme.colorScheme.onPrimary
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        ScrollableTabRow(
            selectedTabIndex = state.currentTab.ordinal,
            contentColor = MaterialTheme.colorScheme.onBackground,
            edgePadding = 16.dp,
            divider = {},
            containerColor = Color.Transparent
        ) {
            ProfileTab.entries.forEach { tab ->
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

        Box(modifier = Modifier.weight(1f)) {
            when (state.currentTab) {
                ProfileTab.LAUNCHES -> {
                    if (state.favoriteLaunches.isEmpty()) {
                        EmptyFavoritesText()
                    } else {
                        LazyColumn {
                            items(state.favoriteLaunches, key = { it.flight_number!! }) { launch ->
                                LaunchItem(
                                    launch = launch,
                                    onClick = { },
                                    onFavoriteClick = { viewModel.onLaunchFavoriteClicked(launch.flight_number!!) }
                                )
                            }
                        }
                    }
                }
                ProfileTab.ROCKETS -> {
                    if (state.favoriteRockets.isEmpty()) {
                        EmptyFavoritesText()
                    } else {
                        LazyColumn {
                            items(state.favoriteRockets, key = { it.id!! }) { rocket ->
                                RocketItem(
                                    rocket = rocket,
                                    onClick = { },
                                    onFavoriteClick = { viewModel.onRocketFavoriteClicked(rocket.id!!) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyFavoritesText() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            "Список пуст",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 16.sp
        )
    }
}
