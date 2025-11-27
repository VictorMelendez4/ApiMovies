package com.example.apimovies.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.apimovies.components.MovieCard
import com.example.apimovies.viewModel.MoviesViewModel
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeView(viewModel: MoviesViewModel = hiltViewModel(), navController: NavController) {
    val state by viewModel.state.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()

    // Las etiquetas de las pestañas
    val categories = listOf("Top Películas", "Pelis Populares", "Top Series", "Series Populares")

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "ApiMovies", color = Color.White, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFFE91E63)
                )
            )
        }
    ) { paddingValues ->

        Column(modifier = Modifier.padding(paddingValues)) {
            // 1. SECCIÓN DE PESTAÑAS (TABS)
            ScrollableTabRow(
                selectedTabIndex = selectedCategory,
                containerColor = Color(0xFFE91E63),
                contentColor = Color.White,
                edgePadding = 0.dp,
                indicator = {} // Ocultamos la línea de abajo para estilo simple, o déjala default
            ) {
                categories.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedCategory == index,
                        onClick = { viewModel.changeCategory(index) },
                        text = { Text(text = title, color = if (selectedCategory == index) Color.White else Color.LightGray) }
                    )
                }
            }

            // 2. CONTENIDO DE LA LISTA
            ContentHomeView(state, navController)
        }
    }
}

@Composable
fun ContentHomeView(
    state: com.example.apimovies.state.MovieState,
    navController: NavController
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black) // Fondo negro para que resalten las pelis
    ) {
        if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = Color.White)
        } else if (state.error != null) {
            Text(text = state.error ?: "Error", modifier = Modifier.align(Alignment.Center), color = Color.White)
        } else {
            LazyColumn(contentPadding = PaddingValues(10.dp)) {
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