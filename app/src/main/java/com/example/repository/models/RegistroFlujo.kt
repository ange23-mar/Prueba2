package com.example.repository.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "registro_flujo",
    foreignKeys = [
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
    indices = [Index("accesoId"), Index("fechaHora"), Index("usuarioId")]
)
data class RegistroFlujo(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val accesoId: Int,
    val fechaHora: Long,
    val tipoFlujo: TipoFlujo,
    val rangoEtario: RangoEtario = RangoEtario.SIN_DATO,
    val cantidad: Int,
    val fotoUri: String? = null, // foto de evidencia tomada con CameraX (opcional)
    val usuarioId: Int? = null   // != null cuando lo generó un cliente desde la app
)
