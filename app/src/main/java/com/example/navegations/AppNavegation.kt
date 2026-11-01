package com.example.navegations

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PieChart
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.view.ui.DashboardScreen
import com.example.view.ui.FlujoFormScreen
import com.example.view.ui.FlujoListScreen
import com.example.view.ui.SegmentacionScreen
import com.example.view.utils.navEnterTransition
import com.example.view.utils.navExitTransition
import com.example.view.utils.navPopEnterTransition
import com.example.view.utils.navPopExitTransition
import com.example.viewmodels.DashboardViewModel
import com.example.viewmodels.FlujoViewModel
import com.example.viewmodels.SegmentacionViewModel
import kotlinx.coroutines.launch

/** Rutas de navegación */
object Routes {
    const val DASHBOARD = "dashboard"
    const val SEGMENTACION = "segmentacion"
    const val FLUJOS = "flujos"
    const val FLUJO_FORM = "flujo_form"
}

data class MenuItem(val route: String, val label: String, val icon: ImageVector)

/** Opciones del menú lateral */
val menuItems = listOf(
    MenuItem(Routes.DASHBOARD, "Resumen por acceso", Icons.Default.Dashboard),
    MenuItem(Routes.SEGMENTACION, "Perfil de tránsito", Icons.Default.PieChart),
    MenuItem(Routes.FLUJOS, "Registros", Icons.AutoMirrored.Filled.ListAlt)
)

private fun titleFor(route: String?): String = when (route) {
    Routes.DASHBOARD -> "Resumen por acceso"
    Routes.SEGMENTACION -> "Perfil de tránsito"
    Routes.FLUJOS -> "Registros"
    Routes.FLUJO_FORM -> "Nuevo registro"
    else -> ""
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(
    dashboardViewModel: DashboardViewModel,
    segmentacionViewModel: SegmentacionViewModel,
    flujoViewModel: FlujoViewModel
) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val isTopLevel = menuItems.any { it.route == currentRoute }

    fun navigateTopLevel(route: String) {
        navController.navigate(route) {
            popUpTo(Routes.DASHBOARD) { saveState = false }
            launchSingleTop = true
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = isTopLevel,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(Modifier.height(16.dp))
                Text(
                    "Tienda Rincón Sur",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
                Text(
                    "Conteo de accesos · Plaza Norte",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp)
                )
                HorizontalDivider(Modifier.padding(vertical = 8.dp))
                menuItems.forEach { item ->
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
            }
        }
    ) {
        Scaffold(
            topBar = {
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
        ) { innerPadding ->
            AppNavHost(
                navController = navController,
                modifier = Modifier.padding(innerPadding),
                dashboardViewModel = dashboardViewModel,
                segmentacionViewModel = segmentacionViewModel,
                flujoViewModel = flujoViewModel
            )
        }
    }
}

@Composable
private fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier,
    dashboardViewModel: DashboardViewModel,
    segmentacionViewModel: SegmentacionViewModel,
    flujoViewModel: FlujoViewModel
) {
    // Transiciones de deslizamiento definidas en view/utils/Animations.kt
    NavHost(
        navController = navController,
        startDestination = Routes.DASHBOARD,
        modifier = modifier,
        enterTransition = navEnterTransition,
        exitTransition = navExitTransition,
        popEnterTransition = navPopEnterTransition,
        popExitTransition = navPopExitTransition
    ) {
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
    }
}
