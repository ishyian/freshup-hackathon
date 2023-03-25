package com.melitopolcherry.hackathon.features.notifications

import androidx.lifecycle.MutableLiveData
import com.melitopolcherry.hackathon.base.BaseViewModel
import com.melitopolcherry.hackathon.data.livedata.AuthTokenLiveData
import com.melitopolcherry.hackathon.data.models.notification.NotificationsItem
import com.melitopolcherry.hackathon.domain.managers.account.IAccountManager
import com.melitopolcherry.hackathon.domain.managers.events.IEventManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val accountManager: IAccountManager,
    private val evensManager: IEventManager
) : BaseViewModel() {

    val tokenLiveData = AuthTokenLiveData.instance

    val isLoading: MutableLiveData<Boolean> = MutableLiveData()
    val processError: MutableLiveData<Throwable> = MutableLiveData()
    val notificationsResponse: MutableLiveData<List<NotificationsItem>?> = MutableLiveData()

    init {
        getNotifications()
    }

    fun getPush(type: Int) {
        if (tokenLiveData.value?.accessToken != null) {
            when (type) {
                0 -> {
                    evensManager.getPush(tokenLiveData.value?.accessToken!!)
                        .subscribe({}, { e ->
                            processError.postValue(e)
                        })
                }
                1 -> {
                    evensManager.getPushWithTitle(tokenLiveData.value?.accessToken!!)
                        .subscribe({}, { e ->
                            processError.postValue(e)
                        })
                }
                2 -> {
                    evensManager.getPushWithImage(tokenLiveData.value?.accessToken!!)
                        .subscribe({}, { e ->
                            processError.postValue(e)
                        })
                }
            }
        }
    }

    fun getNotifications() {
        println("🤟🏿 GET in NOTIF")

        if (tokenLiveData.value?.accessToken != null) {
            evensManager.getNotifications(tokenLiveData.value?.accessToken!!)
                .doOnSubscribe {
                    isLoading.postValue(true)
                }
                .doOnSuccess { isLoading.postValue(false) }
                .doOnError { isLoading.postValue(false) }
                .subscribe({
                    notificationsResponse.postValue(it)
                }, { e ->
                    notificationsResponse.postValue(null)
                    processError.postValue(e)
                })
        } else {
            notificationsResponse.postValue(null)
        }
    }

    fun deleteNotification(id: String) {
        tokenLiveData.value?.accessToken?.let {
            evensManager.deleteNotification(it, id)
                .subscribe({}, { e ->
                    processError.postValue(e)
                })
        }
    }
}