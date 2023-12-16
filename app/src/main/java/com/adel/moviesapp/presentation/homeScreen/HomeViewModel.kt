package com.adel.moviesapp.presentation.homeScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adel.moviesapp.data.model.MovieModel
import com.adel.moviesapp.domain.enities.Result
import com.adel.moviesapp.domain.usecases.GetAllMoviesUseCase
import com.adel.moviesapp.domain.usecases.GetPopularMoviesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    var getAllMoviesUseCase: GetAllMoviesUseCase,
    var getPopularMoviesUseCase: GetPopularMoviesUseCase
) : ViewModel() {

    var allMovies: MutableStateFlow<Result<List<MovieModel>>> =
        MutableStateFlow(Result.Loading<List<MovieModel>>())
    var popularMovies: MutableStateFlow<Result<List<MovieModel>>> =
        MutableStateFlow(Result.Loading<List<MovieModel>>())

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val allMoviesData = getAllMoviesUseCase.invoke()
            allMovies.emit(allMoviesData)

            val popularMoviesData = getPopularMoviesUseCase.invoke()
            popularMovies.emit(popularMoviesData)
        }
    }
}