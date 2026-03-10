package com.example.spacex.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.spacex.Data.Launch
import com.example.spacex.Data.LaunchFilter
import com.example.spacex.Repository.LaunchesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.reflect.KClass

val MainViewModelFactory = object : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: KClass<T>, extras: CreationExtras): T {
        @Suppress("UNCHECKED_CAST")
        return MainViewModel(LaunchesRepository) as T
    }
}

data class MainUiState(
    val launches: List<Launch> = emptyList(),
    val isLoading: Boolean = true, // Первичная загрузка
    val isLoadingMore: Boolean = false, // Пагинация
    val endReached: Boolean = false,
    val currentFilter: LaunchFilter = LaunchFilter.ALL,
    val searchQuery: String = "",
    val errorMessage: String? = null // Если не null, показываем ошибку
)

class MainViewModel(
    private val repository: LaunchesRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MainUiState())
    val state: StateFlow<MainUiState> = _state.asStateFlow()

    private var allLaunches: List<Launch> = emptyList()
    private var currentPage = 0
    private val pageSize = 20

    init {
        observeData()
        loadData() // Запускаем первичную загрузку
    }

    private fun observeData() {
        viewModelScope.launch {
            repository.launchesFlow.collect { launches ->
                allLaunches = launches.distinctBy { it.flight_number }
                // Если данные пришли, сбрасываем ошибку и применяем фильтры
                _state.update { it.copy(errorMessage = null) }
                applyFilters(resetPagination = true)
            }
        }
    }

    // Первичная загрузка или повторная попытка
    fun loadData() {
        // Если уже грузим или данные уже есть (не ошибка), не перезагружаем
        if (_state.value.isLoading && allLaunches.isNotEmpty()) return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                // Переключаемся на IO поток для сети и базы данных
                withContext(Dispatchers.IO) {
                    repository.init()
                }
                // Успех: observeData обновит список автоматически
            } catch (e: Exception) {
                // Ошибка: показываем экран ошибки
                _state.update { it.copy(isLoading = false, errorMessage = e.message ?: "Неизвестная ошибка") }
            }
        }
    }

    fun onFilterSelected(filter: LaunchFilter) {
        _state.update { it.copy(currentFilter = filter) }
        applyFilters(resetPagination = true)
    }

    fun onSearchQueryChanged(query: String) {
        _state.update { it.copy(searchQuery = query) }
        applyFilters(resetPagination = true)
    }

    fun loadNextPage() {
        if (_state.value.isLoadingMore || _state.value.endReached || _state.value.isLoading) return

        viewModelScope.launch {
            _state.update { it.copy(isLoadingMore = true) }

            // Имитация задержки для UI (можно убрать в релизе)
            delay(300)

            currentPage++
            applyFilters(resetPagination = false)

            _state.update { it.copy(isLoadingMore = false) }
        }
    }

    fun onFavoriteClicked(flightNumber: Long) {
        viewModelScope.launch {
            // Выполняем на фоне
            withContext(Dispatchers.IO) {
                val currentLaunch = allLaunches.find { it.flight_number == flightNumber }
                val newStatus = !(currentLaunch?.is_favorite ?: false)
                repository.toggleFavorite(flightNumber, newStatus)
            }
        }
    }

    private fun applyFilters(resetPagination: Boolean) {
        if (resetPagination) {
            currentPage = 0
        }

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

        val finalFullList = if (query.isBlank()) {
            filteredByType
        } else {
            filteredByType.filter { launch ->
                launch.mission_name?.contains(query, ignoreCase = true) == true ||
                        launch.flight_number?.toString()?.contains(query) == true ||
                        launch.launch_year?.contains(query) == true
            }
        }

        val itemsToSkip = currentPage * pageSize
        val paginatedList = finalFullList.take(itemsToSkip + pageSize)
        val endReached = paginatedList.size >= finalFullList.size

        _state.update {
            it.copy(
                launches = paginatedList,
                isLoading = false, // Гасим лоадер после применения фильтров
                endReached = endReached
            )
        }
    }
}