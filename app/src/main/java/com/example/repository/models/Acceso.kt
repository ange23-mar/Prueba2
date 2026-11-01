package com.example.repository.models
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.AccesoErrores

@Entity(tableName = "accesos")
data class Acceso (
    @PrimaryKey (autoGenerate = true) val id: Int = 0,
    val nombreAcceso: String,
    val ubicacionDescripcion: String
)