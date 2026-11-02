package com.example.repository.data

import androidx.room.withTransaction
import com.example.repository.models.Acceso
import com.example.repository.models.MovimientoCliente
import com.example.repository.models.RangoEtario
import com.example.repository.models.RegistroConteo
import com.example.repository.models.RegistroFlujo
import com.example.repository.models.RolUsuario
import com.example.repository.models.TipoFlujo
import com.example.repository.models.TipoMovimiento
import com.example.repository.models.TotalAcceso
import com.example.repository.models.TotalAccesoTipo
import com.example.repository.models.TotalMovimiento
import com.example.repository.models.TotalRango
import kotlinx.coroutines.flow.Flow

/**
 * Única fuente de datos de conteo/flujo para los ViewModels (patrón Repository).
 * Todo es LOCAL: Room/SQLite + datos ficticios (Initializer) + registros de clientes.
 * La UI se actualiza sola porque las lecturas devuelven Flow.
 */
class ConteoRepository(private val db: AppDatabase) {

    private val accesoDao = db.accesoDao()
    private val conteoDao = db.registroConteoDao()
    private val flujoDao = db.registroFlujoDao()
    private val usuarioDao = db.usuarioDao()
    private val movimientoDao = db.movimientoClienteDao()

    // ---------- Lecturas (Room, reactivas con Flow) ----------
    fun accesos(): Flow<List<Acceso>> = accesoDao.getAll()
    fun totalesConteo(desde: Long): Flow<List<TotalAcceso>> = conteoDao.totalesPorAcceso(desde)
    fun totalesFlujo(desde: Long): Flow<List<TotalAccesoTipo>> = flujoDao.totalesPorAccesoYTipo(desde)
    fun segmentacion(accesoId: Int, desde: Long): Flow<List<TotalRango>> =
        flujoDao.segmentacionTransito(accesoId, desde)
    fun flujos(desde: Long): Flow<List<RegistroFlujo>> = flujoDao.getDesde(desde)

    // Movimientos de clientes
    fun historialCliente(usuarioId: Int): Flow<List<MovimientoCliente>> = movimientoDao.historial(usuarioId)
    fun totalesMovimientos(desde: Long): Flow<List<TotalMovimiento>> = movimientoDao.totalesDesde(desde)
    fun clientesDentro(): Flow<Int> = movimientoDao.clientesDentro()

    // ---------- Escrituras ----------
    suspend fun guardarFlujo(accesoId: Int, tipo: TipoFlujo, rango: RangoEtario, cantidad: Int, fotoUri: String?) {
        flujoDao.insert(
            RegistroFlujo(
                accesoId = accesoId,
                fechaHora = System.currentTimeMillis(),
                tipoFlujo = tipo,
                rangoEtario = rango,
                cantidad = cantidad,
                fotoUri = fotoUri
            )
        )
    }

    suspend fun eliminarFlujo(registro: RegistroFlujo) = flujoDao.delete(registro)

    /**
     * Registra un INGRESO o SALIDA de un cliente. Devuelve un mensaje de error o null si todo salió bien.
     *
     * Integración con el Dashboard (todo en una sola transacción):
     *  - Siempre se guarda el movimiento en movimiento_cliente (bitácora del cliente).
     *  - En un INGRESO además se suma 1 a registro_conteo (equivale a lo que contaría el sensor)
     *    y 1 a registro_flujo con el motivo y el rango etario del cliente (ya clasificado).
     *    Así el Dashboard (total, tránsito/compra) y la Segmentación cambian solos vía Flow.
     *  - La SALIDA no suma ingresos: solo cierra la visita.
     */
    suspend fun registrarMovimiento(
        usuarioId: Int,
        accesoId: Int,
        tipo: TipoMovimiento,
        motivo: TipoFlujo
    ): String? = db.withTransaction {
        val usuario = usuarioDao.getById(usuarioId)
            ?: return@withTransaction "Usuario no encontrado"
        if (usuario.rol != RolUsuario.CLIENTE) {
            return@withTransaction "Solo los clientes registran ingresos y salidas"
        }

        val dentro = movimientoDao.ultimo(usuarioId)?.tipoMovimiento == TipoMovimiento.INGRESO
        if (tipo == TipoMovimiento.INGRESO && dentro) {
            return@withTransaction "Ya tienes un ingreso abierto. Marca tu salida primero."
        }
        if (tipo == TipoMovimiento.SALIDA && !dentro) {
            return@withTransaction "No tienes un ingreso registrado. Marca tu ingreso primero."
        }

        val ahora = System.currentTimeMillis()
        movimientoDao.insert(
            MovimientoCliente(
                usuarioId = usuarioId,
                accesoId = accesoId,
                tipoMovimiento = tipo,
                motivo = motivo,
                fechaHora = ahora
            )
        )

        if (tipo == TipoMovimiento.INGRESO) {
            conteoDao.insert(
                RegistroConteo(accesoId = accesoId, fechaHora = ahora, cantidad = 1, usuarioId = usuarioId)
            )
            flujoDao.insert(
                RegistroFlujo(
                    accesoId = accesoId,
                    fechaHora = ahora,
                    tipoFlujo = motivo,
                    rangoEtario = usuario.rangoEtario,
                    cantidad = 1,
                    usuarioId = usuarioId
                )
            )
        }
        null
    }

    /** Reemplaza los datos FICTICIOS por otros de los últimos 30 días (conserva los de clientes). */
    suspend fun cargarInitializer() {
        val (conteos, flujos) = DatosIniciales.generar()
        // withTransaction: o se guarda todo o no se guarda nada
        db.withTransaction {
            conteoDao.deleteFicticios()
            flujoDao.deleteFicticios()
            conteoDao.insertAll(conteos)
            flujoDao.insertAll(flujos)
        }
    }

    /** Primera vez que se abre el Dashboard: si no hay datos ficticios, se generan automáticamente. */
    suspend fun cargarSiEstaVacio() {
        if (conteoDao.contarFicticios() == 0) cargarInitializer()
    }
}
