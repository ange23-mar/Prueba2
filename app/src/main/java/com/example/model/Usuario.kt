package com.example.model

data class Usuario(
    val idUsuario: Int,
    val nombre:String,
    val rol:String,
    val ErrorUsu: ErrorUsuario = ErrorUsuario()
)
