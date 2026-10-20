package com.example.ui


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.viewmodel.SistemaViewModel



@Composable
fun Registro(
    navController: NavController,
    viewModel: SistemaViewModel
) {
    val estadoProyec by viewModel.estado.collectAsState()
    val usuario = estadoProyec.usuario
    val errorUsuario = estadoProyec.ErrorUsuario

    Column1(
        modifier = Modifier
            .fillMaxSize()
            .padding(all = 16.dp),
        verticalArrangement = Arrangement.spacedBy(space = 12.dp)
    ) {
        Text1(
            text = "Registro de Usuario",
            style = MaterialTheme.typography.headlineMedium
        )

        // Campo ID Usuario
        OutlinedTextField(
            value = usuario.idUsuario,
            onValueChange = { viewModel.onUsuarioIdUsuarioChange(it) },
            label = { Text1(text = "ID Usuario") },
            isError = errorUsuario.idUsuario != null,
            trailingIcon = {
                if (errorUsuario.idUsuario != null) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Error",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            },
            supportingText = {
                errorUsuario.idUsuario?.let {
                    Text1(text = it, color = MaterialTheme.colorScheme.error)
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        // Campo Nombre
        OutlinedTextField(
            value = usuario.nombre,
            onValueChange = { viewModel.onNombreChange(it) },
            label = { Text1(text = "Nombre") },
            isError = errorUsuario.nombre != null,
            trailingIcon = {
                if (errorUsuario.nombre != null) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Error",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            },
            supportingText = {
                errorUsuario.nombre?.let {
                    Text1(text = it, color = MaterialTheme.colorScheme.error)
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        // Campo Correo electrónico
        OutlinedTextField(
            value = usuario.correo,
            onValueChange = { viewModel.onCorreoChange(it) },
            label = { Text1(text = "Correo electrónico") },
            isError = errorUsuario.correo != null,
            trailingIcon = {
                if (errorUsuario.correo != null) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Error",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            },
            supportingText = {
                errorUsuario.correo?.let {
                    Text1(text = it, color = MaterialTheme.colorScheme.error)
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        // Campo Clave / Contraseña
        OutlinedTextField(
            value = usuario.clave,
            onValueChange = { viewModel.onClaveChange(it) },
            label = { Text1(text = "Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            isError = errorUsuario.clave != null,
            trailingIcon = {
                if (errorUsuario.clave != null) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Error",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            },
            supportingText = {
                errorUsuario.clave?.let {
                    Text1(text = it, color = MaterialTheme.colorScheme.error)
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        // Botón: Validar
        Button(
            onClick = {
                if (viewModel.validarFormularioUsuario()) {
                    navController.navigate(route = "resumen")
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text1(text = "Registrar")
        }
    }
}