package com.example.repository.models

//Para distinguir si solo transita o compra
enum class TipoFlujo(val etiqueta: String) {
    TRANSITO("Sólo tránsito"),
    COMPRA("Compra") }

//Para segmentar por edad
enum class RangoEtario(val etiqueta: String) {
    MENOR_18("Menor de 18"),
    ADULTO_18_29("18 a 29"),
    ADULTO_30_44("30 a 44"),
    ADULTO_45_59("45 a 59"),
    MAYOR_60("60 o más"),
    SIN_DATO("Sin dato")
}