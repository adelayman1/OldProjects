package com.adel.moviesapp.domain.utilities

import com.adel.moviesapp.domain.enities.ErrorEntity

interface ErrorHandler {
    fun getErrorFromThrowable(throwable: Throwable): ErrorEntity
    fun getErrorFromMessage(message: String): ErrorEntity
}