package com.example.navegations

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.repository.models.RolUsuario
import com.example.repository.models.Usuario
import com.example.view.ui.ClienteMovimientosScreen
import com.example.view.ui.DashboardScreen
import com.example.view.ui.FlujoFormScreen
import com.example.view.ui.FlujoListScreen
import com.example.view.ui.LoginScreen
import com.example.view.ui.PerfilScreen
import com.example.view.ui.RegistroClienteScreen
import com.example.view.ui.SegmentacionScreen
import com.example.view.utils.navEnterTransition
import com.example.view.utils.navExitTransition
import com.example.view.utils.navPopEnterTransition
import com.example.view.utils.navPopExitTransition
import com.example.viewmodels.DashboardViewModel
import com.example.viewmodels.FlujoViewModel
import com.example.viewmodels.LoginViewModel
import com.example.viewmodels.MovimientoViewModel
import com.example.viewmodels.PerfilViewModel
import com.example.viewmodels.SegmentacionViewModel
import com.example.viewmodels.UsuarioViewModel
import kotlinx.coroutines.launch

/** Rutas de navegación */
object Routes {
    // Públicas
    const val LOGIN = "login"
    const val REGISTRO = "registro_cliente"

    // ADMINISTRADOR
    const val DASHBOARD = "dashboard"
    const val SEGMENTACION = "segmentacion"
    const val FLUJOS = "flujos"
    const val FLUJO_FORM = "flujo_form"

    // CLIENTE
    const val CLIENTE_MOVIMIENTOS = "cliente_movimientos"
    const val CLIENTE_PERFIL = "cliente_perfil"

    val publicas = setOf(LOGIN, REGISTRO)
    val administrador = setOf(DASHBOARD, SEGMENTACION, FLUJOS, FLUJO_FORM)
    val cliente = setOf(CLIENTE_MOVIMIENTOS, CLIENTE_PERFIL)

    /** Pantalla inicial según el rol: Dashboard para admin, Ingreso/Salida para cliente. */
    fun inicioPara(rol: RolUsuario): String = when (rol) {
        RolUsuario.ADMINISTRADOR -> DASHBOARD
        RolUsuario.CLIENTE -> CLIENTE_MOVIMIENTOS
    }

    fun permitida(route: String, rol: RolUsuario): Boolean = when (rol) {
        RolUsuario.ADMINISTRADOR -> route in administrador
        RolUsuario.CLIENTE -> route in cliente
    }
}

data class MenuItem(val route: String, val label: String, val icon: ImageVector)

/** Menú lateral del ADMINISTRADOR (Dashboard existente) */
val menuAdministrador = listOf(
    MenuItem(Routes.DASHBOARD, "Resumen por acceso", Icons.Default.Dashboard),
    MenuItem(Routes.SEGMENTACION, "Perfil de tránsito", Icons.Default.PieChart),
    MenuItem(Routes.FLUJOS, "Registros", Icons.AutoMirrored.Filled.ListAlt)
)

/** Menú lateral del CLIENTE */
val menuCliente = listOf(
    MenuItem(Routes.CLIENTE_MOVIMIENTOS, "Ingreso / Salida", Icons.Default.SwapHoriz),
    MenuItem(Routes.CLIENTE_PERFIL, "Mi perfil", Icons.Default.AccountCircle)
)

fun menuPara(rol: RolUsuario?): List<MenuItem> = when (rol) {
    RolUsuario.ADMINISTRADOR -> menuAdministrador
    RolUsuario.CLIENTE -> menuCliente
    null -> emptyList()
}

