package com.example.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.repository.data.AppDatabase
import com.example.repository.data.ConteoRepository
import com.example.repository.models.Acceso
import com.example.repository.models.MovimientoCliente
import com.example.repository.models.TipoFlujo
import com.example.repository.models.TipoMovimiento
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class Aviso(val texto: String, val esError: Boolean)

data class MovimientoUiState(
    val accesos: List<Acceso> = emptyList(),
    val historial: List<MovimientoCliente> = emptyList(),
    val procesando: Boolean = false,
    val aviso: Aviso? = null
) {
    /** El historial viene ordenado del más reciente al más antiguo. */
    val ultimo: MovimientoCliente? get() = historial.firstOrNull()

    /** El cliente está dentro si su último movimiento fue un INGRESO. */
    val dentro: Boolean get() = ultimo?.tipoMovimiento == TipoMovimiento.INGRESO
}

/** Registro de Ingreso / Salida del cliente con sesión iniciada. */
@OptIn(ExperimentalCoroutinesApi::class)
class MovimientoViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ConteoRepository(AppDatabase.getDatabase(application))

    private val usuarioId = MutableStateFlow<Int?>(null)
    private val procesando = MutableStateFlow(false)
    private val aviso = MutableStateFlow<Aviso?>(null)

    private val historial = usuarioId.flatMapLatest { id ->
        if (id == null) flowOf(emptyList()) else repository.historialCliente(id)
    }

    val uiState: StateFlow<MovimientoUiState> =
        combine(repository.accesos(), historial, procesando, aviso) { accesos, hist, p, a ->
            MovimientoUiState(accesos = accesos, historial = hist, procesando = p, aviso = a)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MovimientoUiState())

    /** Lo llama AppNavigation al iniciar/cerrar sesión. */
    fun setUsuario(id: Int?) {
        usuarioId.value = id
        aviso.value = null
    }

    fun registrar(tipo: TipoMovimiento, accesoId: Int?, motivo: TipoFlujo?) {
        val id = usuarioId.value ?: run { aviso.value = Aviso("Sesión no válida", true); return }
        if (accesoId == null) { aviso.value = Aviso("Seleccione el acceso", true); return }
        if (motivo == null) { aviso.value = Aviso("Seleccione el motivo: Solo transita o Compra", true); return }

        viewModelScope.launch {
            procesando.value = true
            val error = withContext(Dispatchers.IO) {
                repository.registrarMovimiento(id, accesoId, tipo, motivo)
            }
            aviso.value = if (error != null) Aviso(error, true)
            else Aviso("${tipo.etiqueta} registrado · ${motivo.etiqueta}", false)
            procesando.value = false
        }
    }

    fun cerrarAviso() { aviso.value = null }
}
