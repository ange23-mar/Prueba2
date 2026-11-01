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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.repository.models.RangoEtario
import com.example.repository.models.TipoFlujo
import com.example.view.utils.AnimacionEntradaFormulario
import com.example.view.utils.CampoSelector
import com.example.viewmodels.FlujoViewModel

/** Formulario para registrar cuántas personas de un tipo/rango pasaron por un acceso. */
@Composable
fun FlujoFormScreen(viewModel: FlujoViewModel, onDone: () -> Unit) {
    val accesos by viewModel.accesos.collectAsStateWithLifecycle()

    var accesoId by rememberSaveable { mutableStateOf<Int?>(null) }
    var tipo by rememberSaveable { mutableStateOf(TipoFlujo.TRANSITO) }
    var rango by rememberSaveable { mutableStateOf(RangoEtario.SIN_DATO) }
    var cantidad by rememberSaveable { mutableStateOf("") }
    var error by rememberSaveable { mutableStateOf<String?>(null) }
    var visible by rememberSaveable { mutableStateOf(false) }

    // Activa la animación de entrada una sola vez al abrir la pantalla
    LaunchedEffect(Unit) { visible = true }

    AnimacionEntradaFormulario(visible = visible) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Nuevo registro de flujo", style = MaterialTheme.typography.headlineMedium)

            CampoSelector(
                etiqueta = "Acceso",
                valorActual = accesos.firstOrNull { it.id == accesoId }?.nombre ?: "Seleccione",
                opciones = accesos,
                textoDe = { it.nombre },
                onSeleccion = { accesoId = it.id }
            )

            Text("Tipo de flujo", style = MaterialTheme.typography.titleSmall)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TipoFlujo.entries.forEach { t ->
                    FilterChip(
                        selected = tipo == t,
                        onClick = { tipo = t },
                        label = { Text(t.etiqueta) }
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

            OutlinedTextField(
                value = cantidad,
                onValueChange = { cantidad = it },
                label = { Text("Cantidad de personas") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            error?.let { Text(it, color = MaterialTheme.colorScheme.error) }

            Button(
                onClick = {
                    // El ViewModel valida; si devuelve un mensaje se muestra, si no, se cierra
                    error = viewModel.guardar(accesoId, tipo, rango, cantidad)
                    if (error == null) onDone()
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Guardar registro") }

            OutlinedButton(onClick = onDone, modifier = Modifier.fillMaxWidth()) { Text("Cancelar") }
        }
    }
}
