package com.melitopolcherry.hackathon.features.bottom

import androidx.lifecycle.MutableLiveData
import com.melitopolcherry.hackathon.adapters.callbacks.PersonalInfoCallback
import com.melitopolcherry.hackathon.base.BaseViewModel
import com.melitopolcherry.hackathon.data.livedata.AuthTokenLiveData
import com.melitopolcherry.hackathon.data.models.login.AccessToken
import com.melitopolcherry.hackathon.data.models.login.User
import com.melitopolcherry.hackathon.domain.managers.account.IAccountManager
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class PersonalInfoViewModel @Inject constructor(
    private val accountManager: IAccountManager
) : BaseViewModel() {

    var user: User? = null
    var authToken: AuthTokenLiveData? = AuthTokenLiveData.instance
    var personalInfoCallback: PersonalInfoCallback.Callback? = null
    val isLoading: MutableLiveData<Boolean> = MutableLiveData()
    val dismissView: MutableLiveData<Boolean> = MutableLiveData()
    val processError: MutableLiveData<Throwable> = MutableLiveData()
    val showMessage: MutableLiveData<String> = MutableLiveData()

    init {
        user = accountManager.fetchUser()
    }

    fun validateDataAndUpdate(firstName: String?, secondName: String?, email: String?) {
        if (firstName == user?.firstName && secondName == user?.secondName && email == user?.email) {
            personalInfoCallback?.onReturned(user!!)
            dismissView.postValue(true)
        } else if ((firstName != user?.firstName || secondName != user?.secondName) && email != user?.email) {
            validateAllFields(firstName, secondName, email)
        } else if ((firstName != user?.firstName || secondName != user?.secondName) && email == user?.email) {
            validateChangeName(firstName, secondName)
        } else if (firstName == user?.firstName && secondName == user?.secondName && email != user?.email) {
            validateChangeEmail(email)
        }
    }

    fun validateChangeName(firstName: String?, secondName: String?) {
        if (firstName?.isNotEmpty()!! && secondName?.isNotEmpty()!!) {
            val map = HashMap<String, Any?>()
            map["first_name"] = firstName
            map["second_name"] = secondName

            accountManager.updateUserName(authToken?.value?.accessToken!!, map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { isLoading.postValue(true) }
                .doOnSuccess { isLoading.postValue(false) }
                .doOnError { isLoading.postValue(false) }
                .subscribe({
                    user?.let { itUser ->
                        itUser.firstName = it.firstName
                        itUser.secondName = it.secondName
                        accountManager.saveUser(itUser)
                        personalInfoCallback?.onReturned(itUser)
                        dismissView.postValue(true)
                    }
                }, { e ->
                    processError.postValue(e)
                })
        } else {
            if (firstName.isEmpty()) {
                showMessage.postValue("First name is empty")
            }
            if (secondName?.isEmpty()!!) {
                showMessage.postValue("Second name is empty")
            }
        }
    }

    private fun validateChangeEmail(email: String?) {
        if (email?.isNotEmpty()!! && email != user?.email && android.util.Patterns.EMAIL_ADDRESS.matcher(
                email
            ).matches()
        ) {
            accountManager.updateUserEmail(authToken?.value?.accessToken!!, email)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { isLoading.postValue(true) }
                .doOnSuccess { isLoading.postValue(false) }
                .doOnError { isLoading.postValue(false) }
                .subscribe({
                    accountManager.saveUser(it.user!!)
                    val accessToken = AccessToken()
                    accessToken.accessToken = it.accessToken
                    accessToken.refreshToken = it.refreshToken
                    accessToken.tokenType = it.tokenType
                    accountManager.saveToken(accessToken)
                    personalInfoCallback?.onReturned(it.user!!)
                    dismissView.postValue(true)
                }, { e ->
                    processError.postValue(e)
                })
        } else {
            if (email.isEmpty()) {
                showMessage.postValue("Email is empty")
            }
            if (email == user?.email) {
                showMessage.postValue("It's your current email")
            }
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                showMessage.postValue("Incorrect email")
            }
        }
    }

    private fun validateAllFields(firstName: String?, secondName: String?, email: String?) {
        if (firstName?.isNotEmpty()!! && secondName?.isNotEmpty()!!) {
            val map = HashMap<String, Any?>()
            map["first_name"] = firstName
            map["second_name"] = secondName

            if (email?.isNotEmpty()!! && email != user?.email && android.util.Patterns.EMAIL_ADDRESS.matcher(
                    email
                ).matches()
            ) {
                accountManager.updateUserName(authToken?.value?.accessToken!!, map)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe { isLoading.postValue(true) }
                    .doOnSuccess { isLoading.postValue(false) }
                    .doOnError { isLoading.postValue(false) }
                    .subscribe({
                        user?.let { itUser ->
                            itUser.firstName = it.firstName
                            itUser.secondName = it.secondName
                            accountManager.saveUser(itUser)

                            accountManager.updateUserEmail(
                                authToken?.value?.accessToken!!,
                                email
                            )
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .doOnSubscribe { isLoading.postValue(true) }
                                .doOnSuccess { isLoading.postValue(false) }
                                .doOnError { isLoading.postValue(false) }
                                .subscribe({
                                    accountManager.saveUser(it.user!!)
                                    val accessToken = AccessToken()
                                    accessToken.accessToken = it.accessToken
                                    accessToken.refreshToken = it.refreshToken
                                    accessToken.tokenType = it.tokenType
                                    accountManager.saveToken(accessToken)
                                    personalInfoCallback?.onReturned(it.user!!)
                                    dismissView.postValue(true)
                                }, { e ->
                                    processError.postValue(e)
                                })
                        }
                    }, { e ->
                        processError.postValue(e)
                    })
            } else {
                if (email.isEmpty()) {
                    showMessage.postValue("Email is empty")
                }
                if (email == user?.email) {
                    showMessage.postValue("It's your current email")
                }
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    showMessage.postValue("Incorrect email")
                }
            }
        } else {
            if (firstName.isEmpty()) {
                showMessage.postValue("First name is empty")
            }
            if (secondName?.isEmpty()!!) {
                showMessage.postValue("Second name is empty")
            }
        }
    }

}