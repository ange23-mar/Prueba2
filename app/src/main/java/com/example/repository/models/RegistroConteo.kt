package com.example.repository.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "registro_conteo",
    foreignKeys = [
        // La relación de llave foránea es con la clase/entidad Acceso como tal,
        // no con una instancia particular de un acceso
        ForeignKey(
            entity = Acceso::class,
            parentColumns = ["id"],
            childColumns = ["accesoId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Usuario::class,
            parentColumns = ["id"],
            childColumns = ["usuarioId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("accesoId"), Index("usuarioId")]
)
data class RegistroConteo(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val accesoId: Int,
    val fechaHora: Long,
    val cantidad: Int,
    val usuarioId: Int? = null // != null cuando el ingreso lo marcó un cliente desde la app
)
