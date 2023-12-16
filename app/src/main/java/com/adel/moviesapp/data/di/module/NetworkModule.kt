package com.adel.moviesapp.data.di.module

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {
    @Singleton
    @Named("Realtime")
    @Provides
    fun provideRealtimeRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://moviesapp-22e13-default-rtdb.firebaseio.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Singleton
    @Named("Auth")
    @Provides
    fun provideAuthRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://identitytoolkit.googleapis.com/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}