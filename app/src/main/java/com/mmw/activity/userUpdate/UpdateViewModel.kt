package com.mmw.activity.userUpdate

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.databinding.ObservableInt
import android.view.View
import com.mmw.AppConstant
import com.mmw.R
import com.mmw.data.repository.UserRepository
import com.mmw.helper.view.SingleLiveEvent
import com.mmw.model.User
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import retrofit2.Response

/**
 * Created by Mathias on 24/08/2017.
 *
 */

class UpdateViewModel(context: Application, private val userRepo: UserRepository)
    : AndroidViewModel(context) {

    var user: User? = null

    private val disposable = CompositeDisposable()

    val pseudo = ObservableField<String>("")
    val firstName = ObservableField<String>("")
    val lastName = ObservableField<String>("")
    val description = ObservableField<String>("")

    val dataLoading = ObservableBoolean(false)

    val pseudoError = ObservableField<String>("")
    val firstNameError = ObservableField<String>("")
    val lastNameError = ObservableField<String>("")
    val descriptionError = ObservableField<String>("")

    val pseudoRequestFocus = ObservableBoolean(false)
    val firstNameRequestFocus = ObservableBoolean(false)
    val lastNameRequestFocus = ObservableBoolean(false)
    val descriptionRequestFocus = ObservableBoolean(false)

    val snackBarMessage = SingleLiveEvent<String>()

    var isUpdated = SingleLiveEvent<Boolean>()

    var pictureLoading = ObservableBoolean(false)
    var progress = ObservableInt(0)
    val picturePath = ObservableField<String>("")
    private var pictureName: String? = null

    fun initUser(user: User) {
        this.user = user

        pseudo.set(user.pseudo)
        firstName.set(user.firstName)
        lastName.set(user.lastName)
        description.set(user.description)

        setPicture(user.pictureProfile)
    }

    fun setPicture(pictureName: String?) {
        if (pictureName != null) {
            this.pictureName = pictureName
            picturePath.set(AppConstant.S3_PROFILE_PICTURE_ROOT + pictureName)
        }
    }

    fun onClickUpdate(@Suppress("UNUSED_PARAMETER") view: View) {
        dataLoading.set(true)

        if (user != null) {
            user!!.pseudo = pseudo.get()
            user!!.firstName = firstName.get()
            user!!.lastName = lastName.get()
            user!!.description = description.get()
            user!!.pictureProfile = pictureName

            if (!handleFormError(user!!)) {
                updateUser(user!!)
            }
        }
    }

    private fun handleFormError(user: User): Boolean {
        resetError()

        val context = getApplication<Application>().applicationContext

        val mandatory = context.getString(R.string.form_field_error_mandatory)

        var isError = false

        if (user.firstName?.isEmpty()!!) {
            firstNameError.set(mandatory)
            firstNameRequestFocus.set(true)
            isError = true
        }

        if (user.lastName?.isEmpty()!!) {
            lastNameError.set(mandatory)
            lastNameRequestFocus.set(true)
            isError = true
        }

        return isError
    }

    private fun resetError() {
        pseudoError.set("")
        firstNameError.set("")
        lastNameError.set("")
        descriptionError.set("")

        pseudoRequestFocus.set(false)
        firstNameRequestFocus.set(false)
        lastNameRequestFocus.set(false)
        descriptionRequestFocus.set(false)
    }

    private fun updateUser(user: User) {
        dataLoading.set(true)
        disposable.add(userRepo.update(user)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        { e -> showMessage(e.message) },
                        {},
                        { r -> handleResponse(user, r) }
                ))
    }

    private fun handleResponse(user: User, response: Response<Void>) {
        if (response.isSuccessful) {
            isUpdated.value = true
        } else {
            dataLoading.set(false)
        }
    }

    private fun showMessage(message: String?) {
        dataLoading.set(false)
        snackBarMessage.value = message
    }

    fun saveState() {
        if (user != null) {
            userRepo.saveUserState(user!!)
        }
    }

    fun restoreState() {
        val user: User? = userRepo.getLastUserState()
        pseudo.set(user?.pseudo)
        firstName.set(user?.firstName)
        lastName.set(user?.lastName)
        description.set(user?.description)
    }

    fun dispose() {
        disposable.clear()
    }
}