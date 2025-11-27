package com.example.apimovies.repository

import com.example.apimovies.data.MovieApi
import com.example.apimovies.model.MovieModel
import javax.inject.Inject

class MovieRepository @Inject constructor(private val api: MovieApi) {

    suspend fun getMediaByCategory(categoryIndex: Int): List<MovieModel> {
        val response = try {
            when (categoryIndex) {
                0 -> api.getTop250Movies()
                1 -> api.getPopularMovies()
                2 -> api.getTop250TV()
                3 -> api.getPopularTV()
                else -> api.getTop250Movies()
            }
        } catch (e: Exception) {
            return emptyList()
        }

        return if (response.isSuccessful) {
            response.body() ?: emptyList()
        } else {
            emptyList()
        }
    }
}