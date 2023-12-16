package com.adel.moviesapp.data.api

import com.adel.moviesapp.data.model.MovieModel
import retrofit2.Response
import retrofit2.http.*

interface MovieApiService {
    @GET("Movies/{movieID}.json")
    suspend fun getMovieByID(@Path("movieID") movieID: String): Response<MovieModel>

    @GET("Movies.json")
    suspend fun getMovies(): Response<HashMap<String, MovieModel>>

    @GET("PopularMovies.json")
    suspend fun getPopularMovies(): Response<HashMap<String, MovieModel>>

    @GET("savedMovies/{user}.json")
    suspend fun getMoviesSaved(@Path("user") userUID: String): Response<HashMap<String, String>>

    @PATCH("savedMovies/{user}.json")
    suspend fun saveMovie(
        @Path("user") userUID: String, @Body movie: Map<String, String>
    ): Response<HashMap<String, String>>

    @DELETE("savedMovies/{user}/{movieID}.json")
    suspend fun deleteSavedMovie(
        @Path("user") userUID: String,
        @Path("movieID") movieID: String
    ): Response<Object>
}