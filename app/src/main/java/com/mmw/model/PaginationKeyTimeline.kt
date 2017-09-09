package com.mmw.model

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by Mathias on 27/08/2017.
 *
 */
data class PaginationKeyTimeline constructor(val id: String, val feed: String, val creationDate: String) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(feed)
        parcel.writeString(creationDate)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PaginationKeyTimeline> {
        override fun createFromParcel(parcel: Parcel): PaginationKeyTimeline {
            return PaginationKeyTimeline(parcel)
        }

        override fun newArray(size: Int): Array<PaginationKeyTimeline?> {
            return arrayOfNulls(size)
        }
    }
}