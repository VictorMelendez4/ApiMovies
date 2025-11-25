package com.example.apimovies.data


import com.example.apimovies.model.MovieModel
import retrofit2.Response
import retrofit2.http.GET

interface MovieApi {
    // Endpoint de RapidAPI para Top Movies.
    // Verifica en RapidAPI si la URL termina en "movies/top250" o similar.
    // Usaremos este ejemplo basado en lo común:
    @GET("movies/top250")
    suspend fun getPopularMovies(): Response<List<MovieModel>>
}