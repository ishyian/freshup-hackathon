package com.melitopolcherry.hackathon.features.login.sign_up_details

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import com.melitopolcherry.hackathon.base.BaseViewModel
import com.melitopolcherry.hackathon.data.livedata.AuthTokenLiveData
import com.melitopolcherry.hackathon.data.utils.Enums
import com.melitopolcherry.hackathon.domain.managers.account.IAccountManager
import com.melitopolcherry.hackathon.domain.managers.registration.IRegistrationManager
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class SignUpDetailsViewModel @Inject constructor(
    private val registrationManager: IRegistrationManager,
    private val accountManager: IAccountManager
) : BaseViewModel() {

    private val tokenLiveData = AuthTokenLiveData.instance
    private var fcmToken: String? = null
    private var backgroundHandler: Handler? = Handler(Looper.getMainLooper())
    private var subscription: Disposable? = null

    val isLoading: MutableLiveData<Boolean> = MutableLiveData()
    val goToMainScreen: MutableLiveData<Boolean> = MutableLiveData()
    val resetTimerText: MutableLiveData<Int> = MutableLiveData()
    val resetTimerBlock: MutableLiveData<Boolean> = MutableLiveData()
    val showErrorMessage: MutableLiveData<String> = MutableLiveData()
    val processError: MutableLiveData<Throwable> = MutableLiveData()

    init {
        startTimer()
        FirebaseMessaging.getInstance().token.addOnSuccessListener {
            println("🤢 FCM Token: $fcmToken")
            fcmToken = it
        }
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

    private fun timerMethod(num: Int) {
        backgroundHandler?.postDelayed({
                                           resetTimerText.postValue(num)
                                       }, 0)
    }

    private fun timerStop() {
        backgroundHandler?.postDelayed({
                                           resetTimerBlock.postValue(true)
                                       }, 0)
    }

    fun signUp(email: String, password: String, firstName: String, lastName: String, code: String) {

        if (android.util.Patterns.EMAIL_ADDRESS.matcher(email)
                .matches() && password.isNotEmpty() && firstName.isNotEmpty() && lastName.isNotEmpty() && code.isNotEmpty()
        ) {
            val registration = HashMap<String, String>()
            registration["email"] = email
            registration["password"] = password
            registration["firstname"] = firstName
            registration["lastname"] = lastName
            registration["confirmation_code"] = code
            registration["repeated_password"] = password
            registration["platform"] = "ANDROID"
            registration["device_token"] = fcmToken!!

            registrationManager.emailRegistration(registration)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { isLoading.postValue(true) }
                .doOnSuccess { isLoading.postValue(false) }
                .doOnError { isLoading.postValue(false) }
                .subscribe({ loginResponse ->
                               if (loginResponse.accessToken != null) {
                                   accountManager.saveLoginMethod(Enums.LoginMethods.Email)
                                   accountManager.saveUser(loginResponse.user!!)
                                   tokenLiveData.setToken(loginResponse.separateToken())
                                   accountManager.saveToken(loginResponse.separateToken())

                                   goToMainScreen.postValue(true)
                               }
                           }, { e ->
                               processError.postValue(e)
                           })
        } else {
            if (email.isEmpty() && password.isEmpty() && firstName.isEmpty() && lastName.isEmpty() && code.isEmpty()) {
                showErrorMessage.value = "Fields should be filled"
            } else if (email.isEmpty() && password.isEmpty()) {
                showErrorMessage.value = "Email and Password should be filled"
            } else if (email.isEmpty()) {
                showErrorMessage.value = "Email should be filled"
            } else if (password.isEmpty()) {
                showErrorMessage.value = "Password should be filled"
            } else if (password.length < 8) {
                showErrorMessage.value = "Too short Password"
            }
        }
    }

    fun resetTimer(email: String) {
        if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            registrationManager.emailSignUp(email)
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

    override fun onCleared() {
        backgroundHandler = null
        subscription?.dispose()
        super.onCleared()
    }
}