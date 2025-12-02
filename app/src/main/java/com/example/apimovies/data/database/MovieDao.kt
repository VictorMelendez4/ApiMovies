package com.example.apimovies.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.apimovies.data.database.entities.MovieEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {
    // Obtener todas las favoritas (Flow permite ver cambios en tiempo real)
    @Query("SELECT * FROM movies_table")
    fun getFavorites(): Flow<List<MovieEntity>>

    // Verificar si una película ya es favorita (devuelve 1 si existe, 0 si no)
    @Query("SELECT EXISTS (SELECT 1 FROM movies_table WHERE id = :id)")
    suspend fun checkFavorite(id: String): Boolean

    // Guardar (Si ya existe, la reemplaza)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(movie: MovieEntity)

    // Borrar
    @Delete
    suspend fun delete(movie: MovieEntity)
}