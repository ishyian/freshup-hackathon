package com.melitopolcherry.hackathon.features.login.forgot_password

import androidx.lifecycle.MutableLiveData
import com.melitopolcherry.hackathon.base.BaseViewModel
import com.melitopolcherry.hackathon.data.utils.Enums
import com.melitopolcherry.hackathon.domain.managers.registration.IRegistrationManager
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val registrationManager: IRegistrationManager
) : BaseViewModel() {
    val isLoading: MutableLiveData<Boolean> = MutableLiveData()
    val showNextScreen: MutableLiveData<Boolean> = MutableLiveData()
    val showErrorMessage: MutableLiveData<String> = MutableLiveData()
    val processError: MutableLiveData<Throwable> = MutableLiveData()

    fun sendEmail(email: String) {
        if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            registrationManager.forgotPassword(email)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { isLoading.postValue(true) }
                .doOnComplete { isLoading.postValue(false) }
                .doOnError {
                    isLoading.postValue(false)
                }
                .subscribe(::onEmailSentSuccessful, ::onEmailSentError)
        } else {
            if (email.isEmpty()) {
                showErrorMessage.postValue("Email should be filled")
            } else if (email.isEmpty()) {
                showErrorMessage.postValue("Incorrect email")
            }
        }
    }

    private fun onEmailSentSuccessful() {
        showNextScreen.postValue(true)
    }

    private fun onEmailSentError(e: Throwable) {
        processError.postValue(e)
        if (e.message == Enums.ErrorCodes.CODE_GENERATION_TIMEOUT.description) {
            showNextScreen.postValue(true)
        }
    }
}