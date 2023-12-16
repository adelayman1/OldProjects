package com.adel.moviesapp.domain.repositories

import com.adel.moviesapp.data.model.UserModel
import retrofit2.Response

interface UserRepository {
    suspend fun uploadUser(uid: String, user:UserModel): Response<UserModel>
    suspend fun getUser(uid: String): Response<UserModel>
    suspend fun updateUserName(uid:String,name:HashMap<String,String>): Response<Any>
}