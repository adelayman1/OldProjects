package com.adel.moviesapp.data.repositories

import android.content.SharedPreferences
import com.adel.moviesapp.data.model.MovieModel
import com.adel.moviesapp.data.api.MovieApiService
import com.adel.moviesapp.domain.repositories.MovieRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieRepositoryImpl @Inject constructor(
    private var service: MovieApiService,
    var sharedPreferences: SharedPreferences
):MovieRepository {

    override suspend fun getMovies(): Response<HashMap<String, MovieModel>> = service.getMovies()
    override suspend fun getMovieByID(id:String): Response<MovieModel> =
        service.getMovieByID(id)

    //    suspend fun getMovie(key: Int): Flow<Response<MovieModel>> = flow { emit(service.readData("Movies/$key")) }
    override suspend fun getPopularMovies(): Response<HashMap<String, MovieModel>> = service.getPopularMovies()

    override suspend fun saveMovie(uid:String, movieID: HashMap<String, String>): Response<HashMap<String, String>> {
        return service.saveMovie(uid, movieID)
    }

    override suspend fun getSavedMovies(userUID:String): Response<HashMap<String, String>> = service.getMoviesSaved(userUID)


    override suspend fun unSaveMovie(uid:String,movieID: String): Response<Object> =
        service.deleteSavedMovie(uid, movieID)
}