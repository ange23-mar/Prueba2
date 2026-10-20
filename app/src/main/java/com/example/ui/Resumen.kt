package com.example.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposableInferredTarget
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.viewmodel.SistemaViewModel

@Composable
fun Resumen(viewModel: SistemaViewModel) {
    val estadoProyec by viewModel.estado.collectAsState()
    val usuario = estadoProyec.usuario


    @Composable
    fun Resumen(viewModel: SistemaViewModel) {
        val estadoProyec by viewModel.estado.collectAsState()
        val usuario = estadoProyec.usuario

        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Resumen Registro", style = MaterialTheme.typography.headlineMedium)
            Text(text = "ID Usuario: ${usuario.idUsuario}")
            Text(text = "Nombre: ${usuario.nombre}")
            Text(text = "Correo: ${usuario.correo}")
            Text(text = "Contraseña: ${"*".repeat(usuario.clave.length)}")
        }
    }
}







