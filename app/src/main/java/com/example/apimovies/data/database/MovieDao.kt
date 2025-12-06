package com.example.apimovies.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.apimovies.data.database.entities.MovieEntity
import kotlinx.coroutines.flow.Flow

/**
 * Interfaz de Acceso a Datos (DAO).
 * Define los métodos abstractos para interactuar con la base de datos.
 * Room se encarga de generar el código SQL correspondiente en tiempo de compilación.
 */
@Dao
interface MovieDao {

    // --- SECCIÓN DE CONSULTAS (LECTURA) ---

    /**
     * Obtiene la lista completa de películas marcadas como favoritas.
     * Retorna un 'Flow', lo que permite observar los cambios en la tabla
     * en tiempo real (reactivo) y actualizar la UI automáticamente.
     */
    @Query("SELECT * FROM movies_table")
    fun getFavorites(): Flow<List<MovieEntity>>

    /**
     * Verifica de forma eficiente si una película ya existe en la base de datos.
     * Utiliza una subconsulta 'EXISTS' para retornar un booleano (true/false)
     * sin necesidad de cargar todos los datos del objeto.
     */
    @Query("SELECT EXISTS (SELECT 1 FROM movies_table WHERE id = :id)")
    suspend fun checkFavorite(id: String): Boolean

    /**
     * Recupera una película específica basada en su ID.
     * Útil para cargar los detalles desde la memoria local sin internet.
     * Puede retornar null si la película no se encuentra.
     */
    @Query("SELECT * FROM movies_table WHERE id = :id LIMIT 1")
    suspend fun getMovieById(id: String): MovieEntity?

    // --- SECCIÓN DE OPERACIONES (ESCRITURA) ---

    /**
     * Inserta una nueva película en la base de datos.
     * La estrategia 'REPLACE' indica que si ya existe una película con el mismo ID,
     * se sobrescribirán sus datos (actualización).
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(movie: MovieEntity)

    /**
     * Elimina el registro de una película específica de la tabla.
     */
    @Delete
    suspend fun delete(movie: MovieEntity)
}