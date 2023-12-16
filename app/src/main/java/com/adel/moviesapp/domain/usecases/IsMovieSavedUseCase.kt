package com.adel.moviesapp.domain.usecases

import android.content.SharedPreferences
import com.adel.moviesapp.data.repositories.AuthRepositoryImpl
import com.adel.moviesapp.data.repositories.MovieRepositoryImpl
import com.adel.moviesapp.data.utilities.ErrorHandlerImpl
import com.adel.moviesapp.domain.enities.ErrorEntity
import com.adel.moviesapp.domain.enities.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IsMovieSavedUseCase @Inject constructor(
    var movieRepository: MovieRepositoryImpl,
    var errorHandler: ErrorHandlerImpl,
    var authRepository: AuthRepositoryImpl,
    var sharedPreferences: SharedPreferences
) {
    suspend operator fun invoke(movieID: String): Result<Boolean> {
        if (!authRepository.isUserLoggedIn())
            return Result.Error(ErrorEntity.UserIsNotExist)
        return withContext(Dispatchers.IO) {
            return@withContext try {
                val savedMoviesResult =
                    movieRepository.getSavedMovies(authRepository.getLocalUserUID())
                if (savedMoviesResult.isSuccessful) {
                    Result.Success(savedMoviesResult.body()!!.keys.contains(movieID))
                }else {
                    // get error body as json
                    val errorBody =
                        JSONObject(savedMoviesResult.errorBody()!!.string()).getJSONObject("error")
                    // get error message
                    val errorMessage = errorBody.getString("message")
                    Result.Error(errorHandler.getErrorFromMessage(errorMessage))
                }
            } catch (e: IOException) {
                Result.Error(ErrorEntity.Network)
            } catch (e: NullPointerException) {
                Result.Success(false)
            }
        }
    }
}