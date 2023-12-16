package com.adel.moviesapp.domain.usecases

import com.adel.moviesapp.data.model.UserModel
import com.adel.moviesapp.data.repositories.AuthRepositoryImpl
import com.adel.moviesapp.data.repositories.UserRepositoryImpl
import com.adel.moviesapp.data.utilities.ErrorHandlerImpl
import com.adel.moviesapp.domain.enities.ErrorEntity
import com.adel.moviesapp.domain.enities.Result
import org.json.JSONObject
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetUserDataUseCase @Inject constructor(var userRepository: UserRepositoryImpl, var authRepository: AuthRepositoryImpl, var errorHandler: ErrorHandlerImpl) {
    suspend operator fun invoke(): Result<UserModel> {
        if(!authRepository.isUserLoggedIn())
            return Result.Error(ErrorEntity.UserIsNotExist)
        return try {
            var getUserDataResult = userRepository.getUser(authRepository.getLocalUserUID())
               if(getUserDataResult.isSuccessful)
                   Result.Success(getUserDataResult.body()!!)
               else {
                   // get error body as json
                   val errorBody =
                       JSONObject(getUserDataResult.errorBody()!!.string()).getJSONObject("error")
                   // get error message
                   val errorMessage = errorBody.getString("message")
                   Result.Error(errorHandler.getErrorFromMessage(errorMessage))
               }
        } catch (e: IOException) {
            Result.Error(ErrorEntity.Network)
        }

    }
}