package com.example.view.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.repository.models.RangoEtario
import com.example.repository.models.TipoFlujo
import com.example.view.utils.AnimacionEntradaFormulario
import com.example.view.utils.CampoSelector
import com.example.viewmodels.UsuarioViewModel

/** Formulario de creación de cuenta de CLIENTE (rol fijo). */
@Composable
fun RegistroClienteScreen(
    viewModel: UsuarioViewModel,
    onRegistrado: (email: String) -> Unit,
    onCancelar: () -> Unit
) {
    val mensaje by viewModel.message.collectAsStateWithLifecycle()
    val guardando by viewModel.guardando.collectAsStateWithLifecycle()

    var nombre by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmacion by rememberSaveable { mutableStateOf("") }
    var tipoCliente by rememberSaveable { mutableStateOf(TipoFlujo.COMPRA) }
    var rango by rememberSaveable { mutableStateOf(RangoEtario.SIN_DATO) }
    var visible by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) { visible = true }
    // Limpia mensajes antiguos al salir de la pantalla
    DisposableEffect(Unit) { onDispose { viewModel.clearMessage() } }

    AnimacionEntradaFormulario(visible = visible) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Crear cuenta de cliente", style = MaterialTheme.typography.headlineSmall)

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = confirmacion,
                onValueChange = { confirmacion = it },
                label = { Text("Confirmar contraseña") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
            )

            Text("Tipo de cliente", style = MaterialTheme.typography.titleSmall)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TipoFlujo.entries.forEach { t ->
                    FilterChip(
                        selected = tipoCliente == t,
                        onClick = { tipoCliente = t },
                        label = { Text(t.tipoCliente) }
                    )
                }
            }

            CampoSelector(
                etiqueta = "Rango etario",
                valorActual = rango.etiqueta,
                opciones = RangoEtario.entries.toList(),
                textoDe = { it.etiqueta },
                onSeleccion = { rango = it }
            )

            mensaje?.let { Text(it, color = MaterialTheme.colorScheme.error) }

            Button(
                onClick = {
                    viewModel.registrarCliente(
                        nombre, email, password, confirmacion, tipoCliente, rango, onRegistrado
                    )
                },
                enabled = !guardando,
                modifier = Modifier.fillMaxWidth()
            ) { Text("Crear cuenta") }

            OutlinedButton(onClick = onCancelar, modifier = Modifier.fillMaxWidth()) { Text("Cancelar") }
        }
    }
}
