package com.example.repository.models

/*
 * Clases que Room usa para devolver el resultado de consultas con SUM / GROUP BY.
 * No son tablas: solo "moldes" cuyos nombres de campo coinciden con los alias del SELECT.
 */

data class TotalAcceso(
    val accesoId: Int,
    val total: Int)

data class TotalAccesoTipo(
    val accesoId: Int,
    val tipoFlujo: TipoFlujo,
    val total: Int
)

data class TotalRango(
    val rangoEtario: RangoEtario?,
    val total: Int
)