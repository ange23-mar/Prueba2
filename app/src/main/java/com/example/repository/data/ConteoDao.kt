package com.example.repository.data

import androidx.room.withTransaction
import com.example.repository.models.Acceso
import com.example.repository.models.RangoEtario
import com.example.repository.models.RegistroFlujo
import com.example.repository.models.TipoFlujo
import com.example.repository.models.TotalAcceso
import com.example.repository.models.TotalAccesoTipo
import com.example.repository.models.TotalRango
import kotlinx.coroutines.flow.Flow
import kotlin.text.insert

/**
 * Única fuente de datos para los ViewModels (patrón Repository).
 *  - Lectura: siempre desde Room (funciona sin internet; la UI se actualiza con Flow).
 *  - Retrofit: solo se usa en sincronizar() para enviar/recibir datos del backend.
 */
class ConteoDao(private val db: AppDatabase){

    private val accesoDao = db.accesoDao()
    private val conteoDao = db.registroConteoDao()
    private val flujoDao = db.registroFlujoDao()

    // ---------- Lecturas (Room, reactivas con Flow) ----------
    fun accesos(): Flow<List<Acceso>> = accesoDao.getAll()
    fun totalesConteo(desde: Long): Flow<List<TotalAcceso>> = conteoDao.totalesPorAcceso(desde)
    fun totalesFlujo(desde: Long): Flow<List<TotalAccesoTipo>> = flujoDao.totalesPorAccesoYTipo(desde)
    fun segmentacion(accesoId: Int, desde: Long): Flow<List<TotalRango>> =
        flujoDao.segmentacionTransito(accesoId, desde)
    fun flujos(desde: Long): Flow<List<RegistroFlujo>> = flujoDao.getDesde(desde)

    // ---------- Escrituras ----------
    suspend fun guardarFlujo(accesoId: Int, tipo: TipoFlujo, rango: RangoEtario, cantidad: Int) {
        flujoDao.insert(
            RegistroFlujo(
                accesoId = accesoId,
                fechaHora = System.currentTimeMillis(),
                tipoFlujo = tipo,
                rangoEtario = rango,
                cantidad = cantidad
            )
        )
    }

    suspend fun eliminarFlujo(registro: RegistroFlujo) = flujoDao.delete(registro)

    /** Reemplaza todo lo local por datos ficticios de los últimos 30 días. */
    suspend fun cargarInitializer() {
        val (conteos, flujos) = Initializer.generar()
        // withTransaction: o se guarda todo o no se guarda nada
        db.withTransaction {
            conteoDao.deleteAll()
            flujoDao.deleteAll()
            conteoDao.insertAll(conteos)
            flujoDao.insertAll(flujos)
        }
    }

    /** Primera vez que se abre la app: si no hay datos, se generan automáticamente. */
    suspend fun cargarSiEstaVacio() {
        if (conteoDao.contar() == 0) cargarInitializer()
    }
}
