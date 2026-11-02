package com.example.viewmodels

import android.app.Application
import android.database.sqlite.SQLiteConstraintException
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.repository.data.AppDatabase
import com.example.repository.data.UsuarioRepository
import com.example.repository.models.RangoEtario
import com.example.repository.models.RolUsuario
import com.example.repository.models.TipoFlujo
import com.example.repository.models.Usuario
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** Registro (CREATE) de nuevas cuentas de CLIENTE desde la pantalla de login. */
class UsuarioViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = UsuarioRepository(AppDatabase.getDatabase(application))

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    private val _guardando = MutableStateFlow(false)
    val guardando: StateFlow<Boolean> = _guardando.asStateFlow()

    fun clearMessage() { _message.value = null }

    /** onSuccess recibe el email registrado (se usa para precargar el login). */
    fun registrarCliente(
        nombre: String,
        email: String,
        password: String,
        confirmacion: String,
        tipoCliente: TipoFlujo,
        rangoEtario: RangoEtario,
        onSuccess: (String) -> Unit
    ) {
        val emailLimpio = email.trim().lowercase()
        val error = when {
            !Validaciones.esSoloLetras(nombre) ->
                "El nombre solo puede contener letras y un espacio entre palabras"
            !Validaciones.emailValido(emailLimpio) -> "Ingrese un email válido"
            !Validaciones.passwordValida(password) ->
                "La contraseña debe tener al menos ${Validaciones.MIN_PASSWORD} caracteres"
            password != confirmacion -> "Las contraseñas no coinciden"
            else -> null
        }
        if (error != null) {
            _message.value = error
            return
        }

        viewModelScope.launch {
            _guardando.value = true
            try {
                withContext(Dispatchers.IO) {
                    repository.crear(
                        Usuario(
                            nombre = nombre.trim(),
                            email = emailLimpio,
                            password = password,
                            rol = RolUsuario.CLIENTE, // desde la app solo se crean clientes
                            tipoCliente = tipoCliente,
                            rangoEtario = rangoEtario
                        )
                    )
                }
                _message.value = null
                onSuccess(emailLimpio)
            } catch (e: SQLiteConstraintException) {
                _message.value = "Ya existe una cuenta con el email $emailLimpio"
            } finally {
                _guardando.value = false
            }
        }
    }
}
