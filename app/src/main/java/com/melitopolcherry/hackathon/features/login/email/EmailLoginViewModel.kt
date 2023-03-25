package com.melitopolcherry.hackathon.features.login.email

import android.util.Patterns
import androidx.lifecycle.MutableLiveData
import com.melitopolcherry.hackathon.base.BaseViewModel
import com.melitopolcherry.hackathon.data.livedata.AuthTokenLiveData
import com.melitopolcherry.hackathon.data.models.login.User
import com.melitopolcherry.hackathon.data.utils.Enums
import com.melitopolcherry.hackathon.domain.managers.account.IAccountManager
import com.melitopolcherry.hackathon.domain.managers.registration.IRegistrationManager
import com.melitopolcherry.hackathon.features.login.LoginActivity
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class EmailLoginViewModel @Inject constructor(
    private val registrationManager: IRegistrationManager,
    private val accountManager: IAccountManager
) : BaseViewModel(), FacebookCallback<LoginResult> {
    private val tokenLiveData = AuthTokenLiveData.instance

    val isLoading: MutableLiveData<Boolean> = MutableLiveData()
    val showErrorMessage: MutableLiveData<String> = MutableLiveData()
    val processError: MutableLiveData<Throwable> = MutableLiveData()
    val goToMain: MutableLiveData<Boolean> = MutableLiveData()

    fun loginWithEmail(email: String, password: String, fcmToken: String) {
        if (email.isNotEmpty() && password.isNotEmpty() && password.length >= 8) {
            if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                println("🤢 FII${fcmToken}")
                registrationManager.emailLogin(
                    email,
                    password,
                    fcmToken
                ).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe { isLoading.postValue(true) }
                    .doOnSuccess { isLoading.postValue(false) }
                    .doOnError { isLoading.postValue(false) }
                    .subscribe({ loginResponse ->
                                   accountManager.saveLoginMethod(Enums.LoginMethods.Email)
                                   accountManager.saveUser(loginResponse.user!!)
                                   tokenLiveData.setToken(loginResponse.separateToken())
                                   accountManager.saveToken(loginResponse.separateToken())

                                   goToMain.postValue(true)
                               }, { e ->
                                   processError.postValue(e)
                               })
            } else {
                showErrorMessage.postValue("Incorrect email")
            }
        } else {
            if (email.isEmpty() && password.isEmpty()) {
                showErrorMessage.postValue("Email and Password should be filled")
            } else if (email.isEmpty()) {
                showErrorMessage.postValue("Email should be filled")
            } else if (password.isEmpty()) {
                showErrorMessage.postValue("Password should be filled")
            } else if (password.length < 8) {
                showErrorMessage.postValue("Too short Password")
            }
        }
    }

    fun googleLoginEvenz(
        grantType: String, clientId: String, clientSecret: String,
        redirectUri: String, code: String, fcmToken: String
    ) {
        registrationManager.getGoogleAccessToken(
            grantType,
            clientId,
            clientSecret,
            redirectUri,
            code
        ).subscribe({ googleToken ->
                        registrationManager.googleLogin(googleToken.access_token, fcmToken)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnSubscribe { isLoading.postValue(true) }
                            .doOnSuccess { isLoading.postValue(false) }
                            .doOnError { isLoading.postValue(false) }
                            .subscribe({ loginResponse ->
                                           if (loginResponse.accessToken != null) {
                                               accountManager.saveLoginMethod(Enums.LoginMethods.Google)
                                               val user: User = loginResponse.user!!
                                               accountManager.saveUser(user)
                                               tokenLiveData.setToken(loginResponse.separateToken())
                                               accountManager.saveToken(loginResponse.separateToken())

                                               goToMain.postValue(true)
                                           }
                                       }, { e ->
                                           processError.postValue(e)
                                       })
                    }, { e ->
                        processError.postValue(e)
                    })
    }

    override fun onCancel() {
        isLoading.postValue(false)
    }

    override fun onError(error: FacebookException) {
        isLoading.postValue(false)
        Timber.d(error.message.toString())
    }

    override fun onSuccess(result: LoginResult) {
        registrationManager.facebookLogin(result.accessToken.token, fcmToken = LoginActivity.fcmToken)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { isLoading.postValue(true) }
            .doOnSuccess { isLoading.postValue(false) }
            .doOnError { isLoading.postValue(false) }
            .subscribe({ loginResponse ->
                           if (loginResponse.accessToken != null) {
                               accountManager.saveLoginMethod(Enums.LoginMethods.Facebook)
                               val user: User = loginResponse.user!!
                               accountManager.saveUser(user)
                               tokenLiveData.setToken(loginResponse.separateToken())
                               accountManager.saveToken(loginResponse.separateToken())
                               goToMain.postValue(true)
                           }
                       }, { e ->
                           processError.postValue(e)
                       })
    }
}