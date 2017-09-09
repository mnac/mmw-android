package com.mmw.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

/**
 * Created by Mathias on 27/08/2017.
 *
 */
data class StagesResult constructor(@SerializedName("Items")val Stages: ArrayList<Stage>, val Count: Int, val ScannedCount: Int, val LastEvaluatedKey: PaginationKeyStages?) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.createTypedArrayList(Stage),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readParcelable(PaginationKeyStages::class.java.classLoader))

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeTypedList(Stages)
        parcel.writeInt(Count)
        parcel.writeInt(ScannedCount)
        parcel.writeParcelable(LastEvaluatedKey, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<StagesResult> {
        override fun createFromParcel(parcel: Parcel): StagesResult {
            return StagesResult(parcel)
        }

        override fun newArray(size: Int): Array<StagesResult?> {
            return arrayOfNulls(size)
        }
    }
}