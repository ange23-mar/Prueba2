package com.example.view.utils

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

/**
 * Avatar del perfil con ANIMACIONES DE CÁMARA (P1 y P4, ver Animations.kt):
 *  - La foto nueva entra con fade + zoom con rebote (AnimatedContent + spring).
 *  - Al cambiar la foto se dispara una onda circular que se expande y desvanece.
 *  - Si aún no hay foto, el ícono de cámara del distintivo "late" (pulsoYRotacion).
 * Al tocarlo se abre la cámara.
 */
@Composable
fun FotoPerfilAnimada(
    fotoUri: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tamano: Dp = 140.dp
) {
    val colorOnda = MaterialTheme.colorScheme.primary

    // Onda expansiva: progreso 0 -> 1 cada vez que la foto cambia (no en la carga inicial)
    val onda = remember { Animatable(1f) }
    var fotoAnterior by remember { mutableStateOf(fotoUri) }
    LaunchedEffect(fotoUri) {
        if (fotoUri != fotoAnterior) {
            fotoAnterior = fotoUri
            onda.snapTo(0f)
            onda.animateTo(1f, animationSpec = tween(800))
        }
    }

    Box(
        modifier = modifier
            .size(tamano * 1.4f)
            .drawBehind {
                val p = onda.value
                if (p < 1f) {
                    val radioBase = tamano.toPx() / 2f
                    drawCircle(
                        color = colorOnda.copy(alpha = 1f - p),
                        radius = radioBase + radioBase * 0.4f * p,
                        style = Stroke(width = (6.dp.toPx()) * (1f - p) + 1f)
                    )
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(tamano)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .border(3.dp, MaterialTheme.colorScheme.primary, CircleShape)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            AnimatedContent(
                targetState = fotoUri,
                transitionSpec = {
                    (fadeIn(tween(450)) +
                            scaleIn(
                                initialScale = 0.5f,
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessLow
                                )
                            )) togetherWith fadeOut(tween(200))
                },
                label = "fotoPerfilAnimada"
            ) { uri ->
                if (uri != null) {
                    AsyncImage(
                        model = uri,
                        contentDescription = "Foto de perfil",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(tamano * 0.55f),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Distintivo de cámara (abajo a la derecha del avatar)
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = tamano * 0.2f, bottom = tamano * 0.2f)
                .size(tamano * 0.3f)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            val iconModifier = Modifier.size(tamano * 0.17f)
            Icon(
                Icons.Default.PhotoCamera,
                contentDescription = "Cambiar foto",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = if (fotoUri == null) iconModifier.pulsoYRotacion() else iconModifier
            )
        }
    }
}
