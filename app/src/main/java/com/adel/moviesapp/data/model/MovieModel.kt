package com.adel.moviesapp.data.model

import android.os.Parcel
import android.os.Parcelable

data class MovieModel(
    var name: String,
    var type: String,
    var rate: Double,
    var image: String,
    var description: String,
    var runtime: String,
    var key: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readDouble(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(type)
        parcel.writeDouble(rate)
        parcel.writeString(image)
        parcel.writeString(description)
        parcel.writeString(runtime)
        parcel.writeString(key)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MovieModel> {
        override fun createFromParcel(parcel: Parcel): MovieModel {
            return MovieModel(parcel)
        }

        override fun newArray(size: Int): Array<MovieModel?> {
            return arrayOfNulls(size)
        }
    }


}