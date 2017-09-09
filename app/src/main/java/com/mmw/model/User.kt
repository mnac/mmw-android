package com.mmw.model

import android.os.Parcel
import android.os.Parcelable
import android.util.Patterns
import com.google.gson.annotations.SerializedName
import com.mmw.AppConstant

/**
 * Created by Mathias on 24/08/2017.
 *
 */
data class User constructor(
        @SerializedName(value="firstName", alternate=arrayOf("first_name")) var firstName: String?,
        @SerializedName(value="lastName", alternate=arrayOf("last_name")) var lastName: String?,
        val email: String?,
        val password: String?,
        val uuid: String?,
        var pseudo: String?,
        var gender: Int?,
        var description: String?,
        @SerializedName(value="pictureProfile", alternate=arrayOf("profile_picture")) var pictureProfile: String?)
    : Parcelable {

    constructor(firstName: String?, lastName: String?, email: String?, password: String?) :
            this(firstName, lastName, email, password, null, null, 0, null, null)

    constructor(email: String?, password: String?) :
            this(null, null, email, password, null, null, 0, null, null)

    constructor(email: String?, uuid: String?, firstName: String?, lastName: String?, pseudo: String?, gender: Int?, pictureProfile: String?, description: String?) :
            this(firstName, lastName, email, null, uuid, pseudo, gender, description, pictureProfile)

    val isValidForRegistration
        get() = !firstName?.isEmpty()!!
                && !lastName?.isEmpty()!!
                && isValidForLogin

    val isValidForLogin
        get() = email != null && !email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
                && password != null && !password.isEmpty() && password.length > AppConstant.PASSWORD_MIN_LENGTH

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(firstName)
        parcel.writeString(lastName)
        parcel.writeString(email)
        parcel.writeString(password)
        parcel.writeString(uuid)
        parcel.writeString(pseudo)
        parcel.writeValue(gender)
        parcel.writeString(description)
        parcel.writeString(pictureProfile)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }
}