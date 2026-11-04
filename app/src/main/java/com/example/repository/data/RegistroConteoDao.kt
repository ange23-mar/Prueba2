package com.example.repository.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.repository.models.RegistroConteo
import com.example.repository.models.TotalAcceso
import kotlinx.coroutines.flow.Flow

@Dao
interface RegistroConteoDao {
    // Total de ingresos por acceso desde una fecha
    @Query(
        """
        SELECT accesoId, SUM(cantidad) AS total
        FROM registro_conteo
        WHERE fechaHora >= :desde
        GROUP BY accesoId
        """
    )
    fun totalesPorAcceso(desde: Long): Flow<List<TotalAcceso>>

    @Insert
    suspend fun insert(registro: RegistroConteo): Long

    @Insert
    suspend fun insertAll(registros: List<RegistroConteo>)

    @Query("SELECT COUNT(*) FROM registro_conteo")
    suspend fun contar(): Int

    // Datos ficticios = los que no vienen de un cliente
    @Query("SELECT COUNT(*) FROM registro_conteo WHERE usuarioId IS NULL")
    suspend fun contarFicticios(): Int

    @Query("DELETE FROM registro_conteo WHERE usuarioId IS NULL")
    suspend fun deleteFicticios()

    @Query("DELETE FROM registro_conteo")
    suspend fun deleteAll()
}
