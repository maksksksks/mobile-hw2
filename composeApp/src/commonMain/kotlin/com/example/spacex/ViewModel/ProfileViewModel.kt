package com.example.spacex.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.spacex.Data.Launch
import com.example.spacex.Data.Rockets
import com.example.spacex.Repository.LaunchesRepository
import com.example.spacex.client
import com.example.spacex.database.UserEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

enum class AppTheme(val title: String) {
    LIGHT("Светлая"),
    DARK("Темная"),
    SYSTEM("Системная")
}

enum class ProfileTab(val title: String) {
    LAUNCHES("Запуски"),
    ROCKETS("Ракеты")
}

val ProfileViewModelFactory = object : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: KClass<T>, extras: CreationExtras): T {
        @Suppress("UNCHECKED_CAST")
        return ProfileViewModel(LaunchesRepository) as T
    }
}

data class ProfileState(
    val user: UserEntity? = null,
    val selectedTheme: AppTheme = AppTheme.SYSTEM,
    val currentTab: ProfileTab = ProfileTab.LAUNCHES,
    val favoriteLaunches: List<Launch> = emptyList(),
    val favoriteRockets: List<Rockets> = emptyList()
)

class ProfileViewModel(
    private val repository: LaunchesRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            // Слушаем запуски и фильтруем избранное
            repository.launchesFlow.collect { launches ->
                _state.update { it.copy(
                    favoriteLaunches = launches.filter { launch -> launch.is_favorite == true },
                    user = repository.database.getUser() // Обновляем юзера заодно
                ) }
            }
        }

        viewModelScope.launch {
            // Слушаем ракеты
            repository.rocketsFlow.collect { rockets ->
                _state.update { it.copy(
                    favoriteRockets = rockets.filter { rocket -> rocket.is_favorite == true }
                ) }
            }
        }
    }

    fun onThemeSelected(theme: AppTheme) {
        _state.update { it.copy(selectedTheme = theme) }
    }

    fun onTabSelected(tab: ProfileTab) {
        _state.update { it.copy(currentTab = tab) }
    }

    fun logout() {
        repository.database.clearUser()
    }

    fun onLaunchFavoriteClicked(flightNumber: Long) {
        viewModelScope.launch {
            val current = _state.value.favoriteLaunches.find { it.flight_number == flightNumber }
            val newStatus = !(current?.is_favorite ?: false)
            repository.database.setFavorite(flightNumber, newStatus)
        }
    }

    fun onRocketFavoriteClicked(id: Long) {
        viewModelScope.launch {
            val current = _state.value.favoriteRockets.find { it.id?.toLong() == id }
            val newStatus = !(current?.is_favorite ?: false)
            repository.database.setRocketFavorite(id.toLong(), newStatus)
        }
    }
}