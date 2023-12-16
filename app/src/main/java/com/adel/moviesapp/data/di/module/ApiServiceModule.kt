package com.adel.moviesapp.data.di.module

import com.adel.moviesapp.data.api.AuthApiService
import com.adel.moviesapp.data.api.MovieApiService
import com.adel.moviesapp.data.api.UserApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Named
import javax.inject.Singleton
@Module(includes = [NetworkModule::class])
@InstallIn(SingletonComponent::class)
class ApiServiceModule {
    @Singleton
    @Provides
    fun provideAuthApiService(@Named("Auth") retrofit: Retrofit): AuthApiService {
        return retrofit.create(AuthApiService::class.java)
    }

    @Singleton
    @Provides
    fun provideUserApiService(@Named("Realtime") retrofit: Retrofit): UserApiService {
        return retrofit.create(UserApiService::class.java)
    }

    @Singleton
    @Provides
    fun provideMovieApiService(@Named("Realtime") retrofit: Retrofit): MovieApiService {
        return retrofit.create(MovieApiService::class.java)
    }

}