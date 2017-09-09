package com.mmw.activity.home.profile

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.databinding.ObservableArrayList
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.databinding.ObservableList
import android.util.Log
import com.mmw.AppConstant
import com.mmw.data.repository.TripRepository
import com.mmw.model.Trip
import com.mmw.model.User
import com.mmw.model.UserProfile
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

/**
 * Created by Mathias on 27/08/2017.
 *
 */

class ProfileViewModel(context: Application, private val tripRepo: TripRepository)
    : AndroidViewModel(context) {

    private val disposable = CompositeDisposable()

    val dataLoading = ObservableBoolean(false)

    var user: User? = null

    val firstName: ObservableField<String> = ObservableField("")
    val lastName: ObservableField<String> = ObservableField("")
    val picturePath: ObservableField<String> = ObservableField("")
    val trips: ObservableList<Trip> = ObservableArrayList()

    fun loadProfile(isRefresh: Boolean) {
        disposable.add(tripRepo.getUserProfile()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        { e -> handleException(e) },
                        {},
                        { t -> handleProfile(t) }
                ))
    }

    private fun handleException(e: Throwable) {
        Log.d("Timeline error", e.message)
    }

    private fun handleProfile(userProfile: UserProfile) {
        if (userProfile.user != null) {

            this.user = userProfile.user

            firstName.set(userProfile.user.firstName)
            lastName.set(userProfile.user.lastName)
            if (userProfile.user.pictureProfile != null
                    && !userProfile.user.pictureProfile!!.isEmpty()) {
                picturePath.set(AppConstant.S3_PROFILE_PICTURE_ROOT + userProfile.user.pictureProfile)
            }
        }

        with(trips) {
            clear()
            addAll(userProfile.trips)
        }
    }
}