package com.example.apimovies.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apimovies.model.MovieModel
import com.example.apimovies.repository.MovieRepository
import com.example.apimovies.state.MovieState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MoviesViewModel @Inject constructor(
    private val repository: MovieRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MovieState())
    val state: StateFlow<MovieState> = _state.asStateFlow()

    private val _selectedCategory = MutableStateFlow(0)
    val selectedCategory: StateFlow<Int> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // CACHÉ: Aquí guardamos las listas que ya descargamos
    // Mapa: Indice de Categoría -> Lista de Películas
    private val dataCache = mutableMapOf<Int, List<MovieModel>>()

    init {
        fetchData(0)
    }

    fun changeCategory(index: Int) {
        _selectedCategory.value = index
        _searchQuery.value = ""
        fetchData(index)
    }

    fun onSearchChange(query: String) {
        _searchQuery.value = query
        // Buscamos sobre la lista actual de la categoría seleccionada
        val currentList = dataCache[_selectedCategory.value] ?: emptyList()

        if (query.isEmpty()) {
            _state.value = _state.value.copy(movies = currentList)
        } else {
            val filtered = currentList.filter { movie ->
                movie.title?.contains(query, ignoreCase = true) == true
            }
            _state.value = _state.value.copy(movies = filtered)
        }
    }

    private fun fetchData(categoryIndex: Int) {
        // 1. PRIMERO REVISAMOS SI YA TENEMOS LOS DATOS EN CACHÉ
        if (dataCache.containsKey(categoryIndex)) {
            // ¡Sí los tenemos! Los usamos directamente sin llamar a internet
            _state.value = _state.value.copy(
                isLoading = false,
                movies = dataCache[categoryIndex] ?: emptyList(),
                error = null
            )
            return // Salimos de la función, no gastamos saldo
        }

        // 2. Si no están en caché, entonces sí vamos a internet
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = _state.value.copy(isLoading = true)

            val result = repository.getMediaByCategory(categoryIndex)

            if (result.isNotEmpty()) {
                // ¡Éxito! Guardamos en caché para la próxima
                dataCache[categoryIndex] = result

                _state.value = _state.value.copy(
                    isLoading = false,
                    movies = result,
                    error = null
                )
            } else {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = "Error al cargar o límite excedido (429)"
                )
            }
        }
    }
}