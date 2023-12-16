package com.adel.moviesapp.data.api

import com.adel.moviesapp.data.model.AuthModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthApiService {
    @POST("./accounts:signInWithPassword")
    suspend fun login(
        @Query("key") key: String,
        @Query("email") email: String,
        @Query("password") password: String
    ): Response<AuthModel>

    @POST("./accounts:signUp")
    suspend fun signUp(
        @Query("key") key: String,
        @Query("email") email: String,
        @Query("password") password: String
    ): Response<AuthModel>

    @POST("./accounts:update")
    suspend fun changePassword(
        @Query("key") key: String,
        @Body passwordBody:HashMap<String,String>
    ): Response<AuthModel>
//
//    @GET("surah/{surahNum}/ar.alafasy")
//    suspend fun signUp(@Path("surahNum") surahNum: Int): ApiResponse<SurahDetailsModel>
}