package com.example.repository.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "registro_flujo",
    foreignKeys = [ForeignKey(
        entity = Acceso::class,
        parentColumns = ["id"],
        childColumns = ["accesoId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("accesoId"), Index("fechaHora")]
)
data class RegistroFlujo(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val accesoId: Int,
    val fechaHora: Long,
    val tipoFlujo: TipoFlujo,
    val rangoEtario: RangoEtario?,   // null si no está disponible
    val cantidad: Int
)