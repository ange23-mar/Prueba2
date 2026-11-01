package com.example.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.repository.data.AppDatabase
import com.example.repository.data.ConteoDao
import com.example.repository.models.Acceso
import com.example.repository.models.RangoEtario
import com.example.repository.models.RegistroFlujo
import com.example.repository.models.TipoFlujo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Lista y registro (crear / eliminar) de clasificaciones de flujo hechas por el personal. */
class FlujoViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ConteoDao(AppDatabase.getDatabase(application))

    val accesos: StateFlow<List<Acceso>> = repository.accesos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // READ: últimos 30 días, reactivo
    val flujos: StateFlow<List<RegistroFlujo>> =
        repository.flujos(Periodo.TREINTA_DIAS.desdeMillis())
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /**
     * CREATE. Valida y devuelve un mensaje de error, o null si todo está bien
     * (en ese caso el guardado ocurre en segundo plano).
     */
    fun guardar(accesoId: Int?, tipo: TipoFlujo, rango: RangoEtario, cantidadTexto: String): String? {
        if (accesoId == null) return "Seleccione un acceso"
        val cantidad = Validaciones.cantidadValida(cantidadTexto)
            ?: return "Ingrese una cantidad entera entre 1 y ${Validaciones.MAX_CANTIDAD}"

        viewModelScope.launch(Dispatchers.IO) {
            repository.guardarFlujo(accesoId, tipo, rango, cantidad)
        }
        return null
    }

    // DELETE
    fun eliminar(registro: RegistroFlujo) {
        viewModelScope.launch(Dispatchers.IO) { repository.eliminarFlujo(registro) }
    }
}
