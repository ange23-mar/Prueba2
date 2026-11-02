package com.example.viewmodels

object Validaciones {
    const val MAX_CANTIDAD = 100_000
    const val MIN_PASSWORD = 6
    const val MAX_NOMBRE = 50

    private val SOLO_LETRAS = Regex("^[A-Za-zÁÉÍÓÚáéíóúÑñÜü]+( [A-Za-zÁÉÍÓÚáéíóúÑñÜü]+)*$")
    private val EMAIL = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*\\.[A-Za-z]{2,}$")

    /** Devuelve la cantidad si es un entero entre 1 y MAX_CANTIDAD; si no, null. */
    fun cantidadValida(texto: String): Int? =
        texto.trim().toIntOrNull()?.takeIf { it in 1..MAX_CANTIDAD }

    /** Solo letras (con tildes) y un espacio entre palabras. */
    fun esSoloLetras(texto: String): Boolean =
        texto.trim().let { it.length <= MAX_NOMBRE && SOLO_LETRAS.matches(it) }

    fun emailValido(email: String): Boolean = EMAIL.matches(email.trim())

    fun passwordValida(password: String): Boolean = password.length >= MIN_PASSWORD
}
