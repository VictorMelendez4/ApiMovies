package com.example.apimovies.repository

import android.util.Log
import com.example.apimovies.data.MovieApi
import com.example.apimovies.data.database.MovieDao
import com.example.apimovies.data.database.entities.MovieEntity
import com.example.apimovies.model.MovieModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

// Inyectamos TAMBIÉN el MovieDao aquí
class MovieRepository @Inject constructor(
    private val api: MovieApi,
    private val movieDao: MovieDao
) {

    // --- PARTE DE API (INTERNET) ---

    suspend fun getMediaByCategory(categoryIndex: Int): List<MovieModel> {
        // (Este código se queda igual que antes)
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
            return emptyList()
        }

        if (response.isSuccessful) {
            return response.body() ?: emptyList()
        } else {
            return emptyList()
        }
    }

    // --- PARTE DE BASE DE DATOS (FAVORITOS) ---

    // Obtener la lista de favoritos (reactiva)
    val favorites: Flow<List<MovieEntity>> = movieDao.getFavorites()

    // Guardar película en favoritos
    suspend fun addFavorite(movie: MovieModel) {
        val entity = MovieEntity(
            id = movie.id,
            title = movie.title ?: "Sin Título",
            image = movie.primaryImage ?: "",
            rating = movie.rating ?: 0.0,
            year = movie.year ?: 0,
            description = movie.description ?: ""
        )
        movieDao.insert(entity)
    }

    // Quitar película de favoritos
    suspend fun removeFavorite(movie: MovieModel) {
        val entity = MovieEntity(
            id = movie.id,
            title = movie.title ?: "",
            image = movie.primaryImage ?: "",
            rating = movie.rating ?: 0.0,
            year = movie.year ?: 0,
            description = movie.description ?: ""
        )
        movieDao.delete(entity)
    }

    // Verificar si ya es favorita
    suspend fun isFavorite(id: String): Boolean {
        return movieDao.checkFavorite(id)
    }
}