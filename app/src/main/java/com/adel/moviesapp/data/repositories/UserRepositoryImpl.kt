package com.adel.moviesapp.data.repositories

import android.content.SharedPreferences
import com.adel.moviesapp.data.model.UserModel
import com.adel.moviesapp.data.api.UserApiService
import com.adel.moviesapp.domain.repositories.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    var userService: UserApiService,
    var sharedPreferences: SharedPreferences
): UserRepository {
    override suspend fun uploadUser(uid: String, user:UserModel): Response<UserModel> {
        return userService.addUser(uid, user)
    }

    override suspend fun getUser(uid: String): Response<UserModel> {
        return userService.getUser(uid)
    }
    override suspend fun updateUserName(uid:String,name:HashMap<String,String>): Response<Any> {
        return userService.updateUserName(uid,name)
    }

}