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
import com.example.viewmodels.SegmentacionViewModel

class MainActivity : ComponentActivity() {

    // ViewModels (capa VM de MVVM) con alcance de la Activity: sobreviven a rotaciones
    private val dashboardViewModel: DashboardViewModel by viewModels()
    private val segmentacionViewModel: SegmentacionViewModel by viewModels()
    private val flujoViewModel: FlujoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                AppNavigation(dashboardViewModel, segmentacionViewModel, flujoViewModel)
            }
        }
    }
}
