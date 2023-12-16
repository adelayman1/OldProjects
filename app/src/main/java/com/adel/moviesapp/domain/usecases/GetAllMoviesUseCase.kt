package com.adel.moviesapp.domain.usecases

import com.adel.moviesapp.data.model.MovieModel
import com.adel.moviesapp.data.repositories.MovieRepositoryImpl
import com.adel.moviesapp.domain.enities.ErrorEntity
import com.adel.moviesapp.domain.enities.Result
import com.adel.moviesapp.data.utilities.ErrorHandlerImpl
import org.json.JSONObject
import java.io.IOException
import javax.inject.Inject

class GetAllMoviesUseCase @Inject constructor(var repository: MovieRepositoryImpl, var errorHandler: ErrorHandlerImpl) {
    suspend operator fun invoke(): Result<ArrayList<MovieModel>> {
        return try {
            val getMoviesResult = repository.getMovies()
            if (getMoviesResult.isSuccessful) {
                Result.Success(ArrayList<MovieModel>(getMoviesResult.body()!!.values))
            } else {
                // get error body as json
                val errorBody =
                    JSONObject(getMoviesResult.errorBody()!!.string()).getJSONObject("error")
                // get error message
                val errorMessage = errorBody.getString("message")
                Result.Error(errorHandler.getErrorFromMessage(errorMessage))
            }
        } catch (e: IOException) {
            Result.Error(ErrorEntity.Network)
        }
    }
}