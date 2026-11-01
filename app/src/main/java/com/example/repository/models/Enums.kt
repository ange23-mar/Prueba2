package com.example.repository.models

//Para distinguir si solo transita o compra
enum class TipoFlujo { TRANSITO, COMPRA }

//Para llevar segmentar por edad
enum class RangoEtario { MENOR_18, ADULTO_18_29, ADULTO_30_44, ADULTO_45_59, MAYOR_60 }