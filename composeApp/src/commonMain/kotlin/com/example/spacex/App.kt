package com.example.spacex

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.spacex.Data.Launch
import com.example.spacex.Data.Rockets
import com.example.spacex.GUI.*
import com.example.spacex.Repository.LaunchesRepository
import com.example.spacex.Theme.AppTheme
import com.example.spacex.ViewModel.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ScreensCapabilityCarousel(
    onCapabilityChange: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { 4 })
    val scope = rememberCoroutineScope()

    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize()
    ) { page ->
        when (page) {
            0 -> FirstScreen(
                onNext = { scope.launch { pagerState.animateScrollToPage(page + 1) } },
                onCapabilityChange = { onCapabilityChange() }
            )
            1 -> SecondScreen(
                onNext = { scope.launch { pagerState.animateScrollToPage(page + 1) } },
                onCapabilityChange = { onCapabilityChange() }
            )
            2 -> ThirdScreen(
                onNext = { scope.launch { pagerState.animateScrollToPage(page + 1) } },
                onCapabilityChange = { onCapabilityChange() }
            )
            3 -> FortyScreen(
                onNext = { onCapabilityChange() },
                onCapabilityChange = { onCapabilityChange() }
            )
        }
    }
}

@Composable
fun BottomNavigationBar(
    currentPage: Int,
    onTabSelected: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        val items = remember { listOf("Launches", "Rockets", "SpaceX", "Profile") }

        items.forEachIndexed { index, title ->
            val isSelected = index == currentPage
            Text(
                text = title,
                color = if (isSelected)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = if (isSelected) 16.sp else 14.sp,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onTabSelected(index) }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainCarousel(
    onLogout: () -> Unit,
) {
    val pagerState = rememberPagerState(pageCount = { 4 })
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        LaunchesRepository.init()
    }

    val mainViewModel: MainViewModel = viewModel(factory = MainViewModelFactory)
    val rocketsViewModel: RocketsViewModel = viewModel(factory = RocketsViewModelFactory)

    var selectedLaunch by remember { mutableStateOf<Launch?>(null) }
    var selectedRocket by remember { mutableStateOf<Rockets?>(null) }

    if (selectedLaunch != null) {
        BackHandler(enabled = true) {
            selectedLaunch = null
        }
        DetailsMissionScreen(
            launch = selectedLaunch!!,
            onBack = { selectedLaunch = null }
        )
    } else if (selectedRocket != null) {
        RocketDetailsScreen(rocket = selectedRocket!!, onBack = { selectedRocket = null })
    } else {
        Scaffold(
            bottomBar = {
                BottomNavigationBar(
                    currentPage = pagerState.currentPage,
                    onTabSelected = { index ->
                        scope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    }
                )
            }
        ) { padding ->
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) { page ->
                when (page) {
                    0 -> MainScreen(
                        viewModel = mainViewModel,
                        onMissionClick = { launch -> selectedLaunch = launch }
                    )
                    1 -> RocketsScreen(
                        viewModel = rocketsViewModel,
                        onRocketClick = { selectedRocket = it }
                    )
                    2 -> CompanyScreen()
                    3 -> ProfileScreen(
                        onLogout = onLogout,
                    )
                }
            }
        }
    }
}


@Composable
@Preview
fun App() {
    val profileViewModel: ProfileViewModel = viewModel(factory = ProfileViewModelFactory)
    val profileState by profileViewModel.state.collectAsState()

    val isDarkTheme = when (profileState.selectedTheme) {
        AppTheme.DARK -> true
        AppTheme.LIGHT -> false
        AppTheme.SYSTEM -> isSystemInDarkTheme()
    }

    AppTheme(darkTheme = isDarkTheme) {
        var capability by remember { mutableStateOf(true) }
        var login by remember { mutableStateOf(false) }

        val loginViewModel: LoginViewModel = viewModel(factory = LoginViewModelFactory)

        when {
            capability -> {
                ScreensCapabilityCarousel(
                    onCapabilityChange = { capability = false }
                )
            }
            !login -> {
                LaunchedEffect(Unit) {
                    loginViewModel.events.collect { event ->
                        when (event) {
                            is LoginUiEvent.LoginSuccessEvent -> login = true
                        }
                    }
                }

                LoginScreen(
                    viewModel = loginViewModel,
                    onLoginSuccess = { login = true },
                    onCapabilityChange = { capability = true }
                )
            }
            else -> {
                MainCarousel(
                    onLogout = {
                        login = false
                        profileViewModel.logout()
                    }
                )
            }
        }
    }
}