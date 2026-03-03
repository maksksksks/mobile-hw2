package com.example.spacex

import app.cash.sqldelight.db.SqlDriver
import com.example.spacex.Data.Launch
import com.example.spacex.Data.Links
import com.example.spacex.Data.Rockets
import com.example.spacex.database.AppDatabase
import com.example.spacex.database.UserEntity
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class Database(driver: SqlDriver) {
    private val database = AppDatabase(driver)
    private val queries = database.appDatabaseQueries

    fun getLaunches(): List<Launch> {
        return queries.selectAllLaunches().executeAsList().map { entity ->
            Launch(
                flight_number = entity.flight_number,
                mission_name = entity.mission_name,
                launch_date_utc = entity.launch_date_utc,
                links = entity.links_json?.let { Json.decodeFromString<Links>(it) },
                details = entity.details,
                launch_year = entity.launch_year,
                is_favorite = entity.is_favorite == 1L
            )
        }
    }

    fun setFavorite(flightNumber: Long, isFavorite: Boolean) {
        queries.updateFavorite(
            is_favorite = if (isFavorite) 1L else 0L,
            flight_number = flightNumber
        )
    }

    fun insertLaunches(launches: List<Launch>) {
        queries.transaction {
            launches.forEach { launch ->
                queries.insertLaunch(
                    flight_number = launch.flight_number,
                    mission_name = launch.mission_name,
                    launch_date_utc = launch.launch_date_utc,
                    links_json = launch.links?.let { Json.encodeToString(it) },
                    details = launch.details,
                    launch_year = launch.launch_year,
                    is_favorite = if (launch.is_favorite == true) 1L else 0L
                )
            }
        }
    }

    fun clearLaunches() {
        queries.deleteAllLaunches()
    }

    fun getUser(): UserEntity? {
        return queries.selectUser().executeAsOneOrNull()
    }

    fun saveUser(username: String, password: String) {
        queries.deleteUser()
        queries.insertUser(username, password)
    }

    fun clearUser() {
        queries.deleteUser()
    }

    fun getRockets(): List<Rockets> {
        return queries.selectAllRockets().executeAsList().map { entity ->
            Rockets(
                id = entity.id.toInt().toLong(),
                rocket_name = entity.rocket_name,
                active = entity.active == 1L,
                cost_per_launch = entity.cost_per_launch,
                success_rate_pct = entity.success_rate_pct?.toInt(),
                first_flight = entity.first_flight,
                flickr_images = entity.flickr_images_json?.let {
                    Json.decodeFromString<List<String>>(it)
                },
                is_favorite = entity.is_favorite == 1L
            )
        }
    }

    fun insertRockets(rockets: List<Rockets>) {
        queries.transaction {
            rockets.forEach { rocket ->
                queries.insertRocket(
                    id = rocket.id?.toLong(),
                    rocket_name = rocket.rocket_name,
                    active = if (rocket.active == true) 1L else 0L,
                    cost_per_launch = rocket.cost_per_launch,
                    success_rate_pct = rocket.success_rate_pct?.toLong(),
                    first_flight = rocket.first_flight,
                    flickr_images_json = rocket.flickr_images?.let {
                        Json.encodeToString(it)
                    },
                    is_favorite = if (rocket.is_favorite == true) 1L else 0L
                )
            }
        }
    }

    fun clearRockets() {
        queries.deleteAllRockets()
    }

    fun setRocketFavorite(id: Long, isFavorite: Boolean) {
        queries.updateRocketFavorite(
            is_favorite = if (isFavorite) 1L else 0L,
            id = id.toLong()
        )
    }

    fun getFavoriteLaunches(): List<Launch> {
        return queries.selectFavoriteLaunches().executeAsList().map { entity ->
            Launch(
                flight_number = entity.flight_number,
                mission_name = entity.mission_name,
                launch_date_utc = entity.launch_date_utc,
                links = entity.links_json?.let { Json.decodeFromString<Links>(it) },
                details = entity.details,
                launch_year = entity.launch_year,
                is_favorite = entity.is_favorite == 1L
            )
        }
    }

    fun getFavoriteRockets(): List<Rockets> {
        return queries.selectFavoriteRockets().executeAsList().map { entity ->
            Rockets(
                id = entity.id.toInt().toLong(),
                rocket_name = entity.rocket_name,
                active = entity.active == 1L,
                cost_per_launch = entity.cost_per_launch,
                success_rate_pct = entity.success_rate_pct?.toInt(),
                first_flight = entity.first_flight,
                flickr_images = entity.flickr_images_json?.let {
                    Json.decodeFromString<List<String>>(it)
                },
                is_favorite = entity.is_favorite == 1L
            )
        }
    }
}