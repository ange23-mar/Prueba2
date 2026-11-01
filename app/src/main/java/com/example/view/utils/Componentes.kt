package com.example.view.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.viewmodels.Periodo
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/* Composables reutilizados por varias pantallas. */

/** Fila de chips para elegir el periodo de consulta (Hoy / 7 días / 30 días / Todo). */
@Composable
fun SelectorPeriodo(periodo: Periodo, onCambio: (Periodo) -> Unit) {
    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Periodo.entries.forEach { p ->
            FilterChip(
                selected = p == periodo,
                onClick = { onCambio(p) },
                label = { Text(p.etiqueta) }
            )
        }
    }
}

/** Colores de cada tipo de flujo, tomados del tema Material 3. */
object ColoresFlujo {
    val transito: Color @Composable get() = MaterialTheme.colorScheme.tertiary
    val compra: Color @Composable get() = MaterialTheme.colorScheme.primary
    val sinClasificar: Color @Composable get() = MaterialTheme.colorScheme.outlineVariant
}

/**
 * Barra horizontal apilada: el ancho de cada tramo es proporcional a su cantidad.
 * Usa Modifier.weight(), por eso solo se dibujan los tramos con valor > 0.
 */
@Composable
fun BarraApilada(transito: Int, compra: Int, sinClasificar: Int, modifier: Modifier = Modifier) {
    val tramos = listOf(
        transito to ColoresFlujo.transito,
        compra to ColoresFlujo.compra,
        sinClasificar to ColoresFlujo.sinClasificar
    ).filter { it.first > 0 }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(14.dp)
            .clip(RoundedCornerShape(7.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        tramos.forEach { (valor, color) ->
            Box(
                modifier = Modifier
                    .weight(valor.toFloat())
                    .fillMaxHeight()
                    .background(color)
            )
        }
    }
}

/** Punto de color + texto, para la leyenda de las barras. */
@Composable
fun LeyendaItem(color: Color, texto: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(10.dp).clip(CircleShape).background(color))
        Spacer(Modifier.width(6.dp))
        Text(texto, style = MaterialTheme.typography.bodySmall)
    }
}

/** Selector desplegable genérico (acceso, rango etario...). */
@Composable
fun <T> CampoSelector(
    etiqueta: String,
    valorActual: String,
    opciones: List<T>,
    textoDe: (T) -> String,
    onSeleccion: (T) -> Unit
) {
    var abierto by remember { mutableStateOf(false) }
    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedButton(onClick = { abierto = true }, modifier = Modifier.fillMaxWidth()) {
            Text("$etiqueta: $valorActual")
            Spacer(Modifier.weight(1f))
            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
        }
        DropdownMenu(expanded = abierto, onDismissRequest = { abierto = false }) {
            opciones.forEach { opcion ->
                DropdownMenuItem(
                    text = { Text(textoDe(opcion)) },
                    onClick = {
                        onSeleccion(opcion)
                        abierto = false
                    }
                )
            }
        }
    }
}

private val formatoFecha: DateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")

fun formatearFecha(millis: Long): String =
    Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).format(formatoFecha)
