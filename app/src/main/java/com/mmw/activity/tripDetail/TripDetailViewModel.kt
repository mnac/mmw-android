package com.mmw.activity.tripDetail

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.databinding.*
import android.util.Log
import com.mmw.AppConstant
import com.mmw.R
import com.mmw.data.repository.TripRepository
import com.mmw.model.Stage
import com.mmw.model.Trip
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import retrofit2.Response


class TripDetailViewModel(context: Application, private var userId: String?, private var tripRepo: TripRepository): AndroidViewModel(context) {

    val dataLoading = ObservableBoolean(false)

    private val disposable = CompositeDisposable()

    val stageList: ObservableList<Stage> = ObservableArrayList()
    var trip: Trip? = null

    var mainPicturePath: ObservableField<String> = ObservableField("")

    private var owner: Boolean = false
    private var follow: Boolean = false
    var fabSrc: ObservableInt = ObservableInt(0)

    var mapAvailable: ObservableBoolean = ObservableBoolean(false)

    fun loadTrip(trip: Trip?) {
        if (trip?.id != null) {
            loadTrip(false, trip.id)
        }
    }

    fun loadTrip() {
        if (trip != null && trip!!.id != null) {
            loadTrip(false, this.trip!!.id!!)
        }
    }

    fun getStages(): ArrayList<Stage>? {
        return if (trip != null) trip?.stages
        else ArrayList()
    }

    fun loadTrip(isRefresh: Boolean, tripId: String) {
        disposable.add(tripRepo.getTrip(tripId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        { e -> handleException(e) },
                        {},
                        { trip -> run {
                                handleTrip(trip)
                                showStages(trip.stages)
                            }
                        }
                ))
    }

    private fun handleTrip(trip: Trip) {
        owner = userId == trip.userId

        this.trip = trip
        follow = trip.isFollowing

        updateIcon()
    }

    private fun updateIcon() {
        if (owner) {
            fabSrc.set(R.drawable.ic_add_24dp)
        } else {
            if (follow) fabSrc.set(R.drawable.ic_following_24dp)
            else fabSrc.set(R.drawable.ic_not_following_24dp)
        }
    }

    private fun handleException(e: Throwable) {
        Log.d("Timeline error", e.message)
    }

    fun isOwner(): Boolean {
        return owner
    }

    fun handleClickFollow() {
        doFollow()
    }

    private fun doFollow() {
        val observable: Observable<Response<Void>>
        if (trip != null) {
            if (follow) {
                observable = tripRepo.unFollow(trip!!.id!!)
            } else {
                observable = tripRepo.doFollow(trip!!.id!!)
            }

            disposable.add(observable
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeBy(
                            { Log.i("Follow/Unfollow", "Failure") },
                            {},
                            {
                                follow = !follow
                                updateIcon()
                                Log.i("Follow/Unfollow", "Success")
                            }
                    ))
        }

    }

    private fun showStages(stages: ArrayList<Stage>?) {
        if (stages != null) {
            if (stages.size > 0 && !(stages[0].pictureUrl?.isEmpty())!!) {
                mainPicturePath.set(AppConstant.S3_TRIP_PICTURE_ROOT + stages[0].pictureUrl)
            }
            with(stageList) {
                clear()
                addAll(stages)
            }
        }
    }
}