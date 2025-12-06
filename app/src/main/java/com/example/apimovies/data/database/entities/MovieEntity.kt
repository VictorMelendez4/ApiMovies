package com.example.apimovies.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad que representa la tabla 'movies_table' en la base de datos local (Room).
 * Cada instancia de esta clase es una fila en la tabla.
 */
@Entity(tableName = "movies_table")
data class MovieEntity(
    // ID único de la película. Es la clave primaria
    @PrimaryKey
    @ColumnInfo(name = "id") val id: String,

    // Título de la película o serie
    @ColumnInfo(name = "title") val title: String,

    // URL de la imagen de portada
    @ColumnInfo(name = "image") val image: String,

    // Calificación promedio
    @ColumnInfo(name = "rating") val rating: Double,

    // Año de lanzamiento
    @ColumnInfo(name = "year") val year: Int,

    // Sinopsis o descripción
    @ColumnInfo(name = "description") val description: String,

    // Tipo de contenido ("movie" o "tvSeries") para poder filtrar
    @ColumnInfo(name = "type") val type: String
)