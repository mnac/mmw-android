package com.mmw.data.source.local

import android.content.Context
import android.preference.PreferenceManager

/**
 * Created by Mathias on 24/08/2017.
 *
 */
class Preferences {
    companion object {
        private const val TOKEN_KEY = "token"
        private const val USER_ID = "id"

        fun saveOauthCredentials(context: Context, token: String, id: String) {
            saveToken(context, token)
            saveUserId(context, id)
        }

        fun getToken(context: Context) : String {
            return PreferenceManager.getDefaultSharedPreferences(context)
                    .getString(TOKEN_KEY, "")
        }

        private fun saveToken(context: Context, token: String) {
            PreferenceManager.getDefaultSharedPreferences(context)
                    .edit()
                    .putString(TOKEN_KEY, token)
                    .apply()
        }

        fun hasToken(context: Context): Boolean {
            return PreferenceManager.getDefaultSharedPreferences(context)
                    .contains(TOKEN_KEY)
        }

        private fun saveUserId(context: Context, id: String) {
            PreferenceManager.getDefaultSharedPreferences(context)
                    .edit()
                    .putString(USER_ID, id)
                    .apply()
        }

        fun getUserId(context: Context) : String {
            return PreferenceManager.getDefaultSharedPreferences(context)
                    .getString(USER_ID, "")
        }
    }
}