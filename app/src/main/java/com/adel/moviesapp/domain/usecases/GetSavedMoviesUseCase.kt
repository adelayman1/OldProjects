package com.adel.moviesapp.domain.usecases

import android.content.SharedPreferences
import com.adel.moviesapp.data.model.MovieModel
import com.adel.moviesapp.data.repositories.AuthRepositoryImpl
import com.adel.moviesapp.data.repositories.MovieRepositoryImpl
import com.adel.moviesapp.data.utilities.ErrorHandlerImpl
import com.adel.moviesapp.domain.enities.ErrorEntity
import com.adel.moviesapp.domain.enities.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetSavedMoviesUseCase @Inject constructor(
    var movieRepository: MovieRepositoryImpl,
    var sharedPreferences: SharedPreferences,
    var authRepository: AuthRepositoryImpl,
    var errorHandler: ErrorHandlerImpl
) {

    suspend operator fun invoke(): Result<List<MovieModel>> {
        if (!authRepository.isUserLoggedIn())
            return Result.Error(ErrorEntity.UserIsNotExist)
        var result: MutableList<MovieModel> = mutableListOf()
        return runBlocking(Dispatchers.IO) {
            return@runBlocking try {
                val savedMoviesResult =
                    movieRepository.getSavedMovies(authRepository.getLocalUserUID())
                if (savedMoviesResult.isSuccessful) {
                    val savedMoviesIDs: List<String> =
                        ArrayList<String>(savedMoviesResult.body()!!.keys)
                    for (movieID in savedMoviesIDs) {
                        var movieItem = movieRepository.getMovieByID(movieID)
                        result.add(movieItem.body()!!)
                    }
                    Result.Success(result)
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
            }catch (e: NullPointerException) {
                Result.Success(result)
            }
        }
    }
}