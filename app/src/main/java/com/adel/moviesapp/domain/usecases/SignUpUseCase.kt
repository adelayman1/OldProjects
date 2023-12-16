package com.adel.moviesapp.domain.usecases

import android.content.SharedPreferences
import com.adel.moviesapp.data.model.UserModel
import com.adel.moviesapp.data.repositories.AuthRepositoryImpl
import com.adel.moviesapp.data.repositories.UserRepositoryImpl
import com.adel.moviesapp.data.utilities.ErrorHandlerImpl
import com.adel.moviesapp.domain.enities.ErrorEntity
import com.adel.moviesapp.domain.enities.Result
import com.adel.moviesapp.domain.utilities.extensions.isEmailValid
import com.adel.moviesapp.domain.utilities.extensions.isPasswordValid
import org.json.JSONObject
import java.io.IOException
import javax.inject.Inject

class SignUpUseCase @Inject constructor(
    var authRepository: AuthRepositoryImpl,
    var userRepository: UserRepositoryImpl,
    var errorHandler: ErrorHandlerImpl,
    var sharedPreferences: SharedPreferences
) {
    suspend operator fun invoke(name: String, email: String, password: String): Result<Boolean> {
        if (authRepository.isUserLoggedIn())
            return Result.Error(ErrorEntity.UserIsExist)

        if (!email.isEmailValid()) {
            return Result.Error(ErrorEntity.EmailIsNotValid)
        }
        if (!password.isPasswordValid()) {
            return Result.Error(ErrorEntity.PasswordIsNotValid)
        }
        return try {
            val createAccountResult = authRepository.createNewAccount(email, password)
            if (createAccountResult.isSuccessful && createAccountResult.code() == 200) {
                val uid = createAccountResult.body()!!.uid
                val token = createAccountResult.body()!!.token
                val user: UserModel = UserModel(name, uid, email)
                val uploadUserResult = userRepository.uploadUser(uid, user)
                if (uploadUserResult.isSuccessful) {
                    authRepository.changeLocalUserTOKEN(token)
                    authRepository.changeLocalUserUID(uid)
                    Result.Success(true)
                } else
                    Result.Error(ErrorEntity.Unknown)
            } else {
                // get error body as json
                val errorBody =
                    JSONObject(createAccountResult.errorBody()!!.string()).getJSONObject("error")
                // get error message
                val errorMessage = errorBody.getString("message")
                Result.Error(errorHandler.getErrorFromMessage(errorMessage))
            }
        } catch (e: IOException) {
            Result.Error(ErrorEntity.Network)
        }
    }
}