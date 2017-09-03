package com.mmw.activity.main

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.databinding.ObservableBoolean
import com.mmw.App
import com.mmw.model.OAuthCredentials
import com.mmw.data.repository.UserRepository
import io.reactivex.disposables.CompositeDisposable

/**
 * Created by Mathias on 25/08/2017.
 *
 */

class MainViewModel(context: Application, private val userRepo: UserRepository)
    : AndroidViewModel(context) {

    private val disposable = CompositeDisposable()

    val dataLoading = ObservableBoolean(false)

    private fun loginUser() {
        dataLoading.set(true)
    }

    private fun handleError() {
        dataLoading.set(false)
    }

    private fun handleToken(credentials: OAuthCredentials) {
        App.restClient.setHeaders(credentials.accessToken, credentials.userId)
        dataLoading.set(false)
    }

    fun dispose() {
        disposable.clear()
    }
}