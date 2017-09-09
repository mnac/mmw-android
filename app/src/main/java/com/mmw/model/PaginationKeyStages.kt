package com.mmw.model

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by Mathias on 27/08/2017.
 *
 */
data class PaginationKeyStages constructor(val id: String, val tripId: String, val date: String) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(tripId)
        parcel.writeString(date)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PaginationKeyStages> {
        override fun createFromParcel(parcel: Parcel): PaginationKeyStages {
            return PaginationKeyStages(parcel)
        }

        override fun newArray(size: Int): Array<PaginationKeyStages?> {
            return arrayOfNulls(size)
        }
    }
}