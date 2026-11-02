package com.example.repository.models

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Usuario de la app. El rol (ADMINISTRADOR / CLIENTE) define la interfaz tras el login.
 * - tipoCliente: solo para clientes (Cliente en tránsito / Cliente comprador). Null para administradores.
 * - rangoEtario: se copia a cada RegistroFlujo que genera el cliente (alimenta la segmentación).
 */
@Entity(
    tableName = "usuarios",
    indices = [Index(value = ["email"], unique = true)]
)
data class Usuario(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val email: String,
    val password: String,               // Solo para fines académicos (sin hash)
    val rol: RolUsuario,
    val tipoCliente: TipoFlujo? = null,
    val rangoEtario: RangoEtario = RangoEtario.SIN_DATO,
    val fotoPerfilUri: String? = null   // Foto tomada con CameraX (almacenamiento interno)
)
