package com.example.model

data class RegistroConteo(
    val idRegistro:Int,
    val  idAcceso: Int,
    val fechaHora:String,
    val tipoCLiente:String,
    val rangoEtario:String,
    val genero: String,
    val errorRegistro: RegistroErrores = RegistroErrores()
)
