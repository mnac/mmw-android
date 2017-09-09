package com.mmw.activity.home.timeline

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.databinding.ObservableArrayList
import android.databinding.ObservableBoolean
import android.databinding.ObservableList
import android.util.Log
import com.mmw.data.repository.TripRepository
import com.mmw.model.Timeline
import com.mmw.model.Trip
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

/**
 * Created by Mathias on 27/08/2017.
 *
 */

class TimelineViewModel(context: Application, private val tripRepo: TripRepository)
    : AndroidViewModel(context) {

    private val disposable = CompositeDisposable()

    val dataLoading = ObservableBoolean(false)

    val trips: ObservableList<Trip> = ObservableArrayList()

    fun onRefresh() {
        loadTasks(true)
    }

    fun loadTasks(isRefresh: Boolean) {
        dataLoading.set(true)
        disposable.add(tripRepo.getTimeline()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        { e -> handleException(e) },
                        {},
                        { t -> handleTimeline(t) }
                ))
    }

    private fun handleException(e: Throwable) {
        dataLoading.set(false)
        Log.d("Timeline error", e.message)
    }

    private fun handleTimeline(timeline: Timeline) {
        dataLoading.set(false)
        with(trips) {
            clear()
            addAll(timeline.trips)
        }
    }
}