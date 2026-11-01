package com.example.view.utils

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry

/*
 * Animaciones de la app reunidas en un solo lugar:
 *   1) AnimacionEntradaFormulario -> FlujoFormScreen
 *   2) Transiciones de navegación  -> NavHost en AppNavigation
 *   3) Barras de porcentaje animadas (animateFloatAsState) -> Componentes.kt / SegmentacionScreen
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