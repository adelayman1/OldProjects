package com.adel.moviesapp.domain.enities

sealed class ErrorEntity {
    object Network : ErrorEntity()
    object NotFound : ErrorEntity()
    object AccessDenied : ErrorEntity()
    object ServiceUnAvailable : ErrorEntity()
    object Unknown : ErrorEntity()
    object EmailIsNotValid : ErrorEntity()
    object PasswordIsNotValid : ErrorEntity()
    object UserIsExist : ErrorEntity()
    object UserIsNotExist : ErrorEntity()
    object EmailExists : ErrorEntity()
    object EmailNotFound : ErrorEntity()
    object TooManyAttempts : ErrorEntity()
    object UserDisabled : ErrorEntity()
    object InvalidIdToken : ErrorEntity()
}
