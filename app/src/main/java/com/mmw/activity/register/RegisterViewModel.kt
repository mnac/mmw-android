package com.mmw.activity.register

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.support.annotation.StringRes
import android.util.Patterns
import android.view.View
import com.mmw.App
import com.mmw.AppConstant
import com.mmw.R
import com.mmw.data.model.OAuthCredentials
import com.mmw.data.model.User
import com.mmw.data.repository.UserRepository
import com.mmw.data.source.local.Preferences
import com.mmw.helper.view.SingleLiveEvent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import okhttp3.Credentials
import retrofit2.Response

/**
 * Created by Mathias on 24/08/2017.
 *
 */

class RegisterViewModel(context: Application, private val userRepo: UserRepository)
    : AndroidViewModel(context) {

    private val disposable = CompositeDisposable()

    val firstName = ObservableField<String>("")
    val lastName = ObservableField<String>("")
    val email = ObservableField<String>("")
    val password = ObservableField<String>("")

    val dataLoading = ObservableBoolean(false)

    val firstNameError = ObservableField<String>("")
    val lastNameError = ObservableField<String>("")
    val emailError = ObservableField<String>("")
    val passwordError = ObservableField<String>("")

    val firstNameRequestFocus = ObservableBoolean(false)
    val lastNameRequestFocus = ObservableBoolean(false)
    val emailRequestFocus = ObservableBoolean(false)
    val passwordRequestFocus = ObservableBoolean(false)

    val snackBarMessage = SingleLiveEvent<String>()
    val snackBarMessageRes = SingleLiveEvent<Int>()

    val isAuthenticated = SingleLiveEvent<Boolean>()

    fun onClickRegister(@Suppress("UNUSED_PARAMETER") view: View) {
        val user = User(firstName.get(), lastName.get(), email.get(), password.get())

        if (user.isValidForRegistration) {
            resetError()
            registerUser(user)
        } else {
            handleFormError(user)
            showMessage(R.string.form_fields_invalid)
        }
    }

    private fun resetError() {
        firstNameError.set("")
        lastNameError.set("")
        emailError.set("")
        passwordError.set("")

        firstNameRequestFocus.set(false)
        lastNameRequestFocus.set(false)
        emailRequestFocus.set(false)
        passwordRequestFocus.set(false)
    }

    private fun handleFormError(user: User) {
        resetError()

        val context = getApplication<Application>().applicationContext

        val mandatory = context.getString(R.string.form_field_error_mandatory)

        if (user.firstName?.isEmpty()!!) {
            firstNameError.set(mandatory)
            firstNameRequestFocus.set(true)
        }

        if (user.lastName?.isEmpty()!!) {
            lastNameError.set(mandatory)
            lastNameRequestFocus.set(true)
        }

        if (user.email?.isEmpty()!!) {
            emailError.set(mandatory)
            emailRequestFocus.set(true)
        } else if (!Patterns.EMAIL_ADDRESS.matcher(user.email).matches()) {
            emailError.set(context.getString(R.string.form_field_error_email_invalid))
            emailRequestFocus.set(true)
        }

        if (user.password?.isEmpty()!!) {
            passwordError.set(mandatory)
            passwordRequestFocus.set(true)
        } else if (user.password.length < AppConstant.PASSWORD_MIN_LENGTH) {
            passwordError.set(context.getString(R.string.form_field_error_password_too_short))
            passwordRequestFocus.set(true)
        }
    }

    private fun registerUser(user: User) {
        dataLoading.set(true)
        disposable.add(userRepo.register(user)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        { e -> showMessage(e.message) },
                        {},
                        { r -> handleResponse(user, r) }
                ))
    }

    private fun handleResponse(user: User, response: Response<Void>) {
        val basicToken: String = Credentials.basic(user.email, user.password)
        when {
            response.code() < 300 -> disposable.add(userRepo.connect(basicToken)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeBy(
                            { e -> showMessage(e.message) },
                            {},
                            { t -> handleToken(t) }
                    ))
            response.code() == 409 -> // user known, try to connect it with credentials
                disposable.add(userRepo.connect(basicToken)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeBy(
                                {
                                    showMessage(response.errorBody()?.string())
                                },
                                {},
                                { t -> handleToken(t) }
                        ))
            else -> showMessage(response.errorBody()?.string())
        }

    }

    private fun handleToken(credentials: OAuthCredentials) {
        Preferences.saveOauthCredentials(
                getApplication<Application>().applicationContext,
                credentials.accessToken,
                credentials.userId)
        App.restClient.setToken(credentials.accessToken)
        App.currentUserId = credentials.userId
        dataLoading.set(false)
        isAuthenticated.value = true
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
        userRepo.saveUserState(User(firstName.get(), lastName.get(), email.get(), password.get()))
    }

    fun restoreState() {
        val user: User? = userRepo.getLastUserState()
        firstName.set(user?.firstName)
        lastName.set(user?.lastName)
        email.set(user?.email)
        password.set(user?.password)
    }

    fun dispose() {
        disposable.clear()
    }
}