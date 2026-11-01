package com.example.repository.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.repository.models.RegistroConteo
import com.example.repository.models.TotalAcceso
import kotlinx.coroutines.flow.Flow

@Dao
interface RegistroConteoDao {
    // Total de ingresos por acceso desde una fecha (el "conteo agregado" del sistema actual)
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
    suspend fun insertAll(registros: List<RegistroConteo>)

    @Query("DELETE FROM registro_conteo")
    suspend fun deleteAll()
}