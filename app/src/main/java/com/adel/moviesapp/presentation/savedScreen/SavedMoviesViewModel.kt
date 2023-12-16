package com.adel.moviesapp.presentation.savedScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adel.moviesapp.data.model.MovieModel
import com.adel.moviesapp.domain.enities.Result
import com.adel.moviesapp.domain.usecases.GetSavedMoviesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SavedMoviesViewModel @Inject constructor(getSavedMoviesUseCase: GetSavedMoviesUseCase) :
    ViewModel() {
    var savedMovies: MutableStateFlow<Result<List<MovieModel>>> =
        MutableStateFlow(Result.Loading<List<MovieModel>>())

    init {
        viewModelScope.launch {
            val savedMoviesResult = getSavedMoviesUseCase.invoke()
            savedMovies.emit(savedMoviesResult)
        }
    }
}