package com.melitopolcherry.hackathon.features.login.change_password

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import com.melitopolcherry.hackathon.base.BaseViewModel
import com.melitopolcherry.hackathon.data.livedata.AuthTokenLiveData
import com.melitopolcherry.hackathon.data.utils.Enums
import com.melitopolcherry.hackathon.domain.managers.account.IAccountManager
import com.melitopolcherry.hackathon.domain.managers.registration.IRegistrationManager
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private val registrationManager: IRegistrationManager,
    private val accountManager: IAccountManager
) : BaseViewModel() {

    private var backgroundHandler: Handler? = Handler(Looper.getMainLooper())
    private val tokenLiveData = AuthTokenLiveData.instance
    private var subscription: Disposable? = null

    val goToMainScreen: MutableLiveData<Boolean> = MutableLiveData()
    val resetTimerText: MutableLiveData<Int> = MutableLiveData()
    val resetTimerBlock: MutableLiveData<Boolean> = MutableLiveData()
    val isLoading: MutableLiveData<Boolean> = MutableLiveData()
    val showErrorMessage: MutableLiveData<String> = MutableLiveData()
    val processError: MutableLiveData<Throwable> = MutableLiveData()

    init {
        startTimer()
    }

    private fun startTimer() {
        val values = Observable.interval(0, 1, TimeUnit.SECONDS).take(59, TimeUnit.SECONDS)
        var i = 59
        subscription = values.subscribe({
                                            timerMethod(i)
                                            i--
                                        }, { e -> processError.postValue(e) }, {
                                            timerStop()
                                        })
    }

    fun resetTimer(email: String) {
        if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            registrationManager.forgotPassword(email)
                .doOnSubscribe { isLoading.postValue(true) }
                .doOnComplete { isLoading.postValue(false) }
                .doOnError { isLoading.postValue(false) }
                .subscribe({
                               resetTimerBlock.postValue(true)
                               startTimer()
                           }, { e ->
                               processError.postValue(e)
                           })
        } else {
            showErrorMessage.postValue("You did input incorrect email")
        }
    }

    private fun timerMethod(num: Int) {
        backgroundHandler?.postDelayed({
                                           resetTimerText.postValue(num)
                                       }, 0)
    }

    private fun timerStop() {
        backgroundHandler?.postDelayed({
                                           resetTimerBlock.postValue(false)
                                       }, 0)
    }

    fun changePassword(email: String, newPassword: String, repeatPassword: String, code: String) {
        if (android.util.Patterns.EMAIL_ADDRESS.matcher(email)
                .matches() && newPassword.isNotEmpty() && repeatPassword.isNotEmpty() && code.isNotEmpty() && newPassword == repeatPassword
        ) {
            registrationManager.changePassword(email, newPassword, code)
                .doOnSubscribe { isLoading.postValue(true) }
                .doOnSuccess { isLoading.postValue(false) }
                .doOnError { isLoading.postValue(false) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ loginResponse ->
                               accountManager.saveLoginMethod(Enums.LoginMethods.Email)
                               accountManager.saveUser(loginResponse.user!!)
                               tokenLiveData.setToken(loginResponse.separateToken())
                               accountManager.saveToken(loginResponse.separateToken())

                               goToMainScreen.postValue(true)
                           }, { e ->
                               processError.postValue(e)
                           })
        } else {
            if (email.isEmpty() && newPassword.isEmpty() && repeatPassword.isEmpty() && code.isEmpty()) {
                showErrorMessage.postValue("Fields should be filled")
            } else if (email.isEmpty() && newPassword.isEmpty()) {
                showErrorMessage.postValue("Email and Password should be filled")
            } else if (email.isEmpty()) {
                showErrorMessage.postValue("Email should be filled")
            } else if (newPassword.isEmpty()) {
                showErrorMessage.postValue("Password should be filled")
            } else if (newPassword.length < 8) {
                showErrorMessage.postValue("Too short Password")
            }
        }
    }

    override fun onCleared() {
        backgroundHandler = null
        subscription?.dispose()
        super.onCleared()
    }
}