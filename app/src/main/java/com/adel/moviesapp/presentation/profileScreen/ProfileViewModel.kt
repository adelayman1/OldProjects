package com.adel.moviesapp.presentation.profileScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adel.moviesapp.domain.enities.ErrorEntity
import com.adel.moviesapp.domain.enities.Result
import com.adel.moviesapp.domain.usecases.EditUserDataUseCase
import com.adel.moviesapp.domain.usecases.GetUserDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    getUserDataUseCase: GetUserDataUseCase,
    var editUserDataUseCase: EditUserDataUseCase
) :
    ViewModel() {
    var userName: MutableStateFlow<Result<String>> = MutableStateFlow(Result.Loading<String>())
    var isDataUpdatedSuccessful: MutableStateFlow<Result<Boolean>> = MutableStateFlow(Result.Loading<Boolean>())
    var userEmail: String? = null

    init {
        viewModelScope.launch {
               val getUserDataResult = getUserDataUseCase.invoke()
                when(getUserDataResult){
                    is Result.Success -> {
                        userName.emit(Result.Success(getUserDataResult.data.name))
                        userEmail = getUserDataResult.data.email
                    }
                    is Result.Error -> userName.emit(Result.Error(getUserDataResult.error))
                }
        }
    }

    fun update(name: String?, oldPassword: String?, newPassword: String?) {
        viewModelScope.launch {
            when (userName.value) {
                is Result.Success -> {
                    val userName: String = (userName.value as Result.Success).data

                    val editUserDataResult = editUserDataUseCase.invoke(
                        name,
                        oldPassword,
                        newPassword,
                        userName,
                        userEmail!!
                    )
                    isDataUpdatedSuccessful.emit(editUserDataResult)


                }
                is Result.Error -> isDataUpdatedSuccessful.emit(Result.Error(ErrorEntity.Unknown))
            }
        }
    }
}