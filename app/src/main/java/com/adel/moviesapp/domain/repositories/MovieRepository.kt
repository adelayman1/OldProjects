package com.adel.moviesapp.domain.repositories

import com.adel.moviesapp.data.model.MovieModel
import retrofit2.Response

interface MovieRepository {
    suspend fun getMovies():  Response<HashMap<String, MovieModel>>
    suspend fun getMovieByID(id:String): Response<MovieModel>
    suspend fun getPopularMovies(): Response<HashMap<String, MovieModel>>
    suspend fun saveMovie(uid:String,movieID: HashMap<String, String>): Response<HashMap<String, String>>
    suspend fun getSavedMovies(userUID:String): Response<HashMap<String, String>>
    suspend fun unSaveMovie(uid:String,movieID: String): Response<Object>
}