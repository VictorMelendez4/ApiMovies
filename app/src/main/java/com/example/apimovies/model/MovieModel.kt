package com.example.apimovies.model

import com.google.gson.annotations.SerializedName

/**
 * Modelo de Datos (Data Class) para la API.
 *
 * Esta clase define la estructura exacta de cómo recibimos la información de las películas
 * desde el servidor (RapidAPI)
 */
data class MovieModel(
    // ID único de la película en la base de datos de IMDb
    @SerializedName("id")
    val id: String,

    // Título principal para mostrar en la UI
    @SerializedName("primaryTitle")
    val title: String,

    // Título original
    @SerializedName("originalTitle")
    val originalTitle: String,

    // Año de estreno
    @SerializedName("startYear")
    val year: Int?,

    // Sinopsis o trama de la película
    @SerializedName("description")
    val description: String?,

    // URL de la imagen del póster
    @SerializedName("primaryImage")
    val primaryImage: String?,

    // Calificación promedio
    @SerializedName("averageRating")
    val rating: Double?,

    // Tipo de contenido ("movie" o "tvSeries")
    // Tiene un valor por defecto "movie" por si la API no envía este campo
    @SerializedName("type")
    val type: String? = "movie"
)