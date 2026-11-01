package com.example.repository.data

import androidx.room.withTransaction
import com.example.repository.models.Acceso
import com.example.repository.models.RegistroFlujo
import com.example.repository.models.RangoEtario
import com.example.repository.models.TipoFlujo
import com.example.repository.models.TotalAcceso
import com.example.repository.models.TotalAccesoTipo
import com.example.repository.models.TotalRango
import com.example.repository.remote.ConteoApiService
import com.example.repository.remote.RetrofitClient
import com.example.repository.remote.toDto
import com.example.repository.remote.toEntity
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlin.text.insert

/**
 * Única fuente de datos para los ViewModels (patrón Repository).
 *  - Lectura: siempre desde Room (funciona sin internet; la UI se actualiza con Flow).
 *  - Retrofit: solo se usa en sincronizar() para enviar/recibir datos del backend.
 */
class ConteoRepository(
    private val db: AppDatabase,
    private val api: ConteoApiService = RetrofitClient.api
) {
    private val accesoDao = db.accesoDao()
    private val conteoDao = db.registroConteoDao()
    private val flujoDao = db.registroFlujoDao()

    // ---------- Lecturas (Room) ----------
    fun accesos(): Flow<List<Acceso>> = accesoDao.getAll()
    fun totalesConteo(desde: Long): Flow<List<TotalAcceso>> = conteoDao.totalesPorAcceso(desde)
    fun totalesFlujo(desde: Long): Flow<List<TotalAccesoTipo>> = flujoDao.totalesPorAccesoYTipo(desde)
    fun segmentacion(accesoId: Int, desde: Long): Flow<List<TotalRango>> =
        flujoDao.segmentacionTransito(accesoId, desde)
    fun flujos(desde: Long): Flow<List<RegistroFlujo>> = flujoDao.getDesde(desde)

    // ---------- Escrituras (Room) ----------
    suspend fun guardarFlujo(accesoId: Int, tipo: TipoFlujo, rango: RangoEtario, cantidad: Int) {
        flujoDao.insert(
            RegistroFlujo(
                accesoId = accesoId,
                fechaHora = System.currentTimeMillis(),
                tipoFlujo = tipo,
                rangoEtario = rango,
                cantidad = cantidad,
                sincronizado = false // queda pendiente de enviar al backend
            )
        )
    }

    suspend fun eliminarFlujo(registro: RegistroFlujo) = flujoDao.delete(registro)

    /** Reemplaza todo lo local por datos ficticios de los últimos 30 días. */
    suspend fun cargarDatosSinteticos() {
        val (conteos, flujos) = DatosSinteticos.generar()
        // withTransaction: o se guarda todo o no se guarda nada
        db.withTransaction {
            conteoDao.deleteAll()
            flujoDao.deleteAll()
            conteoDao.insertAll(conteos)
            flujoDao.insertAll(flujos)
        }
    }

    // ---------- Sincronización (Retrofit) ----------
    /**
     * 1) Envía al backend los registros hechos en el teléfono (sincronizado = false).
     * 2) Descarga conteos y flujos del backend y reemplaza la copia local.
     * Devuelve un mensaje de resumen o el error (sin internet, servidor apagado, etc.).
     */
    suspend fun sincronizar(): Result<String> = try {
        val pendientes = flujoDao.pendientes()
        pendientes.forEach { pendiente ->
            api.crearFlujo(pendiente.toDto())
            flujoDao.marcarSincronizado(pendiente.id)
        }

        val conteos = api.obtenerConteos().filter { it.accesoId in 1..4 }
        val flujos = api.obtenerFlujos().filter { it.accesoId in 1..4 }.mapNotNull { it.toEntity() }

        db.withTransaction {
            conteoDao.deleteAll()
            conteoDao.insertAll(conteos.map { it.toEntity() })
            flujoDao.borrarSincronizados() // los pendientes (si fallara el envío) se conservan
            flujoDao.insertAll(flujos)
        }
        Result.success(
            "Sincronizado: ${pendientes.size} enviados, ${conteos.size} conteos y ${flujos.size} flujos recibidos"
        )
    } catch (e: CancellationException) {
        throw e // no tragarse la cancelación de la corrutina
    } catch (e: Exception) {
        Result.failure(e)
    }
}
