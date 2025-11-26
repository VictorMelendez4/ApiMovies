package com.example.apimovies.views

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items // <--- ESTE es clave para que la lista funcione
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState // <--- Necesario para 'collectAsState'
import androidx.compose.runtime.getValue // <--- Necesario para el 'by'
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel // <--- Necesario para inyectar el ViewModel
import androidx.navigation.NavController
import com.example.apimovies.components.MovieCard
import com.example.apimovies.viewModel.MoviesViewModel
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeView(viewModel: MoviesViewModel = hiltViewModel(), navController: NavController) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "Top Movies IMDb", color = Color.White) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFFE91E63)
                )
            )
        }
    ) { paddingValues ->
        ContentHomeView(paddingValues, state, navController)
    }
}

@Composable
fun ContentHomeView(
    paddingValues: PaddingValues,
    state: com.example.apimovies.state.MovieState,
    navController: NavController
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else if (state.error != null) {
            Text(text = state.error ?: "Error desconocido", modifier = Modifier.align(Alignment.Center))
        } else {
            LazyColumn {
                items(state.movies) { movie ->
                    MovieCard(movie = movie) {
                        val id = movie.id
                        val title = movie.title ?: "SinTitulo"
                        val description = movie.description ?: "SinDescripcion"
                        val image = movie.primaryImage ?: ""

                        val encodedUrl = URLEncoder.encode(image, StandardCharsets.UTF_8.toString())
                        val encodedDesc = URLEncoder.encode(description, StandardCharsets.UTF_8.toString())
                        val encodedTitle = URLEncoder.encode(title, StandardCharsets.UTF_8.toString())

                        navController.navigate("Detail/$id/$encodedTitle/$encodedUrl/$encodedDesc")
                    }
                }
            }
        }
    }
}