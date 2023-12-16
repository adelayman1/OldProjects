package com.adel.moviesapp.presentation.signUpScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adel.moviesapp.domain.enities.Result
import com.adel.moviesapp.domain.usecases.SignUpUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    var signUpUseCase: SignUpUseCase
) : ViewModel() {

    var isSignUpSuccess: MutableStateFlow<Result<Boolean>> =
            MutableStateFlow(Result.Loading())

    fun signUp(name: String, email: String, password: String) {
            viewModelScope.launch(Dispatchers.IO) {

               val signUpResult = signUpUseCase.invoke(name, email, password)
                isSignUpSuccess.emit(signUpResult)
            }
    }
}