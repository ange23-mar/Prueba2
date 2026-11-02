package com.example.view.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.example.repository.models.RangoEtario
import com.example.repository.models.RolUsuario
import com.example.repository.models.TipoFlujo
import com.example.repository.models.Usuario
import com.example.view.utils.CameraCapture
import com.example.view.utils.CampoSelector
import com.example.view.utils.FotoPerfilAnimada
import com.example.viewmodels.PerfilViewModel

/**
 * Perfil del usuario: datos básicos + foto con CameraX.
 * Es la ÚNICA pantalla con animaciones de cámara (guía de rostro, flash y avatar animado).
 */
@Composable
fun PerfilScreen(viewModel: PerfilViewModel) {
    val usuario by viewModel.usuario.collectAsStateWithLifecycle()
    val mensaje by viewModel.mensaje.collectAsStateWithLifecycle()
    var showCamera by rememberSaveable { mutableStateOf(false) }

    // La cámara se dibuja ENCIMA del perfil (no lo reemplaza) para que el avatar siga
    // en composición y su animación de "foto nueva" se vea al cerrar la cámara.
    Box(Modifier.fillMaxSize()) {
        val u = usuario
        if (u == null) {
            CircularProgressIndicator(Modifier.align(Alignment.Center))
        } else {
            ContenidoPerfil(
                usuario = u,
                mensaje = mensaje,
                onCerrarMensaje = viewModel::cerrarMensaje,
                onTomarFoto = { showCamera = true },
                onGuardar = viewModel::actualizarPerfilCliente
            )
        }

        if (showCamera) {
            CameraCapture(
                carpeta = "perfil",
                camaraFrontal = true,
                animarCaptura = true, // guía de rostro + destello de obturador
                onImageCaptured = { uri ->
                    viewModel.actualizarFoto(uri.toString())
                    showCamera = false
                },
                onClose = { showCamera = false }
            )
        }
    }
}

@Composable
private fun ContenidoPerfil(
    usuario: Usuario,
    mensaje: String?,
    onCerrarMensaje: () -> Unit,
    onTomarFoto: () -> Unit,
    onGuardar: (TipoFlujo, RangoEtario) -> Unit
) {
    val esCliente = usuario.rol == RolUsuario.CLIENTE
    // Se reinician si cambian en la BD; sobreviven a rotaciones
    var tipo by rememberSaveable(usuario.tipoCliente) {
        mutableStateOf(usuario.tipoCliente ?: TipoFlujo.COMPRA)
    }
    var rango by rememberSaveable(usuario.rangoEtario) { mutableStateOf(usuario.rangoEtario) }
    val hayCambios = tipo != usuario.tipoCliente || rango != usuario.rangoEtario

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        FotoPerfilAnimada(fotoUri = usuario.fotoPerfilUri, onClick = onTomarFoto)

        Text(usuario.nombre, style = MaterialTheme.typography.headlineSmall)
        Text(usuario.email, style = MaterialTheme.typography.bodyMedium)
        AssistChip(onClick = {}, label = { Text(usuario.rol.etiqueta) })

        OutlinedButton(onClick = onTomarFoto) {
            Icon(Icons.Default.PhotoCamera, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text(if (usuario.fotoPerfilUri == null) "Tomar foto de perfil" else "Actualizar foto")
        }

        mensaje?.let {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(it, color = MaterialTheme.colorScheme.primary, modifier = Modifier.weight(1f))
                TextButton(onClick = onCerrarMensaje) { Text("OK") }
            }
        }

        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Información básica", style = MaterialTheme.typography.titleMedium)
                DatoPerfil("Rol", usuario.rol.etiqueta)
                if (esCliente) {
                    DatoPerfil("Tipo de cliente", usuario.tipoCliente?.tipoCliente ?: "Sin definir")
                    DatoPerfil("Rango etario", usuario.rangoEtario.etiqueta)
                }
            }
        }

        // Edición del perfil de cliente (el rango etario alimenta la segmentación del Dashboard)
        if (esCliente) {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Editar perfil de cliente", style = MaterialTheme.typography.titleMedium)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TipoFlujo.entries.forEach { t ->
                            FilterChip(
                                selected = tipo == t,
                                onClick = { tipo = t },
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
                    HorizontalDivider()
                    Button(
                        onClick = { onGuardar(tipo, rango) },
                        enabled = hayCambios,
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Guardar cambios") }
                }
            }
        }
    }
}

@Composable
private fun DatoPerfil(etiqueta: String, valor: String) {
    Row(Modifier.fillMaxWidth()) {
        Text(etiqueta, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
        Text(valor, style = MaterialTheme.typography.bodyMedium)
    }
}
