package com.example.apimovies.state

import com.example.apimovies.model.MovieModel

/**
 * Esta clase de datos contiene todas las variables necesarias para "dibujar" la pantalla.
 * La vista (View) observa este estado: si cambia, la pantalla se redibuja automáticamente.
 */
data class MovieState(
    // Indica si se está realizando una operación de carga (para mostrar el Shimmer).
    val isLoading: Boolean = false,

    // La lista de películas a mostrar. Inicialmente vacía.
    val movies: List<MovieModel> = emptyList(),

    // Mensaje de error en caso de fallo. Si es null, no hay error.
    val error: String? = null
)