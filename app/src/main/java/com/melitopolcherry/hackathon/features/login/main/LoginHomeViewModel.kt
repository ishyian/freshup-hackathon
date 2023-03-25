package com.melitopolcherry.hackathon.features.login.main

import androidx.lifecycle.MutableLiveData
import com.melitopolcherry.hackathon.base.BaseViewModel
import com.melitopolcherry.hackathon.data.livedata.AuthTokenLiveData
import com.melitopolcherry.hackathon.data.models.login.LoginResponse
import com.melitopolcherry.hackathon.data.models.login.User
import com.melitopolcherry.hackathon.data.models.responces.GoogleAccessTokenResponse
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
class LoginHomeViewModel @Inject constructor(
    private val registrationManager: IRegistrationManager,
    private val accountManager: IAccountManager
) : BaseViewModel(), FacebookCallback<LoginResult> {
    val isLoading: MutableLiveData<Boolean> = MutableLiveData()
    val showErrorMessage: MutableLiveData<String> = MutableLiveData()
    val processError: MutableLiveData<Throwable> = MutableLiveData()
    val navigateToMain: MutableLiveData<Boolean> = MutableLiveData()

    private val tokenLiveData = AuthTokenLiveData.instance

    //Facebook login onSuccess
    override fun onSuccess(result: LoginResult) {
        registrationManager.facebookLogin(result.accessToken.token, fcmToken = LoginActivity.fcmToken)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { isLoading.postValue(true) }
            .doOnSuccess { isLoading.postValue(false) }
            .doOnError { isLoading.postValue(false) }
            .subscribe(
                { response -> onSuccessLoginWithProvider(response, Enums.LoginMethods.Facebook) },
                processError::postValue
            )
    }

    //Facebook login onCancel
    override fun onCancel() {
        isLoading.postValue(false)
    }

    //Facebook login onError
    override fun onError(error: FacebookException) {
        isLoading.postValue(false)
        Timber.d(error.message.toString())
    }

    fun saveSkipLogin() {
        accountManager.saveLoginMethod(Enums.LoginMethods.NONE)
    }

    fun googleLoginEvenz(
        grantType: String, clientId: String, clientSecret: String,
        redirectUri: String, code: String
    ) {
        registrationManager.getGoogleAccessToken(
            grantType,
            clientId,
            clientSecret,
            redirectUri,
            code
        ).subscribe(
            ::onSuccessGoogleTokenReceived,
            processError::postValue
        )
    }

    private fun onSuccessGoogleTokenReceived(token: GoogleAccessTokenResponse) {
        loginWithGoogle(token.access_token)
    }

    private fun loginWithGoogle(accessToken: String) {
        registrationManager.googleLogin(accessToken, LoginActivity.fcmToken)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { isLoading.postValue(true) }
            .doOnError { isLoading.postValue(false) }
            .subscribe(
                { response -> onSuccessLoginWithProvider(response, Enums.LoginMethods.Google) },
                processError::postValue
            )
    }

    private fun onSuccessLoginWithProvider(result: LoginResponse, loginMethod: Enums.LoginMethods) {
        result.accessToken?.let { _ ->
            accountManager.saveLoginMethod(loginMethod)
            val user: User = result.user!!
            accountManager.saveUser(user)
            tokenLiveData.setToken(result.separateToken())
            accountManager.saveToken(result.separateToken())
            navigateToMain.postValue(true)
            isLoading.postValue(false)
        }
    }
}