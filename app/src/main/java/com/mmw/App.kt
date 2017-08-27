package com.mmw

import android.app.Application
import com.mmw.data.source.local.Preferences
import com.mmw.data.source.remote.RestClient

/**
 * Created by Mathias on 24/08/2017.
 *
 */
class App : Application() {

    companion object {
        @JvmStatic lateinit var restClient: RestClient
        @JvmStatic lateinit var currentUserId: String
    }

    override fun onCreate() {
        super.onCreate()
        restClient = RestClient(AppConstant.baseUrl, Preferences.getToken(applicationContext))
        currentUserId = Preferences.getUserId(applicationContext)
    }
}