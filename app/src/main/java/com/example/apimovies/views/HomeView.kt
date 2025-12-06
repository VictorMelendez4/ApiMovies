package com.example.apimovies.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.apimovies.components.ErrorView
import com.example.apimovies.components.MovieCard
import com.example.apimovies.components.SearchBar
import com.example.apimovies.components.ShimmerLoadingAnimation
import com.example.apimovies.ui.theme.HighlightRed
import com.example.apimovies.ui.theme.PrimaryDark
import com.example.apimovies.ui.theme.TextWhite
import com.example.apimovies.viewModel.MoviesViewModel
import kotlinx.coroutines.launch

/**
 * Pantalla Principal (Home).
 *
 * Contiene:
 * 1. Barra de búsqueda en tiempo real.
 * 2. Sistema de pestañas sincronizado con un Pager deslizable.
 * 3. Filtros específicos para la sección de Favoritos.
 * 4. Lista de películas con gestión de estados (Carga, Error, Éxito).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeView(viewModel: MoviesViewModel = hiltViewModel(), navController: NavController) {
    val state by viewModel.state.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val favoriteFilter by viewModel.favoriteFilter.collectAsState()

    // Definición de las pestañas disponibles
    val categories = listOf(
        "Top Películas",
        "Pelis Populares",
        "Top Series",
        "Series Populares",
        "Favoritos"
    )

    // Estado del Pager para controlar el deslizamiento horizontal
    val pagerState = rememberPagerState(pageCount = { categories.size })
    // Scope para lanzar animaciones
    val scope = rememberCoroutineScope()

    // SINCRONIZACION
    // Cada vez que cambia la página del Pager, avisamos al ViewModel
    // para que cargue los datos correspondientes a esa categoría.
    LaunchedEffect(pagerState.currentPage) {
        viewModel.changeCategory(pagerState.currentPage)
    }

    // ESTRUCTURA DE UI
    Scaffold(
        containerColor = PrimaryDark,
        topBar = {
            Column(
                modifier = Modifier
                    .background(PrimaryDark)
                    .padding(top = 40.dp, bottom = 10.dp)
            ) {
                // Título de la App
                Text(
                    text = "ApiMovies",
                    color = HighlightRed,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
                )

                // Componente de Búsqueda
                SearchBar(
                    query = searchQuery,
                    onSearchChange = { viewModel.onSearchChange(it) }
                )

                // FILTROS DE FAVORITOS
                // Solo se muestran si estamos en la pestaña 4
                if (pagerState.currentPage == 4) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Chip para mostrar "Todos"
                        FilterChip(
                            selected = favoriteFilter == "all",
                            onClick = { viewModel.setFavoriteFilter("all") },
                            label = { Text("Todos") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = HighlightRed,
                                selectedLabelColor = TextWhite,
                                containerColor = PrimaryDark,
                                labelColor = Color.Gray
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = favoriteFilter == "all",
                                borderColor = if(favoriteFilter == "all") Color.Transparent else Color.Gray
                            )
                        )

                        // Chip para filtrar "Películas"
                        FilterChip(
                            selected = favoriteFilter == "movie",
                            onClick = { viewModel.setFavoriteFilter("movie") },
                            label = { Text("Películas") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = HighlightRed,
                                selectedLabelColor = TextWhite,
                                containerColor = PrimaryDark,
                                labelColor = Color.Gray
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = favoriteFilter == "movie",
                                borderColor = if(favoriteFilter == "movie") Color.Transparent else Color.Gray
                            )
                        )

                        // Chip para filtrar "Series"
                        FilterChip(
                            selected = favoriteFilter == "tvSeries",
                            onClick = { viewModel.setFavoriteFilter("tvSeries") },
                            label = { Text("Series") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = HighlightRed,
                                selectedLabelColor = TextWhite,
                                containerColor = PrimaryDark,
                                labelColor = Color.Gray
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = favoriteFilter == "tvSeries",
                                borderColor = if(favoriteFilter == "tvSeries") Color.Transparent else Color.Gray
                            )
                        )
                    }
                }

                // BARRA DE PESTAÑAS
                ScrollableTabRow(
                    selectedTabIndex = pagerState.currentPage,
                    containerColor = Color.Transparent,
                    contentColor = TextWhite,
                    edgePadding = 16.dp,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                            height = 3.dp,
                            color = HighlightRed
                        )
                    },
                    divider = {} // Elimina la línea divisoria por defecto
                ) {
                    categories.forEachIndexed { index, title ->
                        Tab(
                            selected = pagerState.currentPage == index,
                            onClick = {
                                // Al hacer click en un tab, animamos el scroll hacia esa página
                                scope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            },
                            text = {
                                Text(
                                    text = title,
                                    fontWeight = if (pagerState.currentPage == index) FontWeight.Bold else FontWeight.Normal,
                                    color = if (pagerState.currentPage == index) TextWhite else Color.Gray
                                )
                            }
                        )
                    }
                }
            }
        }
    ) { paddingValues ->

        // CONTENIDO DESLIZABLE
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Renderiza el contenido de la página actual
            ContentHomeView(state, navController, viewModel, pagerState.currentPage)
        }
    }
}

/**
 * Contenido interno de la pantalla (Lista de Películas).
 * Gestiona los estados de UI: Carga, Error y Éxito.
 */
@Composable
fun ContentHomeView(
    state: com.example.apimovies.state.MovieState,
    navController: NavController,
    viewModel: MoviesViewModel,
    currentIndex: Int
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PrimaryDark)
    ) {
        if (state.isLoading) {
            // ESTADO DE CARGA: Muestra el efecto de esqueleto brillante
            ShimmerLoadingAnimation()
        } else if (state.error != null) {
            // ESTADO DE ERROR: Muestra pantalla de reintento
            ErrorView(
                message = state.error ?: "Error desconocido",
                onRetry = { viewModel.changeCategory(currentIndex) }
            )
        } else {
            // ESTADO DE ÉXITO: Muestra la lista de películas
            LazyColumn(
                contentPadding = PaddingValues(top = 10.dp, bottom = 10.dp)
            ) {
                items(state.movies) { movie ->
                    MovieCard(movie = movie) {
                        // Navegación al detalle: Solo pasamos el ID para ser seguros y eficientes
                        val id = movie.id
                        if (id.isNotEmpty()) {
                            navController.navigate("Detail/$id")
                        }
                    }
                }
            }
        }
    }
}