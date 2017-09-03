package com.mmw.data.repository

import android.content.Context
import com.mmw.App
import com.mmw.AppConstant
import com.mmw.data.source.remote.api.UserService
import com.mmw.model.OAuthCredentials
import com.mmw.model.Push
import com.mmw.model.User
import io.reactivex.Observable
import retrofit2.Response


/**
 * Created by Mathias on 24/08/2017.
 *
 */
class UserRepository private constructor() {

    private val userService = UserService.create(App.restClient.retrofit)

    private object Holder { val INSTANCE = UserRepository() }

    private var currentUser: User? = null

    companion object  {
        val instance: UserRepository by lazy { Holder.INSTANCE }
    }

    fun register(user: User): Observable<Response<Void>> {
        return userService.register(user)
    }

    fun connect(basicToken: String) : Observable<OAuthCredentials> {
        return userService.connect(basicToken, hashMapOf(
                AppConstant.CONNECTION_BODY_KEY to AppConstant.CONNECTION_BODY_VALUE))
    }

    fun get(userId: String): Observable<User> {
        return userService.get(userId)
    }

    fun savePushToken(context: Context): Observable<Response<Void>>? {
        val preferences = com.mmw.data.source.local.Preferences
        val uuid: String? = preferences.getUserId(context)
        val token: String? = preferences.getPushToken(context)

        if (uuid != null && !uuid.isEmpty() && token != null && !token.isEmpty()) {
            val push = Push(uuid, token)
            return userService.savePushToken(push)
        } else {
            return null
        }
    }

    fun saveUserState(user: User) {
        currentUser = user
    }

    fun getLastUserState() : User? {
        return currentUser
    }
}