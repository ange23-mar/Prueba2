package com.example.viewmodels

import java.time.LocalDate
import java.time.ZoneId

/** Periodos de consulta del historial (requisito: "información histórica y por periodo"). */
enum class Periodo(val etiqueta: String) {
    HOY("Hoy"),
    SIETE_DIAS("7 días"),
    TREINTA_DIAS("30 días"),
    TODOS("Todo");

    /** Fecha de inicio del periodo en epoch-millis; las consultas usan "fechaHora >= desde". */
    fun desdeMillis(): Long {
        val zona = ZoneId.systemDefault()
        val hoy = LocalDate.now(zona)
        return when (this) {
            HOY -> hoy
            SIETE_DIAS -> hoy.minusDays(6)
            TREINTA_DIAS -> hoy.minusDays(29)
            TODOS -> return 0L
        }.atStartOfDay(zona).toInstant().toEpochMilli()
    }
}