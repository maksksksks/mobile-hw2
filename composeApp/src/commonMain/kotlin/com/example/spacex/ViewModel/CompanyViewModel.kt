package com.example.spacex.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.spacex.Data.CompanyInfo
import com.example.spacex.Data.HistoryEvent
import com.example.spacex.Repository.LaunchesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

enum class CompanyTab(val title: String) {
    INFO("О компании"),
    HISTORY("История")
}

val CompanyViewModelFactory = object : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: KClass<T>, extras: CreationExtras): T {
        @Suppress("UNCHECKED_CAST")
        return CompanyViewModel(LaunchesRepository) as T
    }
}

data class CompanyState(
    val isLoading: Boolean = true,
    val currentTab: CompanyTab = CompanyTab.INFO,
    val companyInfo: CompanyInfo? = null,
    val history: List<HistoryEvent> = emptyList(),
    val error: String? = null
)

class CompanyViewModel(
    private val repository: LaunchesRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CompanyState())
    val state: StateFlow<CompanyState> = _state.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            try {
                val info = repository.getCompanyInfo()
                val history = repository.getHistory()

                _state.update { it.copy(
                    companyInfo = info,
                    history = history,
                    isLoading = false
                )}
            } catch (e: Exception) {
                e.printStackTrace()
                _state.update { it.copy(
                    isLoading = false,
                    error = "Ошибка загрузки данных"
                )}
            }
        }
    }

    fun onTabSelected(tab: CompanyTab) {
        _state.update { it.copy(currentTab = tab) }
    }
}