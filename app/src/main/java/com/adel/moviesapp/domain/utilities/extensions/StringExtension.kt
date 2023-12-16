package com.adel.moviesapp.domain.utilities.extensions

import android.text.TextUtils

public fun CharSequence.isEmailValid(): Boolean {
    return !TextUtils.isEmpty(this) && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

public fun CharSequence.isPasswordValid(): Boolean {
    return !TextUtils.isEmpty(this) && (Regex("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#\$%!\\-_?&])(?=\\S+\$).{8,}").matches(this))
}