package com.example.repository.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/** Bitácora de ingresos y salidas marcados por un cliente desde la app. */
@Entity(
    tableName = "movimiento_cliente",
    foreignKeys = [
        ForeignKey(
            entity = Usuario::class,
            parentColumns = ["id"],
            childColumns = ["usuarioId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Acceso::class,
            parentColumns = ["id"],
            childColumns = ["accesoId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("usuarioId"), Index("accesoId"), Index("fechaHora")]
)
data class MovimientoCliente(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val usuarioId: Int,
    val accesoId: Int,
    val tipoMovimiento: TipoMovimiento,
    val motivo: TipoFlujo,              // "Solo transita" o "Compra"
    val fechaHora: Long
)
