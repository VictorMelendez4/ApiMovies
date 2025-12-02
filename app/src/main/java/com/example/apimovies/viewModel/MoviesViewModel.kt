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

    // Estados existentes
    private val _state = MutableStateFlow(MovieState())
    val state: StateFlow<MovieState> = _state.asStateFlow()

    private val _selectedCategory = MutableStateFlow(0)
    val selectedCategory: StateFlow<Int> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private var originalMovieList = emptyList<MovieModel>()
    private val dataCache = mutableMapOf<Int, List<MovieModel>>()

    // NUEVO: Estado para saber si la película actual es favorita
    private val _isCurrentMovieFavorite = MutableStateFlow(false)
    val isCurrentMovieFavorite: StateFlow<Boolean> = _isCurrentMovieFavorite.asStateFlow()

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
        // CASO ESPECIAL: FAVORITOS (Índice 4)
        if (categoryIndex == 4) {
            viewModelScope.launch(Dispatchers.IO) {
                // Escuchamos la base de datos en tiempo real
                repository.favorites.collect { entities ->
                    val models = mapEntityToModel(entities)
                    _state.value = _state.value.copy(
                        isLoading = false,
                        movies = models,
                        error = if (models.isEmpty()) "No tienes favoritos aún" else null
                    )
                }
            }
            return // Salimos de la función para no llamar a la API
        }

        // CASO NORMAL: API (Índices 0, 1, 2, 3)
        if (dataCache.containsKey(categoryIndex)) {
            _state.value = _state.value.copy(
                isLoading = false,
                movies = dataCache[categoryIndex] ?: emptyList(),
                error = null
            )
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            _state.value = _state.value.copy(isLoading = true)
            val result = repository.getMediaByCategory(categoryIndex)
            if (result.isNotEmpty()) {
                dataCache[categoryIndex] = result
                originalMovieList = result
                _state.value = _state.value.copy(isLoading = false, movies = result, error = null)
            } else {
                _state.value = _state.value.copy(isLoading = false, error = "Error al cargar")
            }
        }
    }




    fun checkFavorite(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val exists = repository.isFavorite(id)
            _isCurrentMovieFavorite.value = exists
        }
    }


    fun toggleFavorite(movie: MovieModel) {
        viewModelScope.launch(Dispatchers.IO) {
            if (_isCurrentMovieFavorite.value) {
                repository.removeFavorite(movie)
                _isCurrentMovieFavorite.value = false
            } else {
                repository.addFavorite(movie)
                _isCurrentMovieFavorite.value = true
            }
        }

    }

    // Función auxiliar para convertir de Base de Datos a Modelo de UI
    private fun mapEntityToModel(entities: List<com.example.apimovies.data.database.entities.MovieEntity>): List<MovieModel> {
        return entities.map { entity ->
            MovieModel(
                id = entity.id,
                title = entity.title,
                primaryImage = entity.image,
                rating = entity.rating,
                year = entity.year,
                description = entity.description,
                originalTitle = ""
            )
        }
    }
}