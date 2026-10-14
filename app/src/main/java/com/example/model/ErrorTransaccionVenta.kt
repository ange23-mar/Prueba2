package com.example.model

data class ErrorTransaccionVenta(
    val idTransaccion: Int? = null,
    val idAccesoIngreso:Int? = null,
    val idUsuario:String? = "",
    val fechaHora:String?= "",
    val montoTotal:Double? = null
)
