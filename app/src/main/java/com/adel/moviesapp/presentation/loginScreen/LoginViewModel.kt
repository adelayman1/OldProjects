package com.adel.moviesapp.presentation.loginScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adel.moviesapp.domain.enities.Result
import com.adel.moviesapp.domain.usecases.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(var loginUseCase: LoginUseCase) : ViewModel() {

    var isLoginSuccess: MutableStateFlow<Result<Boolean>> =
        MutableStateFlow(Result.Loading<Boolean>())

    fun login(email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            isLoginSuccess.emit(Result.Loading())
            val loginData = loginUseCase.invoke(email, password)
            isLoginSuccess.emit(loginData)
        }
    }
}