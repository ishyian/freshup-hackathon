package com.melitopolcherry.hackathon.features.main

import androidx.lifecycle.MutableLiveData
import com.melitopolcherry.hackathon.base.BaseViewModel
import com.melitopolcherry.hackathon.data.livedata.AuthTokenLiveData
import com.melitopolcherry.hackathon.data.models.login.User
import com.melitopolcherry.hackathon.data.utils.Enums
import com.melitopolcherry.hackathon.domain.managers.account.IAccountManager
import com.melitopolcherry.hackathon.domain.managers.events.IEventManager
import com.melitopolcherry.hackathon.domain.managers.registration.IRegistrationManager
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val registrationManager: IRegistrationManager,
    private val accountManager: IAccountManager,
    private val eventsManager: IEventManager
) : BaseViewModel() {

    var user: User? = null
    var loginMethod: Enums.LoginMethods? = null
    var authToken: AuthTokenLiveData? = AuthTokenLiveData.instance
    private var disposable: Disposable? = null
    var unreadNotificationsCount = 0
    val unreadNotifCount: MutableLiveData<Int?> = MutableLiveData()
    val processError: MutableLiveData<Throwable> = MutableLiveData()

    init {
        user = accountManager.fetchUser()
        loginMethod = accountManager.getLoginMethod()
    }

    fun getUnreadNotificationsCount() {
        disposable =
            eventsManager.getCountUnreadNotifications(authToken?.value?.accessToken!!)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                               if (it.unreadCount != null) {
                                   unreadNotificationsCount = it.unreadCount!!
                                   unreadNotifCount.postValue(it.unreadCount)
                               }
                           }, { e ->
                               processError.postValue(e)
                           })
    }

    fun readAllNotification() {
        authToken?.value?.accessToken?.let {
            eventsManager.readNotifications(it)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                               unreadNotificationsCount = 0
                               unreadNotifCount.postValue(0)
                           }, { e ->
                               processError.postValue(e)
                           })
        }
    }

    override fun onCleared() {
        disposable?.dispose()
        super.onCleared()
    }
}