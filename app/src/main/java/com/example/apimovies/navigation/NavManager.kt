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

@Composable
fun NavManager(viewModel: MoviesViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "Home") {


        composable("Home") {
            HomeView(viewModel, navController)
        }


        composable(
            route = "Detail/{id}/{title}/{photoUrl}/{description}",
            arguments = listOf(
                navArgument("id") { type = NavType.StringType },
                navArgument("title") { type = NavType.StringType },
                navArgument("photoUrl") { type = NavType.StringType },
                navArgument("description") { type = NavType.StringType }
            )
        ) { backStackEntry ->

            val id = backStackEntry.arguments?.getString("id") ?: ""
            val title = backStackEntry.arguments?.getString("title") ?: ""
            val photoUrl = backStackEntry.arguments?.getString("photoUrl") ?: ""
            val description = backStackEntry.arguments?.getString("description") ?: ""


            DetailView(viewModel, navController, id, title, photoUrl, description)
        }
    }
}