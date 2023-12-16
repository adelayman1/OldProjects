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

class SaveMovieUseCase @Inject constructor(
        var movieRepository: MovieRepositoryImpl,
        var authRepository: AuthRepositoryImpl,
        var errorHandler: ErrorHandlerImpl,
        var sharedPreferences: SharedPreferences
) {
    suspend operator fun invoke(movieID: String): Result<Boolean> {
        if (!authRepository.isUserLoggedIn())
            return Result.Error(ErrorEntity.UserIsNotExist)


            return withContext(Dispatchers.IO) {
                return@withContext try {
                    val movie: HashMap<String, String> = HashMap()
                    movie.put(movieID, "true")
                    val saveMovieResult = movieRepository.saveMovie(authRepository.getLocalUserUID(),movie)
                    if (saveMovieResult.isSuccessful && saveMovieResult.code() == 200)
                        Result.Success(true)
                    else {
                        // get error body as json
                        val errorBody =
                            JSONObject(saveMovieResult.errorBody()!!.string()).getJSONObject("error")
                        // get error message
                        val errorMessage = errorBody.getString("message")
                        Result.Error(errorHandler.getErrorFromMessage(errorMessage))
                    }
                } catch (e: IOException) {
                    Result.Error(ErrorEntity.Network)
                }
            }
    }
}