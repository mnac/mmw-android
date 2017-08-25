package com.mmw.data.model

import android.util.Patterns
import com.mmw.AppConstant

/**
 * Created by Mathias on 24/08/2017.
 *
 */
data class User constructor(
        val firstName: String?,
        val lastName: String?,
        val email: String?,
        val password: String?,
        val uuid: String?,
        val pseudo: String?,
        val pictureProfile: String?) {

    constructor(firstName: String?, lastName: String?, email: String?, password: String?) :
            this(firstName, lastName, email, password, null, null, null)

    constructor(email: String?, password: String?) :
            this(null, null, email, password, null, null, null)

    val isValidForRegistration
        get() = !firstName?.isEmpty()!!
                && !lastName?.isEmpty()!!
                && isValidForLogin

    val isValidForLogin
        get() = email != null && !email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
                && password != null && !password.isEmpty() && password.length > AppConstant.PASSWORD_MIN_LENGTH
}