package com.example.spacex.Repository

import com.example.spacex.Data.CompanyInfo
import com.example.spacex.Data.HistoryEvent
import com.example.spacex.Data.Launch
import com.example.spacex.Data.Rockets
import com.example.spacex.Database
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


object LaunchesRepository {
    private val client: HttpClient = com.example.spacex.client
    val database: Database = com.example.spacex.ViewModel.appDatabase

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
        val cached = database.getLaunches()
        if (cached.isNotEmpty()) {
            _launchesFlow.value = cached
        }

        try {
            val remote: List<Launch> = client.get("https://api.spacexdata.com/v3/launches").body()
            database.clearLaunches()
            database.insertLaunches(remote)
            _launchesFlow.value = remote
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private suspend fun loadRockets() {
        val cached = database.getRockets()
        if (cached.isNotEmpty()) {
            _rocketsFlow.value = cached
        }

        try {
            val remote = client.get("https://api.spacexdata.com/v3/rockets").body<List<Rockets>>()
            database.clearRockets()
            database.insertRockets(remote)
            _rocketsFlow.value = remote
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    suspend fun toggleFavorite(flightNumber: Long, isFavorite: Boolean) {
        database.setFavorite(flightNumber, isFavorite)
        val currentList = _launchesFlow.value.toMutableList()
        val index = currentList.indexOfFirst { it.flight_number == flightNumber }
        if (index != -1) {
            currentList[index] = currentList[index].copy(is_favorite = isFavorite)
            _launchesFlow.value = currentList
        }
    }

    suspend fun toggleRocketFavorite(rocketId: Long, isFavorite: Boolean) {
        database.setRocketFavorite(rocketId, isFavorite)
        val currentList = _rocketsFlow.value.toMutableList()
        val index = currentList.indexOfFirst { it.id?.toLong() == rocketId }
        if (index != -1) {
            currentList[index] = currentList[index].copy(is_favorite = isFavorite)
            _rocketsFlow.value = currentList
        }
    }

    suspend fun getCompanyInfo() = client.get("https://api.spacexdata.com/v3/info").body<CompanyInfo>()
    suspend fun getHistory() = client.get("https://api.spacexdata.com/v3/history").body<List<HistoryEvent>>()
}