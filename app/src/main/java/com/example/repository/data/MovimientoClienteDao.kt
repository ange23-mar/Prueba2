package com.example.repository.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.repository.models.MovimientoCliente
import com.example.repository.models.TotalMovimiento
import kotlinx.coroutines.flow.Flow

@Dao
interface MovimientoClienteDao {

    // CREATE
    @Insert
    suspend fun insert(movimiento: MovimientoCliente): Long

    // Historial del cliente
    @Query(
        """
        SELECT * FROM movimiento_cliente
        WHERE usuarioId = :usuarioId
        ORDER BY fechaHora DESC, id DESC
        LIMIT 100
        """
    )
    fun historial(usuarioId: Int): Flow<List<MovimientoCliente>>

    // Último movimiento
    @Query("SELECT * FROM movimiento_cliente WHERE usuarioId = :usuarioId ORDER BY fechaHora DESC, id DESC LIMIT 1")
    suspend fun ultimo(usuarioId: Int): MovimientoCliente?

    // Dashboard: ingresos/salidas por motivo en un periodo
    @Query(
        """
        SELECT tipoMovimiento, motivo, COUNT(*) AS total
        FROM movimiento_cliente
        WHERE fechaHora >= :desde
        GROUP BY tipoMovimiento, motivo
        """
    )
    fun totalesDesde(desde: Long): Flow<List<TotalMovimiento>>

    // Dashboard: clientes cuyo último movimiento es un INGRESO (están dentro ahora)
    @Query(
        """
        SELECT COUNT(*) FROM movimiento_cliente m
        WHERE m.tipoMovimiento = 'INGRESO'
          AND m.id = (
              SELECT m2.id FROM movimiento_cliente m2
              WHERE m2.usuarioId = m.usuarioId
              ORDER BY m2.fechaHora DESC, m2.id DESC
              LIMIT 1
          )
        """
    )
    fun clientesDentro(): Flow<Int>
}
