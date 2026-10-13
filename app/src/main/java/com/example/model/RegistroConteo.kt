package com.example.model

data class RegistroConteo(
    val idRegistro:Int=0,
    val  idAcceso: Int=0,
    val fechaHora:String= "",
    val tipoCLiente:String= "",
    val rangoEtario:String= "",
    val genero: String= "",
    val errorRegistro: RegistroErrores = RegistroErrores()
)
