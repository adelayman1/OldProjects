package com.adel.moviesapp.domain.usecases

import com.adel.moviesapp.data.repositories.AuthRepositoryImpl
import com.adel.moviesapp.data.repositories.UserRepositoryImpl
import com.adel.moviesapp.data.utilities.ErrorHandlerImpl
import com.adel.moviesapp.domain.enities.ErrorEntity
import com.adel.moviesapp.domain.enities.Result
import com.adel.moviesapp.domain.utilities.extensions.isPasswordValid
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject

class EditUserDataUseCase @Inject constructor(
    var userRepository: UserRepositoryImpl,
    var authRepository: AuthRepositoryImpl,
    var errorHandler: ErrorHandlerImpl,
    var changeUserNameUseCase: ChangeUserNameUseCase,
    var changePasswordUseCase: ChangePasswordUseCase
) {
    suspend operator fun invoke(
        name: String?,
        oldPassword: String?,
        newPassword: String?,
        userName: String,
        email: String
    ): Result<Boolean> {
        if(!authRepository.isUserLoggedIn())
            return Result.Error(ErrorEntity.UserIsNotExist)
        return if (name != null && name.trim().isNotEmpty() && name != userName) {
          withContext(Dispatchers.IO) {
              return@withContext  try {
                    val changeUserNameResult = changeUserNameUseCase.invoke(name)
                    when (changeUserNameResult) {
                        is Result.Success -> {
                            if ((oldPassword != null && oldPassword.trim() != "") && (newPassword != null && newPassword.trim() != "") && (newPassword.trim() != oldPassword.trim()) && (newPassword.isPasswordValid() && oldPassword.isPasswordValid())) {

                                val changePasswordResult = changePasswordUseCase.invoke(
                                    email,
                                    newPassword,
                                    oldPassword
                                )
                                when (changePasswordResult) {
                                    is Result.Success -> {
                                        Result.Success(true)
                                    }
                                    is Result.Error -> Result.Error(changePasswordResult.error)
                                    else -> Result.Error(ErrorEntity.Unknown)
                                }
                            } else {
                                Result.Success(true)
                            }
                        }
                        is Result.Error -> Result.Error(changeUserNameResult.error)
                        else -> Result.Error(ErrorEntity.Unknown)
                    }
                } catch (e: IOException) {
                    Result.Error(ErrorEntity.Network)
                }
            }
        } else {

            if ((oldPassword != null && oldPassword.trim() != "") && (newPassword != null && newPassword.trim() != "") && (newPassword.trim() != oldPassword.trim()) && (newPassword.isPasswordValid() && oldPassword.isPasswordValid())) {
                try {
                    val changePasswordResult = changePasswordUseCase.invoke(
                        email,
                        newPassword,
                        oldPassword
                    )
                    when (changePasswordResult) {
                        is Result.Success -> {
                            Result.Success(true)
                        }
                        is Result.Error -> Result.Error(changePasswordResult.error)
                        else -> Result.Error(ErrorEntity.Unknown)
                    }
                } catch (e: Throwable) {
                    Result.Error(errorHandler.getErrorFromThrowable(e))
                }
            }else {
                    Result.Success(true)
                }
            }
        }
}
