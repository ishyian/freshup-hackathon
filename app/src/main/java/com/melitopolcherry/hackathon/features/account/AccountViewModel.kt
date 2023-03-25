package com.melitopolcherry.hackathon.features.account

import androidx.lifecycle.MutableLiveData
import com.melitopolcherry.hackathon.adapters.callbacks.ChangePhoneCallback
import com.melitopolcherry.hackathon.adapters.callbacks.DeliveryInfoCallback
import com.melitopolcherry.hackathon.adapters.callbacks.PersonalInfoCallback
import com.melitopolcherry.hackathon.base.BaseViewModel
import com.melitopolcherry.hackathon.data.livedata.AuthTokenLiveData
import com.melitopolcherry.hackathon.data.models.Address
import com.melitopolcherry.hackathon.data.models.Phone
import com.melitopolcherry.hackathon.data.models.login.User
import com.melitopolcherry.hackathon.data.utils.Enums
import com.melitopolcherry.hackathon.domain.managers.account.IAccountManager
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    val accountManager: IAccountManager
) : BaseViewModel(), DeliveryInfoCallback, PersonalInfoCallback,
    ChangePhoneCallback {

    var user: User? = null
    var authToken: AuthTokenLiveData? = AuthTokenLiveData.instance
    var loginMethod: Enums.LoginMethods? = null

    val isLoading: MutableLiveData<Boolean> = MutableLiveData()
    val showContent: MutableLiveData<Boolean> = MutableLiveData()
    val processError: MutableLiveData<Throwable> = MutableLiveData()
    val goToLogin: MutableLiveData<Boolean> = MutableLiveData()
    val userUpdates: MutableLiveData<User> = MutableLiveData()
    val loginMethodUpdates: MutableLiveData<Enums.LoginMethods> = MutableLiveData()

    init {
        user = accountManager.fetchUser()
        userUpdates.postValue(user)
        loginMethod = accountManager.getLoginMethod()
    }

    fun getUser() {
        accountManager.getProfile(authToken?.value?.accessToken!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { isLoading.postValue(true) }
            .doOnSuccess { isLoading.postValue(false) }
            .doOnError { isLoading.postValue(false) }
            .subscribe({
                           it.let { u ->
                               showContent.postValue(true)
                               accountManager.saveUser(u)
                               loginMethodUpdates.postValue(loginMethod)
                               //                    accountManager.saveToken(it.separateToken())
                           }
                       }, { e ->
                           e.printStackTrace()
                       })
    }

    fun deleteUser() {
        val accessToken = accountManager.getToken()
        accountManager.deleteAccount(accessToken?.accessToken!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { isLoading.postValue(true) }
            .doOnComplete { isLoading.postValue(false) }
            .doOnError { isLoading.postValue(false) }
            .subscribe({
                           deleteFromLocalAndShowLogin()
                       }, { e ->
                           processError.postValue(e)
                       })
    }

    override val personalInfoCallback = object : PersonalInfoCallback.Callback {
        override fun onReturned(updatedUser: User) {
            user?.firstName = updatedUser.firstName
            user?.secondName = updatedUser.secondName
            userUpdates.postValue(user)
        }
    }

    override val changePhoneCallback = object : ChangePhoneCallback.Callback {
        override fun onReturned(phone: Phone?) {
            if (phone != user?.phone && phone != null) {
                user?.phone = phone
            }
            userUpdates.postValue(user)
        }
    }

    override val deliveryInfoCallback = object : DeliveryInfoCallback.Callback {
        override fun onReturned(address: Address?) {
            if (address != user?.address && address != null) {
                user?.address = address
            }
            userUpdates.postValue(user)
        }
    }

    fun deleteFromLocalAndShowLogin() {
        AuthTokenLiveData.instance.clearToken()
        accountManager.deleteFully()
        goToLogin.postValue(true)
    }
}