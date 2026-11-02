package com.example.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.repository.data.AppDatabase
import com.example.repository.data.ConteoRepository
import com.example.repository.models.Acceso
import com.example.repository.models.TipoFlujo
import com.example.repository.models.TipoMovimiento
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

/** Lo que aportaron los clientes desde la app en el periodo (ya incluido en ResumenAcceso). */
data class ResumenClientes(
    val ingresosTransito: Int = 0,
    val ingresosCompra: Int = 0,
    val salidas: Int = 0,
    val dentroAhora: Int = 0
) {
    val ingresos: Int get() = ingresosTransito + ingresosCompra
}

data class DashboardUiState(
    val periodo: Periodo = Periodo.TREINTA_DIAS,
    val resumen: List<ResumenAcceso> = emptyList(),
    val clientes: ResumenClientes = ResumenClientes(),
    val ocupado: Boolean = false,
    val mensaje: String? = null
)

@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ConteoRepository(AppDatabase.getDatabase(application))

    // Estados internos modificables; la vista solo ve el uiState de solo lectura
    private val periodo = MutableStateFlow(Periodo.TREINTA_DIAS)
    private val ocupado = MutableStateFlow(false)
    private val mensaje = MutableStateFlow<String?>(null)

    // Cada vez que cambia el periodo, flatMapLatest cancela la consulta anterior y
    // abre una nueva. combine() junta las 3 consultas de Room en una lista de resúmenes.
    // Los ingresos que marcan los clientes ya están en registro_conteo / registro_flujo,
    // por lo que se reflejan aquí automáticamente.
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

    // Detalle de los movimientos de clientes (ingresos por motivo, salidas, presentes ahora)
    private val clientes: Flow<ResumenClientes> = periodo.flatMapLatest { p ->
        combine(
            repository.totalesMovimientos(p.desdeMillis()),
            repository.clientesDentro()
        ) { totales, dentro ->
            fun suma(tipo: TipoMovimiento, motivo: TipoFlujo? = null) = totales
                .filter { it.tipoMovimiento == tipo && (motivo == null || it.motivo == motivo) }
                .sumOf { it.total }
            ResumenClientes(
                ingresosTransito = suma(TipoMovimiento.INGRESO, TipoFlujo.TRANSITO),
                ingresosCompra = suma(TipoMovimiento.INGRESO, TipoFlujo.COMPRA),
                salidas = suma(TipoMovimiento.SALIDA),
                dentroAhora = dentro
            )
        }
    }

    val uiState: StateFlow<DashboardUiState> =
        combine(periodo, resumen, clientes, ocupado, mensaje) { p, r, c, o, m ->
            DashboardUiState(periodo = p, resumen = r, clientes = c, ocupado = o, mensaje = m)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardUiState())

    // Al abrir el Dashboard por primera vez se generan los datos ficticios automáticamente
    init {
        viewModelScope.launch(Dispatchers.IO) { repository.cargarSiEstaVacio() }
    }

    fun cambiarPeriodo(nuevo: Periodo) {
        periodo.value = nuevo
    }

    /** Regenera los datos ficticios (30 días). Los registros de clientes se conservan. */
    fun cargarDatosEjemplo() {
        viewModelScope.launch(Dispatchers.IO) {
            ocupado.value = true
            repository.cargarInitializer()
            mensaje.value = "Datos de ejemplo regenerados (30 días). Se conservaron los registros de clientes."
            ocupado.value = false
        }
    }

    fun cerrarMensaje() {
        mensaje.value = null
    }
}
