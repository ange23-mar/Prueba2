package com.example.model

data class Acceso(
    val idAcceso: Int,
    val nombreAcceso: String,
    val ubicacionDescripcion: String,
    val errores: AccesoErrores = AccesoErrores()
)
