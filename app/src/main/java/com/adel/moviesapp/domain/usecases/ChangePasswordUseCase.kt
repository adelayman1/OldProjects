package com.adel.moviesapp.domain.usecases

import android.content.SharedPreferences
import com.adel.moviesapp.data.repositories.AuthRepositoryImpl
import com.adel.moviesapp.data.utilities.ErrorHandlerImpl
import com.adel.moviesapp.domain.enities.ErrorEntity
import com.adel.moviesapp.domain.enities.Result
import org.json.JSONObject
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChangePasswordUseCase @Inject constructor(
    var authRepository: AuthRepositoryImpl,
    var errorHandler: ErrorHandlerImpl,
    var sharedPreferences: SharedPreferences
) {
    suspend operator fun invoke(
        email: String,
        newPassword: String,
        oldPassword: String
    ): Result<Boolean> {
        if (!authRepository.isUserLoggedIn())
            return Result.Error(ErrorEntity.UserIsNotExist)
        val getDataByEmailResult = authRepository.getDataByEmailAndPass(email, oldPassword)
        return try {
            if (getDataByEmailResult.isSuccessful) {
                val passwordMap: HashMap<String, String> = hashMapOf()
                passwordMap.put("password", newPassword)
                passwordMap.put("idToken", authRepository.getLocalUserTOKEN())

                val changePasswordResult = authRepository.changePassword(passwordMap);
                if (changePasswordResult.isSuccessful) {
                    authRepository.changeLocalUserTOKEN(changePasswordResult.body()!!.token)
                    Result.Success(true)
                } else {
                    // get error body as json
                    val errorBody =
                        JSONObject(
                            changePasswordResult.errorBody()!!.string()
                        ).getJSONObject("error")
                    // get error message
                    val errorMessage = errorBody.getString("message")
                    Result.Error(errorHandler.getErrorFromMessage(errorMessage))
                }

            } else {
                // get error body as json
                val errorBody =
                    JSONObject(getDataByEmailResult.errorBody()!!.string()).getJSONObject("error")
                // get error message
                val errorMessage = errorBody.getString("message")
                Result.Error(errorHandler.getErrorFromMessage(errorMessage))
            }
        } catch (e: IOException) {
            Result.Error(ErrorEntity.Network)
        }
    }
}