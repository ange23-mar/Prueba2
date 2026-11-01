package com.example.repository.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.model.AccesoErrores

@Entity(
    tableName = "registro_conteo",
    foreignKeys = [ForeignKey(
        //La relación de llave foránea es con la clase/entidad Acceso como tal,
        // no con una instancia particular de un acceso
        entity = Acceso::class,
        parentColumns = ["id"],
        childColumns = ["accesoId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("accesoId")]
)
data class RegistroConteo(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val accesoId: Int,
    val fechaHora: Long,
    val cantidadIngresos: Int
)