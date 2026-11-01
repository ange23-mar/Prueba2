package com.example.repository.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.repository.models.Acceso
import kotlinx.coroutines.flow.Flow


@Dao
interface AccesoDao {
    // READ reactivo: la UI se actualiza sola si la tabla cambia
    @Query("SELECT * FROM accesos ORDER BY nombreAcceso ASC")
    fun getAll(): Flow<List<Acceso>>

    //Buscar por Id
    @Query("SELECT * FROM accesos WHERE id = :id")
    suspend fun getProductById(id: Int): Acceso?

    //Buscar por nombre
    @Query("SELECT * FROM accesos WHERE nombreAcceso = :nombre_acceso")
    suspend fun getProductByName(nombre_acceso: String): Acceso?


    // CREATE
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAcceso(acceso: Acceso): Long

    // UPDATE
    @Update
    suspend fun updateAcceso(acceso: Acceso)

    // DELETE
    @Delete
    suspend fun deleteAcceso(acceso: Acceso)

}
