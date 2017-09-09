package com.mmw.model

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by Mathias on 30/08/2017.
 *
 */
data class TripItemView constructor(val trip: Trip, val picturePath: String?, val title: String?, val averageRate: Float) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readParcelable(Trip::class.java.classLoader),
            parcel.readString(),
            parcel.readString(),
            parcel.readFloat())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(trip, flags)
        parcel.writeString(picturePath)
        parcel.writeString(title)
        parcel.writeFloat(averageRate)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TripItemView> {
        override fun createFromParcel(parcel: Parcel): TripItemView {
            return TripItemView(parcel)
        }

        override fun newArray(size: Int): Array<TripItemView?> {
            return arrayOfNulls(size)
        }
    }
}