private fun titleFor(route: String?): String = when (route) {
    Routes.REGISTRO -> "Registro de cliente"
    Routes.DASHBOARD -> "Resumen por acceso"
    Routes.SEGMENTACION -> "Perfil de tránsito"
    Routes.FLUJOS -> "Registros"
    Routes.FLUJO_FORM -> "Nuevo registro"
    Routes.CLIENTE_MOVIMIENTOS -> "Ingreso / Salida"
    Routes.CLIENTE_PERFIL -> "Mi perfil"
    else -> ""
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(
    loginViewModel: LoginViewModel,
    usuarioViewModel: UsuarioViewModel,
    dashboardViewModel: DashboardViewModel,
    segmentacionViewModel: SegmentacionViewModel,
    flujoViewModel: FlujoViewModel,
    perfilViewModel: PerfilViewModel,
    movimientoViewModel: MovimientoViewModel
) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val loginState by loginViewModel.uiState.collectAsStateWithLifecycle()
    val sesion = loginState.loggedUser
    // Versión reactiva del usuario (refleja foto/tipo de cliente actualizados).
    // Se valida el id para no mostrar datos de una sesión anterior.
    val perfil by perfilViewModel.usuario.collectAsStateWithLifecycle()
    val usuarioActual: Usuario? = perfil?.takeIf { it.id == sesion?.id } ?: sesion

    val menu = menuPara(sesion?.rol)
    val isTopLevel = menu.any { it.route == currentRoute }

    // Los ViewModels del cliente siguen a la sesión activa
    LaunchedEffect(sesion?.id) {
        perfilViewModel.setUsuario(sesion?.id)
        movimientoViewModel.setUsuario(sesion?.id)
    }

    // Protección de rutas: sin sesión -> Login; rol sin permiso -> su pantalla de inicio
    LaunchedEffect(sesion, currentRoute) {
        val route = currentRoute ?: return@LaunchedEffect
        if (route in Routes.publicas) return@LaunchedEffect
        when {
            sesion == null -> navController.navigate(Routes.LOGIN) {
                popUpTo(navController.graph.id) { inclusive = true }
                launchSingleTop = true
            }
            !Routes.permitida(route, sesion.rol) -> navController.navigate(Routes.inicioPara(sesion.rol)) {
                popUpTo(navController.graph.id) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    fun navigateTopLevel(route: String) {
        val inicio = sesion?.let { Routes.inicioPara(it.rol) } ?: return
        navController.navigate(route) {
            popUpTo(inicio) { saveState = false }
            launchSingleTop = true
        }
    }

    fun cerrarSesion() {
        scope.launch { drawerState.close() }
        loginViewModel.logout()
        navController.navigate(Routes.LOGIN) {
            popUpTo(navController.graph.id) { inclusive = true }
            launchSingleTop = true
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = isTopLevel,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(Modifier.height(16.dp))
                CabeceraMenu(usuarioActual)
                HorizontalDivider(Modifier.padding(vertical = 8.dp))
                menu.forEach { item ->
                    NavigationDrawerItem(
                        label = { Text(item.label) },
                        icon = { Icon(item.icon, contentDescription = null) },
                        selected = currentRoute == item.route,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navigateTopLevel(item.route)
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
                HorizontalDivider(Modifier.padding(vertical = 8.dp))
                NavigationDrawerItem(
                    label = { Text("Cerrar sesión") },
                    icon = { Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null) },
                    selected = false,
                    onClick = ::cerrarSesion,
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                if (currentRoute != null && currentRoute != Routes.LOGIN) {
                    TopAppBar(
                        title = { Text(titleFor(currentRoute)) },
                        navigationIcon = {
                            if (isTopLevel) {
                                IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                    Icon(Icons.Default.Menu, contentDescription = "Menú")
                                }
                            } else {
                                IconButton(onClick = { navController.popBackStack() }) {
                                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                                }
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }
            }
        ) { innerPadding ->
            AppNavHost(
                navController = navController,
                modifier = Modifier.padding(innerPadding),
                loginViewModel = loginViewModel,
                usuarioViewModel = usuarioViewModel,
                dashboardViewModel = dashboardViewModel,
                segmentacionViewModel = segmentacionViewModel,
                flujoViewModel = flujoViewModel,
                perfilViewModel = perfilViewModel,
                movimientoViewModel = movimientoViewModel,
                usuarioActual = usuarioActual
            )
        }
    }
}

/** Cabecera del menú lateral: foto de perfil (si existe), nombre, email y rol. */
@Composable
private fun CabeceraMenu(usuario: Usuario?) {
    Row(
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (usuario?.fotoPerfilUri != null) {
            AsyncImage(
                model = usuario.fotoPerfilUri,
                contentDescription = "Foto de perfil",
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(48.dp).clip(CircleShape)
            )
        } else {
            Icon(Icons.Default.AccountCircle, contentDescription = null, modifier = Modifier.size(48.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column {
            Text(usuario?.nombre ?: "Tienda Rincón Sur", style = MaterialTheme.typography.titleMedium)
            Text(usuario?.email ?: "", style = MaterialTheme.typography.bodySmall)
            Text(
                usuario?.rol?.etiqueta ?: "",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier,
    loginViewModel: LoginViewModel,
    usuarioViewModel: UsuarioViewModel,
    dashboardViewModel: DashboardViewModel,
    segmentacionViewModel: SegmentacionViewModel,
    flujoViewModel: FlujoViewModel,
    perfilViewModel: PerfilViewModel,
    movimientoViewModel: MovimientoViewModel,
    usuarioActual: Usuario?
) {
    // Transiciones de deslizamiento definidas en view/utils/Animations.kt
    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN,
        modifier = modifier,
        enterTransition = navEnterTransition,
        exitTransition = navExitTransition,
        popEnterTransition = navPopEnterTransition,
        popExitTransition = navPopExitTransition
    ) {
        // ---------- Públicas ----------
        composable(Routes.LOGIN) {
            LoginScreen(
                viewModel = loginViewModel,
                onLoginSuccess = { usuario ->
                    // El ROL decide la interfaz: Dashboard (admin) o Ingreso/Salida (cliente)
                    navController.navigate(Routes.inicioPara(usuario.rol)) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onRegistrarse = { navController.navigate(Routes.REGISTRO) }
            )
        }
        composable(Routes.REGISTRO) {
            RegistroClienteScreen(
                viewModel = usuarioViewModel,
                onRegistrado = { email ->
                    loginViewModel.onEmailChange(email) // precarga el email en el login
                    navController.popBackStack()
                },
                onCancelar = { navController.popBackStack() }
            )
        }

        // ---------- ADMINISTRADOR ----------
        composable(Routes.DASHBOARD) { DashboardScreen(dashboardViewModel) }
        composable(Routes.SEGMENTACION) { SegmentacionScreen(segmentacionViewModel) }
        composable(Routes.FLUJOS) {
            FlujoListScreen(
                viewModel = flujoViewModel,
                onAdd = { navController.navigate(Routes.FLUJO_FORM) }
            )
        }
        composable(Routes.FLUJO_FORM) {
            FlujoFormScreen(
                viewModel = flujoViewModel,
                onDone = { navController.popBackStack() }
            )
        }

        // ---------- CLIENTE ----------
        composable(Routes.CLIENTE_MOVIMIENTOS) {
            ClienteMovimientosScreen(viewModel = movimientoViewModel, usuario = usuarioActual)
        }
        composable(Routes.CLIENTE_PERFIL) { PerfilScreen(perfilViewModel) }
    }
}
