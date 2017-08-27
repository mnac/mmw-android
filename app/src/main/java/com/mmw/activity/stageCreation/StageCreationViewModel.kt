package com.mmw.activity.stageCreation

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.support.annotation.StringRes
import android.view.View
import com.mmw.AppConstant
import com.mmw.R
import com.mmw.data.model.Stage
import com.mmw.data.model.Trip
import com.mmw.data.repository.TripRepository
import com.mmw.helper.view.SingleLiveEvent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Mathias on 27/08/2017.
 *
 */
class StageCreationViewModel(context: Application, private val tripRepo: TripRepository)
    : AndroidViewModel(context) {

    private val disposable = CompositeDisposable()

    val dataLoading = ObservableBoolean(false)

    var address = ObservableField<String>("")
    val formattedDate = ObservableField<String>("")

    val picturePath = ObservableField<String>("")

    val title = ObservableField<String>("")
    val titleError = ObservableField<String>("")
    val titleRequestFocus = ObservableBoolean(false)

    val comment = ObservableField<String>("")

    var trip: Trip? = null
    var date: String? = null
    var type: String? = null
    var rate: Int? = null
    var latitude: Double? = null
    var longitude: Double? = null

    private var pictureName: String? = null

    fun setPicture(pictureName: String) {
        this.pictureName = pictureName
        picturePath.set(AppConstant.S3_BASE_URL + pictureName)
    }

    fun setPicture(file: File) {
        picturePath.set(file.path)
    }

    val snackBarMessage = SingleLiveEvent<String>()
    val snackBarMessageRes = SingleLiveEvent<Int>()

    var createdStage = SingleLiveEvent<Stage>()

    fun onClickValidate(@Suppress("UNUSED_PARAMETER") view: View) {
        val stage = Stage(trip?.id, title.get(), comment.get(), type, rate, latitude, longitude, address.get(), date, pictureName)

        val context = getApplication<Application>().applicationContext

        resetError()
        if (stage.title == null || stage.title.isEmpty()) {
            titleError.set(context.getString(R.string.stageCreationNoTitleError))
            return
        }

        if (stage.title.length < AppConstant.TITLE_MIN_LENGTH) {
            titleError.set(context.getString(R.string.stageCreationTitleTooShort))
            return
        }

        if (stage.pictureUrl == null || stage.pictureUrl.isEmpty()) {
            showMessage(R.string.stageCreationNoPicture)
            return
        }

        if (stage.tripId == null || stage.tripId.isEmpty()) {
            showMessage(R.string.stageCreationNoTripId)
            return
        }

        createStage(stage)
    }

    private fun createStage(stage: Stage) {
        dataLoading.set(true)

        disposable.add(tripRepo.create(stage)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        { e -> handleException(e) },
                        {},
                        { (stage) -> handleNewStage(stage)}
                ))
    }

    private fun handleException(e: Throwable) {
        showMessage(e.message)
    }

    private fun handleNewStage(stage: Stage) {
        dataLoading.set(false)
        createdStage.value = stage
    }

    private fun resetError() {
        titleError.set("")
        titleRequestFocus.set(false)
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
        tripRepo.saveTripState(trip!!)
        tripRepo.saveStageState(Stage(trip?.id, title.get(), comment.get(), type, rate, latitude, longitude, address.get(), date, pictureName))
    }

    fun restoreState() {
        trip = tripRepo.getLastTripState()
        val stage: Stage? = tripRepo.getLastStageState()
        title.set(stage?.title)
        comment.set(stage?.comment)
        type = stage?.type
        rate = stage?.rate
        latitude = stage?.latitude
        longitude = stage?.longitude
        address.set(stage?.address)
        date = stage?.date

        val formatSaveDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ", Locale.FRENCH)
        val formatDate = formatSaveDate.parse(date)

        val viewFormatDate = SimpleDateFormat("'le' d MMMM yyyy 'à' HH:mm", Locale.FRENCH)
        val formatDateForView = viewFormatDate.format(formatDate)
        formattedDate.set(formatDateForView)
        pictureName = stage?.pictureUrl
    }

    fun dispose() {
        disposable.clear()
    }
}