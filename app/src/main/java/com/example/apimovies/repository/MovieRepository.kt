package com.example.apimovies.repository

import android.util.Log
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
            Log.e("REPO_ERROR", "Error de red: ${e.message}")
            e.printStackTrace()
            return emptyList()
        }

        if (response.isSuccessful) {
            val body = response.body()
            if (body == null) {
                Log.e("REPO_ERROR", "Respuesta exitosa pero cuerpo vacío")
            } else {
                Log.d("REPO_EXITO", "Se recibieron ${body.size} elementos")
            }
            return body ?: emptyList()
        } else {
            Log.e("REPO_ERROR", "Error API: Código ${response.code()} - ${response.message()}")
            return emptyList()
        }
    }
}