package com.example.spacex.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.spacex.Data.Launch
import com.example.spacex.Data.LaunchFilter
import com.example.spacex.Repository.LaunchesRepository
import com.example.spacex.client
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

val MainViewModelFactory = object : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: KClass<T>, extras: CreationExtras): T {
        @Suppress("UNCHECKED_CAST")
        return MainViewModel(LaunchesRepository) as T
    }
}

data class MainUiState(
    val launches: List<Launch> = emptyList(),
    val isLoading: Boolean = true,
    val currentFilter: LaunchFilter = LaunchFilter.ALL,
    val searchQuery: String = ""
)

class MainViewModel(
    private val repository: LaunchesRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MainUiState())
    val state: StateFlow<MainUiState> = _state.asStateFlow()

    private var allLaunches: List<Launch> = emptyList()

    init {
        observeData()
    }

    private fun observeData() {
        viewModelScope.launch {
            repository.launchesFlow.collect { launches ->
                allLaunches = launches.distinctBy { it.flight_number }
                applyFilters()
            }
        }
    }

    fun onFilterSelected(filter: LaunchFilter) {
        _state.update { it.copy(currentFilter = filter) }
        applyFilters()
    }

    fun onSearchQueryChanged(query: String) {
        _state.update { it.copy(searchQuery = query) }
        applyFilters()
    }

    fun onFavoriteClicked(flightNumber: Long) {
        viewModelScope.launch {
            val currentLaunch = allLaunches.find { it.flight_number == flightNumber }
            val newStatus = !(currentLaunch?.is_favorite ?: false)
            repository.toggleFavorite(flightNumber, newStatus)
        }
    }

    private fun applyFilters() {
        val currentFilter = _state.value.currentFilter
        val query = _state.value.searchQuery

        val filteredByType = when (currentFilter) {
            LaunchFilter.ALL -> allLaunches

            LaunchFilter.PAST -> allLaunches.filter { it.upcoming != true }

            LaunchFilter.FUTURE -> allLaunches.filter { it.upcoming == true }

            LaunchFilter.LAST -> {
                allLaunches.filter { it.upcoming != true }
                    .maxByOrNull { it.launch_date_unix ?: 0L }
                    ?.let { listOf(it) } ?: emptyList()
            }

            LaunchFilter.NEXT -> {
                allLaunches.filter { it.upcoming == true }
                    .minByOrNull { it.launch_date_unix ?: Long.MAX_VALUE }
                    ?.let { listOf(it) } ?: emptyList()
            }
        }

        val finalList = if (query.isBlank()) {
            filteredByType
        } else {
            filteredByType.filter { launch ->
                launch.mission_name?.contains(query, ignoreCase = true) == true ||
                        launch.flight_number?.toString()?.contains(query) == true ||
                        launch.launch_year?.contains(query) == true
            }
        }

        _state.update { it.copy(launches = finalList, isLoading = false) }
    }
}