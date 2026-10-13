package com.example.model

data class ErrorTransaccionVenta(
    val idTransaccion: Int? = null,
    val idAccesoIngreso:Int? = null,
    val idUsuario:Int? = null,
    val montoTotal:Double? = null
)
