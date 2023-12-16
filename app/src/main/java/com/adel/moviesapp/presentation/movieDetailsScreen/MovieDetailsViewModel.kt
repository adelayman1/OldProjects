package com.adel.moviesapp.presentation.movieDetailsScreen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adel.moviesapp.data.model.MovieModel
import com.adel.moviesapp.data.repositories.MovieRepositoryImpl
import com.adel.moviesapp.domain.enities.Result
import com.adel.moviesapp.domain.usecases.IsMovieSavedUseCase
import com.adel.moviesapp.domain.usecases.SaveMovieUseCase
import com.adel.moviesapp.domain.usecases.UnSaveMovieUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieDetailsViewModel @Inject constructor(
    var movieRepository: MovieRepositoryImpl,
    var savedStateHandle: SavedStateHandle,
    isMovieSavedUseCase: IsMovieSavedUseCase,
    var saveMovieUseCase: SaveMovieUseCase,
    var unSaveMovieUseCase: UnSaveMovieUseCase,
) : ViewModel() {
    var isMovieSaved: MutableStateFlow<Result<Boolean>> =
        MutableStateFlow(Result.Loading())

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val isMovieSavedResult =
                isMovieSavedUseCase.invoke(savedStateHandle.get<MovieModel>("movieModel")!!.key)
            isMovieSaved.emit(isMovieSavedResult)
        }
    }

    fun saveMovie(moveID: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val saveMovieResult = saveMovieUseCase.invoke(moveID)
            isMovieSaved.emit(saveMovieResult)
        }
    }

    fun unSaveMovie(moveID: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val unSaveMovieResult = unSaveMovieUseCase.invoke(moveID)
            isMovieSaved.emit(unSaveMovieResult)
        }
    }
}