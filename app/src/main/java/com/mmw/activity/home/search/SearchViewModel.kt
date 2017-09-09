package com.mmw.activity.home.search

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.databinding.ObservableArrayList
import android.databinding.ObservableBoolean
import android.databinding.ObservableList
import android.util.Log
import com.mmw.data.repository.TripRepository
import com.mmw.model.Trip
import com.mmw.model.Trips
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

/**
 * Created by Mathias on 06/09/2017.
 *
 */
class SearchViewModel(context: Application, private val tripRepo: TripRepository)
    : AndroidViewModel(context) {

    private val disposable = CompositeDisposable()

    val dataLoading = ObservableBoolean(false)

    val trips: ObservableList<Trip> = ObservableArrayList()

    var query: String? = null

    fun onRefresh() {
        if (query == null) getFavoriteTrips()
        else searchTrip(query!!)
    }

    fun searchTrip(query: String) {
        dataLoading.set(true)
        disposable.add(tripRepo.searchTrips(query)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        { e -> handleException(e) },
                        {},
                        { t -> handleTrips(t) }
                ))
    }

    fun getFavoriteTrips() {
        dataLoading.set(true)
        query = null
        disposable.add(tripRepo.getFavoriteTrips()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        { e -> handleException(e) },
                        {},
                        { t -> handleTrips(t) }
                ))
    }

    private fun handleException(e: Throwable) {
        dataLoading.set(false)
        Log.d("Timeline error", e.message)
    }

    private fun handleTrips(result: Trips) {
        dataLoading.set(false)
        with(trips) {
            clear()
            addAll(result.trips)
        }
    }
}