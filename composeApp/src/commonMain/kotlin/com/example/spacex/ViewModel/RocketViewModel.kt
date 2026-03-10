package com.example.spacex.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.spacex.Data.Rockets
import com.example.spacex.Repository.LaunchesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.reflect.KClass

val RocketsViewModelFactory = object : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: KClass<T>, extras: CreationExtras): T {
        @Suppress("UNCHECKED_CAST")
        return RocketsViewModel(LaunchesRepository) as T
    }
}

enum class RocketFilter(val title: String) {
    ALL("Все"),
    ACTIVE("Активные"),
    INACTIVE("Неактивные")
}

data class RocketsUiState(
    val rockets: List<Rockets> = emptyList(),
    val isLoading: Boolean = true,
    val currentFilter: RocketFilter = RocketFilter.ALL,
    val searchQuery: String = "",
    val errorMessage: String? = null
)

class RocketsViewModel(
    private val repository: LaunchesRepository
) : ViewModel() {

    private val _state = MutableStateFlow(RocketsUiState())
    val state: StateFlow<RocketsUiState> = _state.asStateFlow()

    private var allRockets: List<Rockets> = emptyList()

    init {
        observeData()
        loadData()
    }

    private fun observeData() {
        viewModelScope.launch {
            repository.rocketsFlow.collect { rockets ->
                allRockets = rockets
                _state.update { it.copy(errorMessage = null) }
                applyFilters()
            }
        }
    }

    fun loadData() {
        if (_state.value.isLoading && allRockets.isNotEmpty()) return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                withContext(Dispatchers.IO) {
                    repository.init() // Убедимся, что данные загружены
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = e.message ?: "Ошибка загрузки ракет") }
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

    fun onFavoriteClicked(rocketId: Long) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val rocket = allRockets.find { it.id == rocketId }
                val newStatus = !(rocket?.is_favorite ?: false)
                repository.toggleRocketFavorite(rocketId, newStatus)
            }
        }
    }

    private fun applyFilters() {
        val query = _state.value.searchQuery
        val filter = _state.value.currentFilter

        val filtered = when (filter) {
            RocketFilter.ALL -> allRockets
            RocketFilter.ACTIVE -> allRockets.filter { it.active == true }
            RocketFilter.INACTIVE -> allRockets.filter { it.active != true }
        }

        val result = if (query.isBlank()) {
            filtered
        } else {
            filtered.filter { it.rocket_name?.contains(query, ignoreCase = true) == true }
        }

        _state.update { it.copy(rockets = result, isLoading = false) }
    }
}