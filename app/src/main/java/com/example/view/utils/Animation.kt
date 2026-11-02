package com.example.view.utils

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry

/*
 * Animaciones de la app reunidas en un solo lugar:
 *   1) AnimacionEntradaFormulario -> Login, Registro de cliente, FlujoFormScreen
 *   2) Transiciones de navegación  -> NavHost en AppNavigation
 *   3) Barras de porcentaje animadas (animateFloatAsState) -> SegmentacionScreen
 *
 * ANIMACIONES DE CÁMARA: se usan SOLO en el PERFIL DE USUARIO
 *   (PerfilScreen -> CameraCapture(animarCaptura = true) y FotoPerfilAnimada):
 *   P1) Modifier.pulsoYRotacion()  -> ícono de cámara del avatar cuando aún no hay foto
 *   P2) GuiaRostroAnimada()        -> círculo guía "respirando" sobre la vista previa en vivo
 *   P3) DestelloObturador()        -> flash blanco al presionar el obturador
 *   P4) FotoPerfilAnimada (FotoPerfil.kt) -> la foto nueva entra con rebote + onda expansiva
 */

// 1) El contenido aparece con fade + deslizamiento vertical
@Composable
fun AnimacionEntradaFormulario(
    visible: Boolean,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(500)) + slideInVertically(tween(500)) { it / 2 },
        content = content
    )
}

// 2) Deslizamiento horizontal entre pantallas
private const val DURACION_NAV = 400

val navEnterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
    slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(DURACION_NAV)) +
            fadeIn(tween(DURACION_NAV))
}

val navExitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
    slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(DURACION_NAV)) +
            fadeOut(tween(DURACION_NAV))
}

val navPopEnterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
    slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(DURACION_NAV)) +
            fadeIn(tween(DURACION_NAV))
}

val navPopExitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
    slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(DURACION_NAV)) +
            fadeOut(tween(DURACION_NAV))
}

// ---------------------------------------------------------------------------
// P1) PERFIL: pulso de escala + rotación suave, infinita.
// Uso: Modifier.pulsoYRotacion()
// ---------------------------------------------------------------------------
@Composable
fun Modifier.pulsoYRotacion(): Modifier {
    val transition = rememberInfiniteTransition(label = "pulsoYRotacion")
    val scale by transition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(tween(900, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "scale"
    )
    val rotation by transition.animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(tween(1400), RepeatMode.Reverse),
        label = "rotation"
    )
    return this.graphicsLayer {
        scaleX = scale
        scaleY = scale
        rotationZ = rotation
    }
}

// ---------------------------------------------------------------------------
// P2) PERFIL: guía circular punteada que "respira" sobre la vista previa en vivo,
// para que el cliente encuadre su rostro.
// ---------------------------------------------------------------------------
@Composable
fun GuiaRostroAnimada(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "guiaRostro")
    val escala by transition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(tween(1100, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "escalaGuia"
    )
    val alpha by transition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(tween(1100), RepeatMode.Reverse),
        label = "alphaGuia"
    )
    Canvas(modifier = modifier.fillMaxSize()) {
        drawCircle(
            color = Color.White.copy(alpha = alpha),
            radius = size.minDimension * 0.36f * escala,
            center = center,
            style = Stroke(
                width = 4.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(28f, 18f))
            )
        )
    }
}

// ---------------------------------------------------------------------------
// P3) PERFIL: destello de obturador. Cada vez que "disparos" aumenta, la pantalla
// se pone blanca de golpe y se desvanece suavemente (350 ms), como un flash.
// ---------------------------------------------------------------------------
@Composable
fun DestelloObturador(disparos: Int, modifier: Modifier = Modifier) {
    val alpha = remember { Animatable(0f) }
    LaunchedEffect(disparos) {
        if (disparos > 0) {
            alpha.snapTo(0.85f)
            alpha.animateTo(0f, animationSpec = tween(350))
        }
    }
    if (alpha.value > 0f) {
        Box(modifier.fillMaxSize().background(Color.White.copy(alpha = alpha.value)))
    }
}
