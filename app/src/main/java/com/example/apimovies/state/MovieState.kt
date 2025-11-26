package com.example.apimovies.state

import com.example.apimovies.model.MovieModel

data class MovieState(
    val isLoading: Boolean = false,
    val movies: List<MovieModel> = emptyList(),
    val error: String? = null
)