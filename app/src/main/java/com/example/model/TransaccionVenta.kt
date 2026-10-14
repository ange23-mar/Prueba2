package com.example.model

data class TransaccionVenta(
    val idTransaccion: Int=0,
    val idAccesoIngreso:Int=0,
    val idUsuario:String ="",
    val fechaHora:String= "",
    val montoTotal:Double=0.0,
    val errorTransa: ErrorTransaccionVenta = ErrorTransaccionVenta()
)




