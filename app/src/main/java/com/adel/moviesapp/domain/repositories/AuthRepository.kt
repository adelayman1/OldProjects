package com.adel.moviesapp.domain.repositories

import com.adel.moviesapp.data.model.AuthModel
import retrofit2.Response

interface AuthRepository {
    suspend fun getDataByEmailAndPass(email: String, password: String): Response<AuthModel>
    suspend fun createNewAccount(email: String, password: String): Response<AuthModel>
    suspend fun changePassword(passwordBody: HashMap<String, String>): Response<AuthModel>

    fun isUserLoggedIn(): Boolean
    fun getLocalUserUID(): String
    fun getLocalUserTOKEN(): String
    fun changeLocalUserTOKEN(userTOKEN: String)
    fun changeLocalUserUID(userUID: String)
}