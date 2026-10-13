package com.example.model

data class TransaccionVenta(
    val idTransaccion: Int,
    val idAccesoIngreso:Int,
    val idUsuario:Int,
    val fechaHora:String,
    val montoTotal:Double,
    val errorTransa: ErrorTransaccion = ErrorTransaccionVenta()
)
