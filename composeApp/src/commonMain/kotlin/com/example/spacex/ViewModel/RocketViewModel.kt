package com.example.spacex.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.spacex.Data.Rockets
import com.example.spacex.Repository.LaunchesRepository
import com.example.spacex.client
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

enum class RocketFilter(val title: String) {
    ALL("Все"),
    ACTIVE("Активные"),
    INACTIVE("Неактивные")
}

val RocketsViewModelFactory = object : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: KClass<T>, extras: CreationExtras): T {
        @Suppress("UNCHECKED_CAST")
        return RocketsViewModel(LaunchesRepository) as T
    }
}

data class RocketsState(
    val rockets: List<Rockets> = emptyList(),
    val isLoading: Boolean = true,
    val currentFilter: RocketFilter = RocketFilter.ALL,
    val searchQuery: String = ""
)

class RocketsViewModel(
    private val repository: LaunchesRepository
) : ViewModel() {

    private val _state = MutableStateFlow(RocketsState())
    val state: StateFlow<RocketsState> = _state.asStateFlow()

    private var allRockets: List<Rockets> = emptyList()

    init {
        observeRockets()
    }

    private fun observeRockets() {
        viewModelScope.launch {
            repository.rocketsFlow.collect { rockets ->
                allRockets = rockets
                applyFilters()
            }
        }
    }

    fun onFilterSelected(filter: RocketFilter) {
        _state.update { it.copy(currentFilter = filter) }
        applyFilters()
    }

    fun onSearchQueryChanged(query: String) {
        _state.update { it.copy(searchQuery = query) }
        applyFilters()
    }

    fun onFavoriteClicked(id: Long) {
        viewModelScope.launch {
            val current = allRockets.find { it.id?.toInt()?.toLong() == id }
            val newStatus = !(current?.is_favorite ?: false)
            repository.toggleRocketFavorite(id, newStatus)
        }
    }

    private fun applyFilters() {
        val currentFilter = _state.value.currentFilter
        val query = _state.value.searchQuery

        val filteredByType = when (currentFilter) {
            RocketFilter.ALL -> allRockets
            RocketFilter.ACTIVE -> allRockets.filter { it.active == true }
            RocketFilter.INACTIVE -> allRockets.filter { it.active != true }
        }

        val finalList = if (query.isBlank()) {
            filteredByType
        } else {
            filteredByType.filter { rocket ->
                rocket.rocket_name?.contains(query, ignoreCase = true) == true
            }
        }

        _state.update { it.copy(rockets = finalList, isLoading = false) }
    }
}