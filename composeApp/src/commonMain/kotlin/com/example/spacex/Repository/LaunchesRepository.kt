package com.example.spacex.Repository

import com.example.spacex.Data.CompanyInfo
import com.example.spacex.Data.HistoryEvent
import com.example.spacex.Data.Launch
import com.example.spacex.Data.Rockets
import com.example.spacex.Database
import com.example.spacex.ViewModel.appDatabase
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// Инициализация базы данных через ваш класс Database
private val db: Database = appDatabase

object LaunchesRepository {
    private val client: HttpClient = com.example.spacex.client

    val database: Database = appDatabase

    private val _launchesFlow = MutableStateFlow<List<Launch>>(emptyList())
    val launchesFlow: StateFlow<List<Launch>> = _launchesFlow.asStateFlow()

    private val _rocketsFlow = MutableStateFlow<List<Rockets>>(emptyList())
    val rocketsFlow: StateFlow<List<Rockets>> = _rocketsFlow.asStateFlow()

    private var isLoaded = false

    suspend fun init() {
        if (isLoaded) return
        isLoaded = true
        loadLaunches()
        loadRockets()
    }

    private suspend fun loadLaunches() {
        // 1. Сначала отдаем кэш (если есть), чтобы пользователь что-то увидел
        val cached = db.getLaunches()
        if (cached.isNotEmpty()) {
            _launchesFlow.value = cached
        }

        Napier.i("Getting list of launches")
        // 2. Сетевой запрос (ошибка пробросится выше, если нет интернета)
        val remote: List<Launch> = client.get("https://api.spacexdata.com/v3/launches").body()

        // 3. Мерж с избранным
        val favoriteIds = db.getFavoriteFlightNumbers()
        val mergedLaunches = remote.map { launch ->
            if (launch.flight_number in favoriteIds) launch.copy(is_favorite = true) else launch
        }

        // 4. Сохранение в БД
        db.clearLaunches()
        db.insertLaunches(mergedLaunches)

        // 5. Обновление Flow
        _launchesFlow.value = mergedLaunches
    }

    private suspend fun loadRockets() {
        val cached = db.getRockets()
        if (cached.isNotEmpty()) {
            _rocketsFlow.value = cached
        }

        Napier.i("Getting list of rockets")
        val remote = client.get("https://api.spacexdata.com/v3/rockets").body<List<Rockets>>()

        val favoriteIds = db.getFavoriteRocketIds()
        val mergedRockets = remote.map { rocket ->
            if (rocket.id in favoriteIds) rocket.copy(is_favorite = true) else rocket
        }

        db.clearRockets()
        db.insertRockets(mergedRockets)
        _rocketsFlow.value = mergedRockets
    }

    suspend fun toggleFavorite(flightNumber: Long, isFavorite: Boolean) {
        db.setFavorite(flightNumber, isFavorite)
        val currentList = _launchesFlow.value.toMutableList()
        val index = currentList.indexOfFirst { it.flight_number == flightNumber }
        if (index != -1) {
            currentList[index] = currentList[index].copy(is_favorite = isFavorite)
            _launchesFlow.value = currentList
        }
    }

    suspend fun toggleRocketFavorite(rocketId: Long, isFavorite: Boolean) {
        db.setRocketFavorite(rocketId, isFavorite)

        val currentList = _rocketsFlow.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == rocketId }
        if (index != -1) {
            currentList[index] = currentList[index].copy(is_favorite = isFavorite)
            _rocketsFlow.value = currentList
        }
    }

    suspend fun getCompanyInfo(): CompanyInfo {
        Napier.i("Getting info about company")
        return client.get("https://api.spacexdata.com/v3/info").body<CompanyInfo>()
    }

    suspend fun getHistory(): List<HistoryEvent> {
        Napier.i("Getting history of company")
        return client.get("https://api.spacexdata.com/v3/history").body<List<HistoryEvent>>()
    }
}