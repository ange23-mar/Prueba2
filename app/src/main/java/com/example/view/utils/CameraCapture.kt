package com.example.view.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import java.io.File

/**
 * Componente reutilizable de cámara (CameraX).
 * - Solicita el permiso CAMERA en tiempo de ejecución.
 * - Muestra la vista previa (Preview + PreviewView).
 * - Captura una foto (ImageCapture) y la guarda en el almacenamiento interno privado:
 *   /data/data/com.example.prueba2/files/fotos/<carpeta>/img_<timestamp>.jpg
 * - Permite cambiar entre cámara trasera y frontal.
 *
 * Por defecto NO tiene animaciones. Solo el perfil de usuario activa [animarCaptura],
 * que agrega la guía circular animada sobre la vista previa en vivo y el destello de obturador.
 */
@Composable
fun CameraCapture(
    carpeta: String, // subcarpeta dentro de files/fotos (ej. "registros", "perfil")
    onImageCaptured: (Uri) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    camaraFrontal: Boolean = false, // el perfil abre con la cámara frontal (selfie)
    animarCaptura: Boolean = false  // animaciones de cámara: solo en el perfil
) {
    val context = LocalContext.current
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_GRANTED
        )
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted -> hasPermission = granted }
    )

    LaunchedEffect(Unit) {
        if (!hasPermission) permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    if (hasPermission) {
        CameraPreviewContent(carpeta, onImageCaptured, onClose, modifier, camaraFrontal, animarCaptura)
    } else {
        Column(
            modifier = modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Se necesita el permiso de cámara para continuar.")
            Spacer(Modifier.height(16.dp))
            Button(onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) }) {
                Text("Conceder permiso")
            }
            Spacer(Modifier.height(8.dp))
            Button(onClick = onClose) { Text("Volver") }
        }
    }
}

@Composable
private fun CameraPreviewContent(
    carpeta: String,
    onImageCaptured: (Uri) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier,
    camaraFrontal: Boolean,
    animarCaptura: Boolean
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var lensFacing by remember {
        mutableIntStateOf(
            if (camaraFrontal) CameraSelector.LENS_FACING_FRONT else CameraSelector.LENS_FACING_BACK
        )
    }

    val previewView = remember {
        PreviewView(context).apply { scaleType = PreviewView.ScaleType.FILL_CENTER }
    }
    val imageCapture = remember {
        ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .build()
    }

    // Vincula la cámara al ciclo de vida; se vuelve a ejecutar al cambiar de lente.
    DisposableEffect(lensFacing) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }
            // Si el dispositivo no tiene el lente pedido (ej. emulador sin frontal), usa el otro
            val pedido = CameraSelector.Builder().requireLensFacing(lensFacing).build()
            val selector = if (tieneCamara(cameraProvider, pedido)) pedido
            else if (lensFacing == CameraSelector.LENS_FACING_FRONT) CameraSelector.DEFAULT_BACK_CAMERA
            else CameraSelector.DEFAULT_FRONT_CAMERA
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(lifecycleOwner, selector, preview, imageCapture)
            } catch (e: Exception) {
                Toast.makeText(context, "No se pudo iniciar la cámara", Toast.LENGTH_SHORT).show()
            }
        }, ContextCompat.getMainExecutor(context))

        onDispose {
            if (cameraProviderFuture.isDone) cameraProviderFuture.get().unbindAll()
        }
    }

    // Contador de capturas: dispara el destello de obturador (solo si animarCaptura)
    var disparos by remember { mutableIntStateOf(0) }

    Box(modifier = modifier.fillMaxSize().background(Color.Black)) {
        AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())

        // Animaciones de cámara del PERFIL (ver Animations.kt): guía de rostro + flash
        if (animarCaptura) {
            GuiaRostroAnimada()
            DestelloObturador(disparos)
        }

        IconButton(
            onClick = onClose,
            modifier = Modifier.align(Alignment.TopStart).padding(16.dp)
        ) {
            Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = Color.White)
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(Modifier.size(56.dp))
            FilledIconButton(
                onClick = {
                    if (animarCaptura) disparos++ // dispara el destello (solo perfil)
                    takePhoto(context, carpeta, imageCapture, onImageCaptured)
                },
                modifier = Modifier.size(76.dp),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(Icons.Default.PhotoCamera, contentDescription = "Capturar", modifier = Modifier.size(36.dp))
            }
            IconButton(
                onClick = {
                    lensFacing = if (lensFacing == CameraSelector.LENS_FACING_BACK)
                        CameraSelector.LENS_FACING_FRONT else CameraSelector.LENS_FACING_BACK
                },
                modifier = Modifier.size(56.dp)
            ) {
                Icon(Icons.Default.Cameraswitch, contentDescription = "Cambiar cámara", tint = Color.White)
            }
        }
    }
}

private fun tieneCamara(provider: ProcessCameraProvider, selector: CameraSelector): Boolean =
    try {
        provider.hasCamera(selector)
    } catch (e: Exception) {
        false
    }

/**
 * Guarda la foto en el almacenamiento INTERNO PRIVADO de la app:
 *   /data/data/com.example.prueba2/files/fotos/<carpeta>/img_<timestamp>.jpg
 * - No aparece en la Galería ni en el administrador de archivos.
 * - Otras apps no pueden leerla; no requiere permisos de almacenamiento.
 * - Se elimina al desinstalar la app y está excluida del respaldo (backup_rules.xml y data_extraction_rules.xml).
 */
private fun takePhoto(context: Context, carpeta: String, imageCapture: ImageCapture, onImageCaptured: (Uri) -> Unit) {
    val dir = File(context.filesDir, "fotos/$carpeta").apply { mkdirs() }
    val outputFile = File(dir, "img_${System.currentTimeMillis()}.jpg")
    val outputOptions = ImageCapture.OutputFileOptions.Builder(outputFile).build()

    imageCapture.takePicture(
        outputOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                onImageCaptured(Uri.fromFile(outputFile))
            }

            override fun onError(exception: ImageCaptureException) {
                Toast.makeText(context, "Error al capturar: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        }
    )
}
