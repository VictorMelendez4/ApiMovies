package com.example.apimovies.data

import com.example.apimovies.model.MovieModel
import retrofit2.Response
import retrofit2.http.GET

interface MovieApi {

    // Top 250 Películas
    @GET("api/imdb/top250-movies")
    suspend fun getTop250Movies(): Response<List<MovieModel>>

    // Películas Populares
    @GET("api/imdb/most-popular-movies")
    suspend fun getPopularMovies(): Response<List<MovieModel>>

    // Top 250 Series
    @GET("api/imdb/top250-tv")
    suspend fun getTop250TV(): Response<List<MovieModel>>

    // Series Populares
    @GET("api/imdb/most-popular-tv")
    suspend fun getPopularTV(): Response<List<MovieModel>>
}