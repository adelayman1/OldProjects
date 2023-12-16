package com.adel.moviesapp.data.api

import com.adel.moviesapp.data.model.UserModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path

interface UserApiService {
    @PATCH("users/{user}.json")
    suspend fun addUser(
        @Path("user") userUID: String,
        @Body userData: UserModel
    ): Response<UserModel>

    @GET("users/{user}.json")
    suspend fun getUser(
        @Path("user") userUID: String
    ): Response<UserModel>

    @PATCH("users/{user}.json")
    suspend fun updateUserName(
        @Path("user") userUID: String,
        @Body name: HashMap<String, String>
    ): Response<Any>
}