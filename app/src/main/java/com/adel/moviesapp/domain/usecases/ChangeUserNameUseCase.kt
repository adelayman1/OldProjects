package com.adel.moviesapp.domain.usecases

import com.adel.moviesapp.data.repositories.AuthRepositoryImpl
import com.adel.moviesapp.data.repositories.UserRepositoryImpl
import com.adel.moviesapp.data.utilities.ErrorHandlerImpl
import com.adel.moviesapp.domain.enities.ErrorEntity
import com.adel.moviesapp.domain.enities.Result
import org.json.JSONObject
import java.io.IOException
import javax.inject.Inject

class ChangeUserNameUseCase @Inject constructor(
    var userRepository: UserRepositoryImpl,
    var authRepository: AuthRepositoryImpl,
    var errorHandler: ErrorHandlerImpl
) {
    suspend operator fun invoke(name: String): Result<Boolean> {
        if (!authRepository.isUserLoggedIn())
            return Result.Error(ErrorEntity.UserIsNotExist)
        val map: HashMap<String, String> = hashMapOf()
        map.put("name", name)
        return try {
            val updateUserNameResult =
                userRepository.updateUserName(authRepository.getLocalUserUID(), map)
            if(updateUserNameResult.isSuccessful){
                Result.Success(true)
            }else {
                // get error body as json
                val errorBody =
                    JSONObject(updateUserNameResult.errorBody()!!.string()).getJSONObject("error")
                // get error message
                val errorMessage = errorBody.getString("message")
                Result.Error(errorHandler.getErrorFromMessage(errorMessage))
            }
        } catch (e: IOException) {
            Result.Error(ErrorEntity.Network)
        }
    }
}