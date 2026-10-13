package com.example.model

data class ErrorTransaccionVenta(
    val idTransaccion: Int? = null,
    val idAccesoIngreso:Int? = null,
    val idUsuario:Int? = null,
    val fechaHora:String?= "",
    val montoTotal:Double? = null
)
