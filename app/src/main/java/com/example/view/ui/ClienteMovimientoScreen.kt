package com.example.view.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.repository.models.MovimientoCliente
import com.example.repository.models.TipoFlujo
import com.example.repository.models.TipoMovimiento
import com.example.repository.models.Usuario
import com.example.view.utils.CampoSelector
import com.example.view.utils.formatearFecha
import com.example.viewmodels.MovimientoViewModel

/** Interfaz principal del CLIENTE: marcar Ingreso / Salida con su motivo y ver su historial. */
@Composable
fun ClienteMovimientosScreen(viewModel: MovimientoViewModel, usuario: Usuario?) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val ultimo = state.ultimo

    // Valores preseleccionados (se recalculan tras cada movimiento y sobreviven a rotaciones):
    //  - Si está dentro: el acceso y motivo de su ingreso.
    //  - Si está fuera: el tipo de cliente de su perfil.
    var accesoId by rememberSaveable(ultimo?.id) {
        mutableStateOf(if (state.dentro) ultimo?.accesoId else null)
    }
    var motivo by rememberSaveable(ultimo?.id, usuario?.tipoCliente) {
        mutableStateOf(if (state.dentro) ultimo?.motivo else usuario?.tipoCliente)
    }

    fun nombreAcceso(id: Int) = state.accesos.firstOrNull { it.id == id }?.nombreAcceso ?: "Acceso $id"

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Hola, ${usuario?.nombre ?: ""}", style = MaterialTheme.typography.headlineSmall)
            usuario?.tipoCliente?.let {
                Text(it.tipoCliente, style = MaterialTheme.typography.bodyMedium)
            }
        }

        // Estado actual: dentro / fuera de la tienda
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (state.dentro) MaterialTheme.colorScheme.primaryContainer
                    else MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    if (state.dentro && ultimo != null) {
                        Text("Estás dentro de la tienda", style = MaterialTheme.typography.titleMedium)
                        Text(
                            "Ingreso por ${nombreAcceso(ultimo.accesoId)} · ${ultimo.motivo.etiqueta}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(formatearFecha(ultimo.fechaHora), style = MaterialTheme.typography.bodySmall)
                    } else {
                        Text("Estás fuera de la tienda", style = MaterialTheme.typography.titleMedium)
                        Text(
                            "Marca tu ingreso al entrar por cualquiera de los accesos.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }

        // Formulario de registro
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                CampoSelector(
                    etiqueta = "Acceso",
                    valorActual = accesoId?.let { nombreAcceso(it) } ?: "Seleccione",
                    opciones = state.accesos,
                    textoDe = { "${it.nombreAcceso} · ${it.ubicacionDescripcion}" },
                    onSeleccion = { accesoId = it.id }
                )

                Text("Motivo de tu visita", style = MaterialTheme.typography.titleSmall)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TipoFlujo.entries.forEach { t ->
                        FilterChip(
                            selected = motivo == t,
                            onClick = { motivo = t },
                            label = { Text(t.etiqueta) },
                            leadingIcon = {
                                Icon(
                                    if (t == TipoFlujo.COMPRA) Icons.Default.ShoppingBag
                                    else Icons.AutoMirrored.Filled.DirectionsWalk,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { viewModel.registrar(TipoMovimiento.INGRESO, accesoId, motivo) },
                        enabled = !state.dentro && !state.procesando,
                        modifier = Modifier.weight(1f).height(52.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Login, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Ingreso")
                    }
                    Button(
                        onClick = { viewModel.registrar(TipoMovimiento.SALIDA, accesoId, motivo) },
                        enabled = state.dentro && !state.procesando,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.tertiary,
                            contentColor = MaterialTheme.colorScheme.onTertiary
                        ),
                        modifier = Modifier.weight(1f).height(52.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Salida")
                    }
                }

                state.aviso?.let { aviso ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            aviso.texto,
                            color = if (aviso.esError) MaterialTheme.colorScheme.error
                            else MaterialTheme.colorScheme.primary,
                            modifier = Modifier.weight(1f)
                        )
                        TextButton(onClick = viewModel::cerrarAviso) { Text("OK") }
                    }
                }
            }
        }

        item { Text("Mis últimos movimientos", style = MaterialTheme.typography.titleMedium) }

        if (state.historial.isEmpty()) {
            item { Text("Aún no registras movimientos.", style = MaterialTheme.typography.bodyMedium) }
        } else {
            items(state.historial, key = { it.id }) { mov ->
                FilaMovimiento(mov, nombreAcceso(mov.accesoId))
            }
        }
    }
}

@Composable
private fun FilaMovimiento(mov: MovimientoCliente, nombreAcceso: String) {
    val esIngreso = mov.tipoMovimiento == TipoMovimiento.INGRESO
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                if (esIngreso) Icons.AutoMirrored.Filled.Login else Icons.AutoMirrored.Filled.Logout,
                contentDescription = null,
                tint = if (esIngreso) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    "${mov.tipoMovimiento.etiqueta} · $nombreAcceso",
                    style = MaterialTheme.typography.titleSmall
                )
                Text(mov.motivo.etiqueta, style = MaterialTheme.typography.bodyMedium)
            }
            Text(formatearFecha(mov.fechaHora), style = MaterialTheme.typography.bodySmall)
        }
    }
}
