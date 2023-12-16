package com.adel.moviesapp.data.utilities

import com.adel.moviesapp.domain.enities.ErrorEntity
import com.adel.moviesapp.domain.utilities.ErrorHandler
import retrofit2.HttpException
import java.io.IOException
import java.net.HttpURLConnection
import java.net.UnknownHostException
import javax.inject.Inject

class ErrorHandlerImpl @Inject constructor(): ErrorHandler {
    override fun getErrorFromThrowable(throwable: Throwable): ErrorEntity {
        return when (throwable) {
            is IOException -> ErrorEntity.Network
            is UnknownHostException -> ErrorEntity.Network
            is HttpException -> {
                when (throwable.code()) {
                    HttpURLConnection.HTTP_NOT_FOUND -> ErrorEntity.NotFound
                    HttpURLConnection.HTTP_FORBIDDEN -> ErrorEntity.AccessDenied
                    HttpURLConnection.HTTP_UNAVAILABLE -> ErrorEntity.ServiceUnAvailable
                    else -> ErrorEntity.Unknown
                }
            }
            else -> ErrorEntity.Unknown
        }
    }

    override fun getErrorFromMessage(message: String): ErrorEntity {
        return when(message){
            "EMAIL_EXISTS" ->ErrorEntity.EmailIsNotValid
            "TOO_MANY_ATTEMPTS_TRY_LATER" ->ErrorEntity.TooManyAttempts
            "EMAIL_NOT_FOUND"->ErrorEntity.EmailNotFound
            "INVALID_PASSWORD"->ErrorEntity.PasswordIsNotValid
            "USER_DISABLED"->ErrorEntity.UserDisabled
            "INVALID_ID_TOKEN"->ErrorEntity.InvalidIdToken
            "WEAK_PASSWORD"->ErrorEntity.PasswordIsNotValid
            else -> ErrorEntity.Unknown
        }
    }
}