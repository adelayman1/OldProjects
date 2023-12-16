package com.adel.moviesapp.domain.usecases

import android.content.SharedPreferences
import com.adel.moviesapp.data.repositories.AuthRepositoryImpl
import com.adel.moviesapp.data.utilities.ErrorHandlerImpl
import com.adel.moviesapp.domain.enities.ErrorEntity
import com.adel.moviesapp.domain.enities.Result
import com.adel.moviesapp.domain.utilities.extensions.isEmailValid
import com.adel.moviesapp.domain.utilities.extensions.isPasswordValid
import org.json.JSONObject
import java.io.IOException
import javax.inject.Inject

class LoginUseCase @Inject constructor(
        var authRepository: AuthRepositoryImpl,
        var errorHandler: ErrorHandlerImpl,
        var sharedPreferences: SharedPreferences
) {
    suspend operator fun invoke(email: String, password: String): Result<Boolean> {
        if (authRepository.isUserLoggedIn())
            return Result.Error(ErrorEntity.UserIsExist)

        if (!email.isEmailValid()) {
            return Result.Error(ErrorEntity.EmailIsNotValid)
        }
        if (!password.isPasswordValid()) {
            return Result.Error(ErrorEntity.PasswordIsNotValid)
        }
        return try {
            val loginResult = authRepository.getDataByEmailAndPass(email, password)
            if (loginResult.isSuccessful && loginResult.code() == 200) {
                authRepository.changeLocalUserTOKEN(loginResult.body()!!.token)
                authRepository.changeLocalUserUID(loginResult.body()!!.uid)
                Result.Success(true)
            }else {
                // get error body as json
                val errorBody =
                    JSONObject(loginResult.errorBody()!!.string()).getJSONObject("error")
                // get error message
                val errorMessage = errorBody.getString("message")
                Result.Error(errorHandler.getErrorFromMessage(errorMessage))
            }
        } catch (e: IOException) {
            Result.Error(ErrorEntity.Network)
        }
    }
}