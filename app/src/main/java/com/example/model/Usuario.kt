package com.example.model

data class Usuario(
    val idUsuario: Int=0,
    val nombre:String= "",
    val rol:String= "",
    val correo: String="",
    val clave: String="",
    val ErrorUsu: ErrorUsuario = ErrorUsuario()
)
