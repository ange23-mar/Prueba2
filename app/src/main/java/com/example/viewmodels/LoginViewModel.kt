package com.example.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.repository.data.AppDatabase
import com.example.repository.data.UsuarioRepository
import com.example.repository.models.Usuario
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val loggedUser: Usuario? = null // su rol decide si se muestra Dashboard o interfaz de cliente
)

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = UsuarioRepository(AppDatabase.getDatabase(application))

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChange(value: String) = _uiState.update { it.copy(email = value, error = null) }
    fun onPasswordChange(value: String) = _uiState.update { it.copy(password = value, error = null) }

    fun login() {
        val state = _uiState.value
        if (state.email.isBlank() || state.password.isBlank()) {
            _uiState.update { it.copy(error = "Ingrese email y contraseña") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            delay(600) // pequeña pausa para mostrar el indicador de carga
            val usuario = withContext(Dispatchers.IO) {
                repository.login(state.email.trim().lowercase(), state.password)
            }
            _uiState.update {
                if (usuario != null) it.copy(isLoading = false, loggedUser = usuario, password = "")
                else it.copy(isLoading = false, error = "Credenciales inválidas")
            }
        }
    }

    fun logout() {
        _uiState.value = LoginUiState()
    }
}
