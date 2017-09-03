package com.mmw.activity.tripCreation

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.support.annotation.StringRes
import android.view.View
import com.mmw.App
import com.mmw.R
import com.mmw.model.Trip
import com.mmw.data.repository.TripRepository
import com.mmw.helper.view.SingleLiveEvent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException

/**
 * Created by Mathias on 24/08/2017.
 *
 */

class TripCreationViewModel(context: Application, private val tripRepo: TripRepository)
    : AndroidViewModel(context) {

    private val disposable = CompositeDisposable()

    val dataLoading = ObservableBoolean(false)

    val title = ObservableField<String>("")
    val titleError = ObservableField<String>("")
    val titleRequestFocus = ObservableBoolean(false)

    val description = ObservableField<String>("")

    val snackBarMessage = SingleLiveEvent<String>()
    val snackBarMessageRes = SingleLiveEvent<Int>()

    var createdTrip = SingleLiveEvent<Trip>()


    fun onClickStart(@Suppress("UNUSED_PARAMETER") view: View) {
        val trip = Trip(App.Companion.currentUserId, title.get(), description.get())

        if (trip.isValidForCreation) {
            resetError()
            createTrip(trip)
        } else {
            handleFormError(trip)
        }
    }

    private fun resetError() {
        titleError.set("")
        titleRequestFocus.set(false)
    }

    private fun handleFormError(trip: Trip) {
        val context = getApplication<Application>().applicationContext

        val mandatory = context.getString(R.string.form_field_error_mandatory)

        if (trip.title?.isEmpty()!!) {
            titleError.set(mandatory)
            titleRequestFocus.set(true)
        }
    }

    private fun createTrip(trip: Trip) {
        dataLoading.set(true)

        disposable.add(tripRepo.create(trip)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        { e -> handleException(e) },
                        {},
                        { (trip) -> handleNewTrip(trip)}
                ))
    }

    private fun handleNewTrip(trip: Trip) {
        createdTrip.value = trip
    }

    private fun handleException(exception: Throwable) {
        if (exception is HttpException) {
            when {
                exception.code() == 401 -> showMessage(R.string.login_failure)
                else -> showMessage(exception.message())
            }
        } else {
            showMessage(exception.localizedMessage)
        }
    }

    private fun showMessage(@StringRes message: Int) {
        dataLoading.set(false)
        snackBarMessageRes.value = message
    }

    private fun showMessage(message: String?) {
        dataLoading.set(false)
        snackBarMessage.value = message
    }

    fun saveState() {
        tripRepo.saveTripState(Trip(App.Companion.currentUserId, title.get(), description.get()))
    }

    fun restoreState() {
        val trip: Trip? = tripRepo.getLastTripState()
        title.set(trip?.title)
        description.set(trip?.description)
    }

    fun dispose() {
        disposable.clear()
    }
}