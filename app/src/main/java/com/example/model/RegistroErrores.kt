package com.example.model

data class RegistroErrores(
    val idRegistro:Int? = null,
    val idAcceso: Int? = null,
    val fechaHora:String?= "",
    val tipoCliente:String?="",
    val rangoEtario:String?= "",
    val genero: String?= ""

)
