package com.example.repository.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.repository.models.RegistroFlujo
import com.example.repository.models.TotalAccesoTipo
import com.example.repository.models.TotalRango
import kotlinx.coroutines.flow.Flow

@Dao
interface RegistroFlujoDao {

    // READ: últimos registros (para la lista)
    @Query("SELECT * FROM registro_flujo WHERE fechaHora >= :desde ORDER BY fechaHora DESC LIMIT 200")
    fun getDesde(desde: Long): Flow<List<RegistroFlujo>>

    // Tránsito vs. compra por acceso en un periodo (pantalla Resumen)
    @Query(
        """
        SELECT accesoId, tipoFlujo, SUM(cantidad) AS total
        FROM registro_flujo
        WHERE fechaHora >= :desde
        GROUP BY accesoId, tipoFlujo
        """
    )
    fun totalesPorAccesoYTipo(desde: Long): Flow<List<TotalAccesoTipo>>

    // Perfil etario de quienes SOLO transitan. accesoId = 0 significa "todos los accesos".
    @Query(
        """
        SELECT rangoEtario, SUM(cantidad) AS total
        FROM registro_flujo
        WHERE tipoFlujo = 'TRANSITO'
          AND (:accesoId = 0 OR accesoId = :accesoId)
          AND fechaHora >= :desde
        GROUP BY rangoEtario
        """
    )
    fun segmentacionTransito(accesoId: Int, desde: Long): Flow<List<TotalRango>>

    // CREATE
    @Insert
    suspend fun insert(registro: RegistroFlujo): Long

    @Insert
    suspend fun insertAll(registros: List<RegistroFlujo>)

    // DELETE
    @Delete
    suspend fun delete(registro: RegistroFlujo)

    // --- Apoyo a la sincronización con el backend ---
    @Query("SELECT * FROM registro_flujo WHERE sincronizado = 0")
    suspend fun pendientes(): List<RegistroFlujo>

    @Query("UPDATE registro_flujo SET sincronizado = 1 WHERE id = :id")
    suspend fun marcarSincronizado(id: Int)

    @Query("DELETE FROM registro_flujo WHERE sincronizado = 1")
    suspend fun borrarSincronizados()

    @Query("DELETE FROM registro_flujo")
    suspend fun deleteAll()
}