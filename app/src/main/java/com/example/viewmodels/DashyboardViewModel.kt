
package com.example.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.repository.data.AppDatabase
import com.example.repository.data.ConteoDao
import com.example.repository.models.Acceso
import com.example.repository.models.TipoFlujo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Resumen de un acceso: cuántos transitan, cuántos compran y cuántos aún no están clasificados. */
data class ResumenAcceso(
    val acceso: Acceso,
    val transito: Int,
    val compra: Int,
    val sinClasificar: Int
) {
    val total: Int get() = transito + compra + sinClasificar
    val porcentajeTransito: Float get() = if (total == 0) 0f else transito.toFloat() / total
}

data class DashboardUiState(
    val periodo: Periodo = Periodo.TREINTA_DIAS,
    val resumen: List<ResumenAcceso> = emptyList(),
    val ocupado: Boolean = false,
    val mensaje: String? = null
)

@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ConteoDao(AppDatabase.getDatabase(application))

    // Estados internos modificables; la vista solo ve el uiState de solo lectura
    private val periodo = MutableStateFlow(Periodo.TREINTA_DIAS)
    private val ocupado = MutableStateFlow(false)
    private val mensaje = MutableStateFlow<String?>(null)

    // Cada vez que cambia el periodo, flatMapLatest cancela la consulta anterior y
    // abre una nueva. combine() junta las 3 consultas de Room en una lista de resúmenes.
    private val resumen: Flow<List<ResumenAcceso>> = periodo.flatMapLatest { p ->
        val desde = p.desdeMillis()
        combine(
            repository.accesos(),
            repository.totalesConteo(desde),
            repository.totalesFlujo(desde)
        ) { accesos, conteos, flujos ->
            accesos.map { acceso ->
                val conteo = conteos.firstOrNull { it.accesoId == acceso.id }?.total ?: 0
                val transito = flujos.firstOrNull {
                    it.accesoId == acceso.id && it.tipoFlujo == TipoFlujo.TRANSITO
                }?.total ?: 0
                val compra = flujos.firstOrNull {
                    it.accesoId == acceso.id && it.tipoFlujo == TipoFlujo.COMPRA
                }?.total ?: 0
                ResumenAcceso(
                    acceso = acceso,
                    transito = transito,
                    compra = compra,
                    // Lo que el sistema de conteo registró pero nadie ha clasificado todavía
                    sinClasificar = (conteo - transito - compra).coerceAtLeast(0)
                )
            }
        }
    }

    val uiState: StateFlow<DashboardUiState> =
        combine(periodo, resumen, ocupado, mensaje) { p, r, o, m ->
            DashboardUiState(periodo = p, resumen = r, ocupado = o, mensaje = m)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardUiState())

    // Al abrir la app por primera vez se generan los datos ficticios automáticamente
    init {
        viewModelScope.launch(Dispatchers.IO) { repository.cargarSiEstaVacio() }
    }

    fun cambiarPeriodo(nuevo: Periodo) {
        periodo.value = nuevo
    }

    /** Regenera los datos ficticios (30 días) y reemplaza los actuales. */
    fun cargarDatosEjemplo() {
        viewModelScope.launch(Dispatchers.IO) {
            ocupado.value = true
            repository.cargarInitializer()
            mensaje.value = "Datos de ejemplo regenerados (30 días)"
            ocupado.value = false
        }
    }

    fun cerrarMensaje() {
        mensaje.value = null
    }
}
