package com.example.spacex.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.spacex.Database
import com.example.spacex.Repository.LoginRepository
import com.example.spacex.driverFactory
import kotlin.reflect.KClass

val appDatabase by lazy { Database(driverFactory) }

val LoginViewModelFactory = object : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: KClass<T>, extras: CreationExtras): T {
        val repo = LoginRepository(appDatabase)
        @Suppress("UNCHECKED_CAST")
        return LoginViewModel(repo) as T
    }
}

data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val isLoginButtonActive: Boolean = false,
    val error: String? = null
)

sealed class LoginUiEvent {
    object LoginSuccessEvent : LoginUiEvent()
}

class LoginViewModel(
    private val loginRepository: LoginRepository
) : ViewModel() {

    private val _state = MutableStateFlow(LoginUiState())
    val state: StateFlow<LoginUiState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<LoginUiEvent>()
    val events: SharedFlow<LoginUiEvent> = _events.asSharedFlow()

    fun onUsernameChanged(username: String) {
        _state.update { it.copy(username = username, error = null) }
        validateInput()
    }

    fun onPasswordChanged(password: String) {
        _state.update { it.copy(password = password, error = null) }
        validateInput()
    }

    private fun validateInput() {
        val currentState = _state.value
        val isActive = currentState.username.isNotBlank() && currentState.password.isNotBlank()
        _state.update { it.copy(isLoginButtonActive = isActive) }
    }

    fun onLoginClick() {
        val currentState = _state.value
        val result = loginRepository.login(currentState.username, currentState.password)

        viewModelScope.launch {
            if (result.isSuccess) {
                _events.emit(LoginUiEvent.LoginSuccessEvent)
            } else {
                _state.update { it.copy(error = result.exceptionOrNull()?.message ?: "Ошибка") }
            }
        }
    }

    fun onRegisterClick() {
        val currentState = _state.value
        val result = loginRepository.register(currentState.username, currentState.password)
    }
}