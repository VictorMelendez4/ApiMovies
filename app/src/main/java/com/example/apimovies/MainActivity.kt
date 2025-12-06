package com.example.apimovies

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.apimovies.navigation.NavManager
import com.example.apimovies.ui.theme.ApiMoviesTheme
import com.example.apimovies.viewModel.MoviesViewModel
import dagger.hilt.android.AndroidEntryPoint
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Activa el diseño de borde a borde
        enableEdgeToEdge()

        // Inicialización del ViewModel.

        val viewModel: MoviesViewModel by viewModels()

        // Definición de la Interfaz de Usuario (UI).
        setContent {
            ApiMoviesTheme {
                // Llamamos al NavManager para manejar la navegación.
                NavManager(viewModel)
            }
        }
    }
}