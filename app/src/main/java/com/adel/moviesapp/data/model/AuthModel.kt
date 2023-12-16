package com.adel.moviesapp.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class AuthModel(
    @SerializedName("localId") @Expose() var uid: String,
    @SerializedName("idToken") @Expose() var token: String,
    @SerializedName("message") var message:String
)