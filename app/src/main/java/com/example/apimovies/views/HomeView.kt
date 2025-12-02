package com.example.apimovies.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeView(viewModel: MoviesViewModel = hiltViewModel(), navController: NavController) {
    val state by viewModel.state.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    val categories = listOf(
        "Top Películas",
        "Pelis Populares",
        "Top Series",
        "Series Populares",
        "Favoritos"
    )

    val pagerState = rememberPagerState(pageCount = { categories.size })
    val scope = rememberCoroutineScope()

    LaunchedEffect(pagerState.currentPage) {
        viewModel.changeCategory(pagerState.currentPage)
    }

    Scaffold(
        containerColor = PrimaryDark,
        topBar = {
            Column(
                modifier = Modifier
                    .background(PrimaryDark)
                    .padding(top = 40.dp, bottom = 10.dp)
            ) {
                Text(
                    text = "ApiMovies",
                    color = HighlightRed,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
                )

                SearchBar(
                    query = searchQuery,
                    onSearchChange = { viewModel.onSearchChange(it) }
                )

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
                    divider = {}
                ) {
                    categories.forEachIndexed { index, title ->
                        Tab(
                            selected = pagerState.currentPage == index,
                            onClick = {
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

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            ContentHomeView(state, navController, viewModel, pagerState.currentPage)
        }
    }
}

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
            ShimmerLoadingAnimation()
        } else if (state.error != null) {
            // El error puede ser "No tienes favoritos", y se verá bien aquí
            ErrorView(
                message = state.error ?: "Error desconocido",
                onRetry = { viewModel.changeCategory(currentIndex) }
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(top = 10.dp, bottom = 10.dp)
            ) {
                items(state.movies) { movie ->
                    MovieCard(movie = movie) {
                        val id = movie.id.ifBlank { "no-id" }
                        val title = movie.title ?: "Sin Título"
                        val description = if (movie.description.isNullOrBlank()) "Descripción no disponible" else movie.description
                        val image = if (movie.primaryImage.isNullOrBlank()) "" else movie.primaryImage
                        val rating = movie.rating?.toFloat() ?: 0.0f
                        val year = movie.year ?: 0

                        val encodedUrl = URLEncoder.encode(image, StandardCharsets.UTF_8.toString())
                        val encodedDesc = URLEncoder.encode(description, StandardCharsets.UTF_8.toString())
                        val encodedTitle = URLEncoder.encode(title, StandardCharsets.UTF_8.toString())

                        navController.navigate("Detail/$id/$encodedTitle/$encodedUrl/$encodedDesc/$rating/$year")
                    }
                }
            }
        }
    }
}