package com.example.repository.models

/**
 * Motivo del paso por la tienda.
 * Se reutiliza también como TIPO DE CLIENTE, así cada registro del cliente
 * cae directamente en las mismas columnas que lee el Dashboard.
 */
enum class TipoFlujo(val etiqueta: String, val tipoCliente: String) {
    TRANSITO("Solo transita", "Cliente en tránsito"),
    COMPRA("Compra", "Cliente comprador")
}

// Para segmentar por edad
enum class RangoEtario(val etiqueta: String) {
    MENOR_18("Menor de 18"),
    ADULTO_18_29("18 a 29"),
    ADULTO_30_44("30 a 44"),
    ADULTO_45_59("45 a 59"),
    MAYOR_60("60 o más"),
    SIN_DATO("Sin dato")
}

/** Rol con el que inicia sesión una persona: decide qué interfaz ve. */
enum class RolUsuario(val etiqueta: String) {
    ADMINISTRADOR("Administrador"),
    CLIENTE("Cliente")
}

/** Movimiento que marca el cliente en un acceso. */
enum class TipoMovimiento(val etiqueta: String) {
    INGRESO("Ingreso"),
    SALIDA("Salida")
}
