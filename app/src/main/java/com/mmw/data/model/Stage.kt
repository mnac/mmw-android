package com.mmw.data.model

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by Mathias on 27/08/2017.
 *
 */
data class Stage constructor(
        val id: String?,
        val tripId: String?,
        val title: String?,
        val comment: String?,
        val type: String?,
        val rate: Int?,
        val latitude: Double?,
        val longitude: Double?,
        val address: String?,
        val date: String?,
        val pictureUrl: String?): Parcelable {

    constructor(tripId: String?, title: String?, comment: String?, type: String?, rate: Int?,
                latitude: Double?, longitude: Double?, address: String?, date: String?,
                pictureUrl: String?) :
            this(null, tripId, title, comment, type, rate, latitude, longitude, address, date, pictureUrl)

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Double::class.java.classLoader) as? Double,
            parcel.readValue(Double::class.java.classLoader) as? Double,
            parcel.readString(),
            parcel.readString(),
            parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(tripId)
        parcel.writeString(title)
        parcel.writeString(comment)
        parcel.writeString(type)
        parcel.writeValue(rate)
        parcel.writeValue(latitude)
        parcel.writeValue(longitude)
        parcel.writeString(address)
        parcel.writeString(date)
        parcel.writeString(pictureUrl)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Stage> {
        override fun createFromParcel(parcel: Parcel): Stage {
            return Stage(parcel)
        }

        override fun newArray(size: Int): Array<Stage?> {
            return arrayOfNulls(size)
        }
    }
}