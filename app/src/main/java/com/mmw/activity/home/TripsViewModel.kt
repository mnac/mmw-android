package com.mmw.activity.home

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import com.mmw.data.repository.UserRepository
import io.reactivex.disposables.CompositeDisposable

/**
 * Created by Mathias on 27/08/2017.
 *
 */

class TripsViewModel(context: Application, private val userRepo: UserRepository)
    : AndroidViewModel(context) {

    private val disposable = CompositeDisposable()

    fun loadTasks(isRefresh: Boolean) {

    }
}