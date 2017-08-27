package com.mmw.data.model

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by Mathias on 26/08/2017.
 *
 */
data class Trip constructor(
        val id: String?,
        val userId: String?,
        val title: String?,
        val description: String?,
        val pictureUrl: String?,
        val feed: String?): Parcelable {

    constructor(userId: String?, title: String?, description: String?) :
            this(null, userId, title, description, null, "timeline")

    val isValidForCreation
        get() = !userId?.isEmpty()!!
                && !title?.isEmpty()!!

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(userId)
        parcel.writeString(title)
        parcel.writeString(description)
        parcel.writeString(pictureUrl)
        parcel.writeString(feed)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Trip> {
        override fun createFromParcel(parcel: Parcel): Trip {
            return Trip(parcel)
        }

        override fun newArray(size: Int): Array<Trip?> {
            return arrayOfNulls(size)
        }
    }
}