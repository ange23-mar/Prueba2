package com.example.view.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.repository.models.RegistroFlujo
import com.example.view.utils.formatearFecha
import com.example.viewmodels.FlujoViewModel

@Composable
fun FlujoListScreen(viewModel: FlujoViewModel, onAdd: () -> Unit) {
    val flujos by viewModel.flujos.collectAsStateWithLifecycle()
    val accesos by viewModel.accesos.collectAsStateWithLifecycle()
    var porEliminar by remember { mutableStateOf<RegistroFlujo?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        if (flujos.isEmpty()) {
            Text(
                "No hay registros. Presiona + para agregar o regenera los datos de ejemplo en Resumen.",
                modifier = Modifier.align(Alignment.Center).padding(24.dp)
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(flujos, key = { it.id }) { flujo ->
                    val nombreAcceso = accesos.firstOrNull { it.id == flujo.accesoId }?.nombreAcceso
                        ?: "Acceso ${flujo.accesoId}"
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Miniatura de la foto de evidencia (si existe)
                            if (flujo.fotoUri != null) {
                                AsyncImage(
                                    model = flujo.fotoUri,
                                    contentDescription = "Foto del registro",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.size(56.dp).clip(RoundedCornerShape(8.dp))
                                )
                                Spacer(Modifier.width(12.dp))
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "$nombreAcceso · ${flujo.tipoFlujo.etiqueta}",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text("${flujo.cantidad} personas · ${flujo.rangoEtario.etiqueta}")
                                // Registros generados automáticamente por el Ingreso de un cliente
                                if (flujo.usuarioId != null) {
                                    Text(
                                        "Registrado por cliente (app)",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                }
                                Text(
                                    formatearFecha(flujo.fechaHora),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            IconButton(onClick = { porEliminar = flujo }) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Eliminar",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = onAdd,
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Agregar registro")
        }
    }

    // Confirmación antes de borrar
    porEliminar?.let { flujo ->
        AlertDialog(
            onDismissRequest = { porEliminar = null },
            title = { Text("Eliminar registro") },
            text = { Text("¿Eliminar el registro de ${flujo.cantidad} personas (${flujo.tipoFlujo.etiqueta})?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.eliminar(flujo)
                    porEliminar = null
                }) { Text("Eliminar") }
            },
            dismissButton = { TextButton(onClick = { porEliminar = null }) { Text("Cancelar") } }
        )
    }
}
