package com.example.view.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.view.utils.BarraApilada
import com.example.view.utils.ColoresFlujo
import com.example.view.utils.LeyendaItem
import com.example.view.utils.SelectorPeriodo
import com.example.viewmodels.DashboardViewModel
import com.example.viewmodels.ResumenAcceso
import kotlin.math.roundToInt

private fun porcentaje(parte: Int, total: Int): Int =
    if (total == 0) 0 else (parte * 100f / total).roundToInt()

/** Pantalla principal: tránsito vs. compra por cada uno de los 4 accesos. */
@Composable
fun DashboardScreen(viewModel: DashboardViewModel) {
    // collectAsStateWithLifecycle: la pantalla se redibuja cuando cambia el uiState
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val totalTransito = state.resumen.sumOf { it.transito }
    val totalCompra = state.resumen.sumOf { it.compra }
    val totalGeneral = state.resumen.sumOf { it.total }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { SelectorPeriodo(state.periodo, viewModel::cambiarPeriodo) }

        // Tarjeta con el total de la tienda
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Total de la tienda", style = MaterialTheme.typography.titleMedium)
                    Text("$totalGeneral ingresos", style = MaterialTheme.typography.headlineMedium)
                    if (totalGeneral > 0) {
                        Text(
                            "${porcentaje(totalTransito, totalGeneral)}% solo transita · " +
                                    "${porcentaje(totalCompra, totalGeneral)}% compra",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    } else {
                        Text("Sin datos en este periodo. Prueba con otro periodo o regenera los datos de ejemplo.")
                    }
                }
            }
        }

        items(state.resumen, key = { it.acceso.id }) { resumen -> TarjetaAcceso(resumen) }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = viewModel::cargarDatosEjemplo,
                    enabled = !state.ocupado,
                    modifier = Modifier.weight(1f)
                ) { Text("Regenerar datos de ejemplo") }
                if (state.ocupado) CircularProgressIndicator(modifier = Modifier.padding(start = 4.dp))
            }
        }

        state.mensaje?.let { mensaje ->
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(mensaje, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
                    TextButton(onClick = viewModel::cerrarMensaje) { Text("OK") }
                }
            }
        }
    }
}

@Composable
private fun TarjetaAcceso(r: ResumenAcceso) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(r.acceso.nombre, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                Text("${r.total} ingresos", style = MaterialTheme.typography.titleSmall)
            }
            Text(r.acceso.ubicacion, style = MaterialTheme.typography.bodySmall)

            BarraApilada(r.transito, r.compra, r.sinClasificar)

            LeyendaItem(ColoresFlujo.transito, "Solo tránsito: ${r.transito} (${porcentaje(r.transito, r.total)}%)")
            LeyendaItem(ColoresFlujo.compra, "Compra: ${r.compra} (${porcentaje(r.compra, r.total)}%)")
            if (r.sinClasificar > 0) {
                LeyendaItem(ColoresFlujo.sinClasificar, "Sin clasificar: ${r.sinClasificar}")
            }
        }
    }
}
