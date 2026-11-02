package com.example.viewmodels

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.repository.data.AppDatabase
import com.example.repository.data.UsuarioRepository
import com.example.repository.models.RangoEtario
import com.example.repository.models.TipoFlujo
import com.example.repository.models.Usuario
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

/** Perfil del usuario con sesión iniciada: datos básicos, foto (CameraX) y tipo de cliente. */
@OptIn(ExperimentalCoroutinesApi::class)
class PerfilViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = UsuarioRepository(AppDatabase.getDatabase(application))

    private val usuarioId = MutableStateFlow<Int?>(null)

    // Se vuelve a consultar Room cada vez que cambia la sesión; Room avisa cuando cambia la fila
    val usuario: StateFlow<Usuario?> = usuarioId
        .flatMapLatest { id -> if (id == null) flowOf(null) else repository.observar(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje.asStateFlow()

    fun cerrarMensaje() { _mensaje.value = null }

    /** Lo llama AppNavigation al iniciar/cerrar sesión. */
    fun setUsuario(id: Int?) {
        usuarioId.value = id
    }

    /** Guarda la nueva foto y borra el archivo anterior para no acumular imágenes. */
    fun actualizarFoto(nuevaUri: String) {
        val id = usuarioId.value ?: return
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val anterior = repository.obtener(id)?.fotoPerfilUri
                repository.actualizarFoto(id, nuevaUri)
                borrarArchivoLocal(anterior)
            }
            _mensaje.value = "Foto de perfil actualizada"
        }
    }

    fun actualizarPerfilCliente(tipoCliente: TipoFlujo, rangoEtario: RangoEtario) {
        val id = usuarioId.value ?: return
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.obtener(id)?.let { actual ->
                    repository.actualizar(actual.copy(tipoCliente = tipoCliente, rangoEtario = rangoEtario))
                }
            }
            _mensaje.value = "Perfil actualizado"
        }
    }

    // Solo borra archivos dentro del almacenamiento interno privado de la app
    private fun borrarArchivoLocal(uri: String?) {
        val path = uri?.let { Uri.parse(it).path } ?: return
        val archivo = File(path)
        if (archivo.absolutePath.startsWith(getApplication<Application>().filesDir.absolutePath)) {
            archivo.delete()
        }
    }
}
