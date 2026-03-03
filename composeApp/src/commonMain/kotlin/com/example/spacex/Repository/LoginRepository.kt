package com.example.spacex.Repository

import com.example.spacex.Database

class LoginRepository(
    private val database: Database
) {
    fun login(username: String, password: String): Result<Unit> {
        val savedUser = database.getUser()

        return if (savedUser != null && savedUser.username == username && savedUser.password == password) {
            Result.success(Unit)
        } else {
            Result.failure(Exception("Неверный логин или пароль"))
        }
    }

    fun register(username: String, password: String) {
        database.saveUser(username, password)
    }
}