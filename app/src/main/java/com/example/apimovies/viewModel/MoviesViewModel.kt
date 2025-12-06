package com.example.apimovies.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apimovies.data.database.entities.MovieEntity
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


    private val _favoriteFilter = MutableStateFlow("all")
    val favoriteFilter: StateFlow<String> = _favoriteFilter.asStateFlow()

    private val _selectedMovie = MutableStateFlow<MovieModel?>(null)
    val selectedMovie: StateFlow<MovieModel?> = _selectedMovie.asStateFlow()

    private val _isCurrentMovieFavorite = MutableStateFlow(false)
    val isCurrentMovieFavorite: StateFlow<Boolean> = _isCurrentMovieFavorite.asStateFlow()
    private var originalMovieList = emptyList<MovieModel>()
    private val dataCache = mutableMapOf<Int, List<MovieModel>>()

    init {
        fetchData(0)
    }

    //CAMBIAR DE PESTAÑA
    fun changeCategory(index: Int) {
        _selectedCategory.value = index
        _searchQuery.value = ""
        fetchData(index)
    }

    //BUSCADOR EN TIEMPO REAL
    fun onSearchChange(query: String) {
        _searchQuery.value = query
        if (query.isEmpty()) {
            _state.value = _state.value.copy(movies = originalMovieList)
        } else {
            val filtered = originalMovieList.filter { movie ->
                movie.title?.contains(query, ignoreCase = true) == true
            }
            _state.value = _state.value.copy(movies = filtered)
        }
    }

    // FILTRO DE FAVORITOS
    fun setFavoriteFilter(filter: String) {
        _favoriteFilter.value = filter
        fetchData(4)
    }

    // LÓGICA CENTRAL DE CARGA DE DATOS
    private fun fetchData(categoryIndex: Int) {
        // FAVORITOS (Índice 4)
        if (categoryIndex == 4) {
            viewModelScope.launch(Dispatchers.IO) {
                repository.favorites.collect { entities ->
                    val models = mapEntityToModel(entities)

                    // Aplicamos el filtro (Todos, Películas o Series)
                    val filteredModels = if (_favoriteFilter.value == "all") {
                        models
                    } else {
                        models.filter { it.type == _favoriteFilter.value }
                    }

                    originalMovieList = filteredModels // Para que el buscador funcione en favoritos también
                    _state.value = _state.value.copy(
                        isLoading = false,
                        movies = filteredModels,
                        error = if (filteredModels.isEmpty()) "No tienes favoritos aquí" else null
                    )
                }
            }
            return
        }

        if (dataCache.containsKey(categoryIndex)) {
            originalMovieList = dataCache[categoryIndex] ?: emptyList()
            _state.value = _state.value.copy(
                isLoading = false,
                movies = originalMovieList,
                error = null
            )
            return
        }

        // Si no está en caché, vamos a internet
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = _state.value.copy(isLoading = true)
            val result = repository.getMediaByCategory(categoryIndex)
            if (result.isNotEmpty()) {
                dataCache[categoryIndex] = result
                originalMovieList = result
                _state.value = _state.value.copy(isLoading = false, movies = result, error = null)
            } else {
                _state.value = _state.value.copy(isLoading = false, error = "Error al cargar datos")
            }
        }
    }

    // Logica de detalle y favoritos
    fun getMovieById(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            //Buscamos en todas las listas que tengamos en memoria
            val movieInCache = dataCache.values.flatten().find { it.id == id }

            if (movieInCache != null) {
                _selectedMovie.value = movieInCache
            } else {
                // Si no está en memoria, la buscamos en la base de datos local
                val favMovie = repository.getMovieFromDB(id)
                _selectedMovie.value = favMovie
            }
            // Verificamos si es favorita para pintar el corazón
            checkFavorite(id)
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

    // Helper para convertir de DB a UI
    private fun mapEntityToModel(entities: List<MovieEntity>): List<MovieModel> {
        return entities.map { entity ->
            MovieModel(
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
}