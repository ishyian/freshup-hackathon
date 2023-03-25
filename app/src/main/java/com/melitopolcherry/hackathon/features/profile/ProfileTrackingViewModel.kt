package com.melitopolcherry.hackathon.features.profile

import androidx.lifecycle.MutableLiveData
import com.melitopolcherry.hackathon.base.BaseViewModel
import com.melitopolcherry.hackathon.data.livedata.AuthTokenLiveData
import com.melitopolcherry.hackathon.data.models.tracking.TrackedEntetiesResponse
import com.melitopolcherry.hackathon.domain.managers.events.IEventManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileTrackingViewModel @Inject constructor(
    private val evensManager: IEventManager
) : BaseViewModel() {

    private val tokenLiveData = AuthTokenLiveData.instance

    val isLoading: MutableLiveData<Boolean> = MutableLiveData()
    val processError: MutableLiveData<Throwable> = MutableLiveData()
    val trackingResponse: MutableLiveData<TrackedEntetiesResponse?> = MutableLiveData()

    fun loadData() {
        if (tokenLiveData.value != null) {
            evensManager.getTracked(tokenLiveData.value?.accessToken!!)
                .doOnSubscribe { isLoading.postValue(true) }
                .doOnError { isLoading.postValue(false) }
                .doOnSuccess { isLoading.postValue(false) }
                .subscribe({
                               if (it.events?.isNotEmpty()!! || it.performers?.isNotEmpty()!! || it.venues?.isNotEmpty()!!) {
                                   trackingResponse.postValue(it)
                               } else {
                                   trackingResponse.postValue(TrackedEntetiesResponse(events = listOf()))
                               }
                           }, { e ->
                               processError.postValue(e)
                           })
        } else {
            trackingResponse.postValue(null)
        }
    }

}