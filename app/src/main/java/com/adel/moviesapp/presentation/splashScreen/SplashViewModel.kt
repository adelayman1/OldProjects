package com.adel.moviesapp.presentation.splashScreen

import androidx.lifecycle.ViewModel
import com.adel.moviesapp.data.repositories.AuthRepositoryImpl
import com.adel.moviesapp.domain.enities.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    authRepository: AuthRepositoryImpl
) : ViewModel() {
    var isSplash: MutableStateFlow<Result<Boolean>> =
        MutableStateFlow(Result.Loading())

    init {
        isSplash.value = if (authRepository.isUserLoggedIn()) {
            Result.Success(false)
        } else {
            Result.Success(true)
        }
    }
}