package com.mmw.model

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by Mathias on 27/08/2017.
 *
 */
data class PaginationKeyUserTrips constructor(val id: String, val userId: String, val creationDate: String) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(userId)
        parcel.writeString(creationDate)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PaginationKeyUserTrips> {
        override fun createFromParcel(parcel: Parcel): PaginationKeyUserTrips {
            return PaginationKeyUserTrips(parcel)
        }

        override fun newArray(size: Int): Array<PaginationKeyUserTrips?> {
            return arrayOfNulls(size)
        }
    }
}