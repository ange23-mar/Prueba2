package com.example.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.repository.data.AppDatabase
import com.example.repository.data.ConteoRepository
import com.example.repository.models.Acceso
import com.example.repository.models.RangoEtario
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

data class FilaSegmento(val rango: RangoEtario, val total: Int, val porcentaje: Float)

data class SegmentacionUiState(
    val periodo: Periodo = Periodo.TREINTA_DIAS,
    val accesoId: Int = 0, // 0 = todos los accesos
    val accesos: List<Acceso> = emptyList(),
    val totalTransito: Int = 0,
    val filas: List<FilaSegmento> = emptyList()
) {
    /** Rango etario con más personas en tránsito (null si no hay datos). */
    val predominante: FilaSegmento? get() = if (totalTransito == 0) null else filas.maxByOrNull { it.total }
}

/** Perfil (rango etario) de las personas que solo transitan por la tienda. */
@OptIn(ExperimentalCoroutinesApi::class)
class SegmentacionViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ConteoRepository(AppDatabase.getDatabase(application))

    private val periodo = MutableStateFlow(Periodo.TREINTA_DIAS)
    private val accesoId = MutableStateFlow(0)

    // Se vuelve a consultar Room cuando cambia el periodo o el acceso seleccionado
    private val datos = combine(periodo, accesoId) { p, a -> p to a }
        .flatMapLatest { (p, a) -> repository.segmentacion(a, p.desdeMillis()) }

    val uiState: StateFlow<SegmentacionUiState> =
        combine(periodo, accesoId, repository.accesos(), datos) { p, a, accesos, filasBd ->
            val total = filasBd.sumOf { it.total }
            // Se listan TODOS los rangos (aunque tengan 0) para que la pantalla sea estable
            val filas = RangoEtario.entries.map { rango ->
                val t = filasBd.firstOrNull { it.rangoEtario == rango }?.total ?: 0
                FilaSegmento(rango, t, if (total == 0) 0f else t.toFloat() / total)
            }
            SegmentacionUiState(p, a, accesos, total, filas)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SegmentacionUiState())

    fun cambiarPeriodo(nuevo: Periodo) {
        periodo.value = nuevo
    }

    fun seleccionarAcceso(id: Int) {
        accesoId.value = id
    }
}
