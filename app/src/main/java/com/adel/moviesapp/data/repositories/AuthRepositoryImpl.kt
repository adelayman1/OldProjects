package com.adel.moviesapp.data.repositories

import android.content.SharedPreferences
import com.adel.moviesapp.data.api.AuthApiService
import com.adel.moviesapp.data.model.AuthModel
import com.adel.moviesapp.data.utilities.Constants
import com.adel.moviesapp.data.utilities.Constants.SERVER_KEY
import com.adel.moviesapp.data.utilities.Constants.USER_TOKEN_KEY
import com.adel.moviesapp.data.utilities.Constants.USER_UID_KEY
import com.adel.moviesapp.domain.repositories.AuthRepository
import retrofit2.Response
import javax.inject.Inject


class AuthRepositoryImpl @Inject constructor(
    var apiService: AuthApiService,
    var sharedPreferences: SharedPreferences
) : AuthRepository {
    override suspend fun getDataByEmailAndPass(
        email: String,
        password: String
    ): Response<AuthModel> =
        apiService.login(SERVER_KEY, email, password)

    override suspend fun createNewAccount(
        email: String,
        password: String
    ): Response<AuthModel> =
        apiService.signUp(SERVER_KEY, email, password)

    override suspend fun changePassword(
        passwordBody: HashMap<String, String>
    ): Response<AuthModel> {
        return apiService.changePassword(
            SERVER_KEY,
            passwordBody
        )

    }


    override fun isUserLoggedIn(): Boolean = sharedPreferences.contains(USER_UID_KEY)
    override fun getLocalUserUID(): String =
        sharedPreferences.getString(USER_UID_KEY, null) ?: throw NullPointerException()

    override fun getLocalUserTOKEN(): String =
        sharedPreferences.getString(USER_TOKEN_KEY, null) ?: throw NullPointerException()

    override fun changeLocalUserTOKEN(userTOKEN: String) {
        val editor = sharedPreferences.edit()
        editor.putString(USER_TOKEN_KEY, userTOKEN)
        editor.commit()
    }

    override fun changeLocalUserUID(userUID: String) {
        val editor = sharedPreferences.edit()
        editor.putString(USER_UID_KEY, userUID)
        editor.commit()
    }
}