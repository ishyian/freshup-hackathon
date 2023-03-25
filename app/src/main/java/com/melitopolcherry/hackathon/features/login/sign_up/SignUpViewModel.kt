package com.melitopolcherry.hackathon.features.login.sign_up

import android.util.Patterns
import androidx.lifecycle.MutableLiveData
import com.melitopolcherry.hackathon.base.BaseViewModel
import com.melitopolcherry.hackathon.data.livedata.AuthTokenLiveData
import com.melitopolcherry.hackathon.data.models.login.User
import com.melitopolcherry.hackathon.data.utils.Enums
import com.melitopolcherry.hackathon.domain.managers.account.IAccountManager
import com.melitopolcherry.hackathon.domain.managers.registration.IRegistrationManager
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val registrationManager: IRegistrationManager,
    private val accountManager: IAccountManager
) : BaseViewModel() {
    private val tokenLiveData = AuthTokenLiveData.instance

    val isLoading: MutableLiveData<Boolean> = MutableLiveData()
    val showErrorMessage: MutableLiveData<String> = MutableLiveData()
    val processError: MutableLiveData<Throwable> = MutableLiveData()
    val goToMain: MutableLiveData<Boolean> = MutableLiveData()
    val showNextScreen: MutableLiveData<Boolean> = MutableLiveData()

    fun sendEmailForSignUp(email: String) {
        if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            registrationManager.emailSignUp(email)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { isLoading.postValue(true) }
                .doOnComplete { isLoading.postValue(false) }
                .doOnError { isLoading.postValue(false) }
                .subscribe({
                    showNextScreen.postValue(true)
                }, { e ->
                    processError.postValue(e)
                })
        } else {
            if (email.isEmpty()) {
                showErrorMessage.postValue("Email is empty")
            } else {
                showErrorMessage.postValue("Incorrect email")
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
}