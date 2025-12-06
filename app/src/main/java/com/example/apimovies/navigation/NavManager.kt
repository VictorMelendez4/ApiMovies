package com.example.apimovies.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.apimovies.viewModel.MoviesViewModel
import com.example.apimovies.views.DetailView
import com.example.apimovies.views.HomeView

/**
 * Este componente define todas las rutas posibles dentro de la aplicación y gestiona
 * el flujo entre pantallas. Actúa como el contenedor principal que intercambia
 * las vistas según la URL actual.
 */
@Composable
fun NavManager(viewModel: MoviesViewModel) {
    // Controlador de navegación: Recuerda el historial de pantallas
    val navController = rememberNavController()

    // Configuración del Host de navegación
    NavHost(
        navController = navController,
        startDestination = "Home" // Pantalla inicial al abrir la app
    ) {

        // RUTA 1: PANTALLA DE INICIO (HOME)
        // Ruta estática simple.
        composable(route = "Home") {
            HomeView(viewModel, navController)
        }

        // RUTA 2: PANTALLA DE DETALLE
        // Ruta dinámica que requiere un parámetro: el ID de la película.
        composable(
            route = "Detail/{id}",
            arguments = listOf(
                // Definimos que el argumento 'id' es de tipo String
                navArgument("id") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            // Recuperamos el argumento 'id' de la ruta actual
            val id = backStackEntry.arguments?.getString("id") ?: ""

            // Renderizamos la vista de detalle pasando el ID recuperado
            DetailView(viewModel, navController, id)
        }
    }
}