package com.example.apimovies.repository

import android.util.Log
import com.example.apimovies.data.MovieApi
import com.example.apimovies.data.database.MovieDao
import com.example.apimovies.data.database.entities.MovieEntity
import com.example.apimovies.model.MovieModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Repositorio central de la aplicación
 * Esta clase actúa como intermediario entre las fuentes de datos (API Remota y Base de Datos Local)
 * y la lógica de negocio. Se encarga de decidir de dónde obtener la información
 * y de transformar los datos si es necesario.
 */
class MovieRepository @Inject constructor(
    private val api: MovieApi,       // Fuente de datos remota (Internet)
    private val movieDao: MovieDao   // Fuente de datos local (Base de Datos)
) {

    // --- SECCIÓN DE DATOS REMOTOS (API) ---

    /**
     * Obtiene una lista de películas o series desde Internet según la categoría seleccionada.
     *
     * @param categoryIndex El índice de la pestaña (0: Top Pelis, 1: Populares, etc.).
     * @return Una lista de objetos MovieModel. Si falla, retorna una lista vacía para no romper la app.
     * Incluye manejo de excepciones para capturar errores de red (404, sin internet, etc.).
     */
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
            return emptyList()
        }

        if (response.isSuccessful) {
            return response.body() ?: emptyList()
        } else {
            return emptyList()
        }
    }

    // SECCIÓN DE DATOS LOCALES

    /**
     * Flujo de datos en tiempo real de las películas favoritas.
     */
    val favorites: Flow<List<MovieEntity>> = movieDao.getFavorites()

    /**
     * Guarda una película en la base de datos local.
     * Realiza un mapeo (conversión) de 'MovieModel' (usado en la UI) a 'MovieEntity' (usado en la BD).
     */
    suspend fun addFavorite(movie: MovieModel) {
        val entity = MovieEntity(
            id = movie.id,
            title = movie.title ?: "Sin Título",
            image = movie.primaryImage ?: "",
            rating = movie.rating ?: 0.0,
            year = movie.year ?: 0,
            description = movie.description ?: "",
            type = movie.type ?: "movie"
        )
        movieDao.insert(entity)
    }

    /**
     * Elimina una película de la base de datos local.
     * También requiere convertir el modelo a entidad para que Room sepa qué borrar.
     */
    suspend fun removeFavorite(movie: MovieModel) {
        val entity = MovieEntity(
            id = movie.id,
            title = movie.title ?: "",
            image = movie.primaryImage ?: "",
            rating = movie.rating ?: 0.0,
            year = movie.year ?: 0,
            description = movie.description ?: "",
            type = movie.type ?: "movie"
        )
        movieDao.delete(entity)
    }

    /**
     * Verifica si una película ya existe en favoritos buscando por su ID.
     * Retorna true si existe, false si no.
     */
    suspend fun isFavorite(id: String): Boolean {
        return movieDao.checkFavorite(id)
    }

    /**
     * Recupera una película específica de la base de datos local y la convierte
     * al modelo de dominio (MovieModel) para que pueda ser mostrada en el detalle.
     * Útil para ver detalles de favoritos cuando no hay conexión a internet.
     */
    suspend fun getMovieFromDB(id: String): MovieModel? {
        val entity = movieDao.getMovieById(id) ?: return null
        return MovieModel(
            id = entity.id,
            title = entity.title,
            primaryImage = entity.image,
            rating = entity.rating,
            year = entity.year,
            description = entity.description,
            type = entity.type,
            originalTitle = ""
        )
    }
}