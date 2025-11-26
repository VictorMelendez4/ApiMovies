package com.example.apimovies

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.apimovies.navigation.NavManager // <--- ¡ESTA ES LA LÍNEA QUE TE FALTA!
import com.example.apimovies.ui.theme.ApiMoviesTheme
import com.example.apimovies.viewModel.MoviesViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val viewModel: MoviesViewModel by viewModels()

        setContent {
            ApiMoviesTheme {
                NavManager(viewModel)
            }
        }
    }
}