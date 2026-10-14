package com.example.model

data class RegistroConteo(
    val idRegistro:String = null,
    val idAcceso: String = null,
    val fechaHora:String= "",
    val tipoCliente:String= "",
    val rangoEtario:String= "",
    val genero: String= "",
    val errorRegistro: RegistroErrores = RegistroErrores()
)
