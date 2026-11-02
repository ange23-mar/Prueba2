package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.navegations.AppNavigation
import com.example.view.theme.AppTheme
import com.example.viewmodels.DashboardViewModel
import com.example.viewmodels.FlujoViewModel
import com.example.viewmodels.LoginViewModel
import com.example.viewmodels.MovimientoViewModel
import com.example.viewmodels.PerfilViewModel
import com.example.viewmodels.SegmentacionViewModel
import com.example.viewmodels.UsuarioViewModel

class MainActivity : ComponentActivity() {

    // ViewModels (capa VM de MVVM) con alcance de la Activity: sobreviven a rotaciones
    private val loginViewModel: LoginViewModel by viewModels()
    private val usuarioViewModel: UsuarioViewModel by viewModels()
    private val dashboardViewModel: DashboardViewModel by viewModels()
    private val segmentacionViewModel: SegmentacionViewModel by viewModels()
    private val flujoViewModel: FlujoViewModel by viewModels()
    private val perfilViewModel: PerfilViewModel by viewModels()
    private val movimientoViewModel: MovimientoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                AppNavigation(
                    loginViewModel = loginViewModel,
                    usuarioViewModel = usuarioViewModel,
                    dashboardViewModel = dashboardViewModel,
                    segmentacionViewModel = segmentacionViewModel,
                    flujoViewModel = flujoViewModel,
                    perfilViewModel = perfilViewModel,
                    movimientoViewModel = movimientoViewModel
                )
            }
        }
    }
}
