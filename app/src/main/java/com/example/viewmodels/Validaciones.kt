package com.example.viewmodels

object Validaciones {
    const val MAX_CANTIDAD = 100_000

    /** Devuelve la cantidad si es un entero entre 1 y MAX_CANTIDAD; si no, null. */
    fun cantidadValida(texto: String): Int? =
        texto.trim().toIntOrNull()?.takeIf { it in 1..MAX_CANTIDAD }
}