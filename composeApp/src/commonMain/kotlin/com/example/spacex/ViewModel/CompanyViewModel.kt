package com.example.spacex.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.spacex.Data.CompanyInfo
import com.example.spacex.Data.HistoryEvent
import com.example.spacex.Repository.LaunchesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

val CompanyViewModelFactory = object : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: KClass<T>, extras: CreationExtras): T {
        @Suppress("UNCHECKED_CAST")
        return CompanyViewModel(LaunchesRepository) as T
    }
}

enum class CompanyTab(val title: String) {
    INFO("Инфо"),
    HISTORY("История")
}

data class CompanyUiState(
    val companyInfo: CompanyInfo? = null,
    val history: List<HistoryEvent> = emptyList(),
    val currentTab: CompanyTab = CompanyTab.INFO,
    val isLoading: Boolean = true,
    val error: String? = null
)

class CompanyViewModel(
    private val repository: LaunchesRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CompanyUiState())
    val state: StateFlow<CompanyUiState> = _state.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                // Загружаем оба источника параллельно на фоне
                val infoDeferred = async(Dispatchers.IO) { repository.getCompanyInfo() }
                val historyDeferred = async(Dispatchers.IO) { repository.getHistory() }

                val info = infoDeferred.await()
                val history = historyDeferred.await()

                _state.update {
                    it.copy(
                        companyInfo = info,
                        history = history,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message ?: "Ошибка загрузки данных") }
            }
        }
    }

    fun onTabSelected(tab: CompanyTab) {
        _state.update { it.copy(currentTab = tab) }
    }
}