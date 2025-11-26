package com.example.apimovies.repository

import com.example.apimovies.data.MovieApi
import com.example.apimovies.model.MovieModel
import javax.inject.Inject

class MovieRepository @Inject constructor(private val api: MovieApi) {

    suspend fun getPopularMovies(): List<MovieModel> {
        return try {
            val response = api.getPopularMovies()
            if (response.isSuccessful) {
                response.body() ?: emptyList()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}