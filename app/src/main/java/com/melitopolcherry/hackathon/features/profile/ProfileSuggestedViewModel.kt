package com.melitopolcherry.hackathon.features.profile

import androidx.lifecycle.MutableLiveData
import com.melitopolcherry.hackathon.base.BaseViewModel
import com.melitopolcherry.hackathon.data.livedata.AuthTokenLiveData
import com.melitopolcherry.hackathon.data.models.suggested.GetSuggestedResponse
import com.melitopolcherry.hackathon.domain.managers.events.IEventManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileSuggestedViewModel @Inject constructor(
    private val evensManager: IEventManager
) : BaseViewModel() {

    private val tokenLiveData = AuthTokenLiveData.instance

    var canLoad = true
    var page: Int = 0
    var size: Int = 30
    val isLoading: MutableLiveData<Boolean> = MutableLiveData()
    val isLoadingMore: MutableLiveData<Boolean> = MutableLiveData()
    val processError: MutableLiveData<Throwable> = MutableLiveData()
    val suggestedResponse: MutableLiveData<GetSuggestedResponse?> = MutableLiveData()

    fun getSuggested() {
        if (tokenLiveData.value != null) {
            evensManager.getSuggestions(tokenLiveData.value?.accessToken!!, size)
                .doOnSubscribe {
                    if (page == 0) {
                        isLoading.postValue(true)
                    } else {
                        isLoadingMore.postValue(true)
                    }
                }
                .doOnSuccess {
                    if (page == 0) {
                        isLoading.postValue(false)
                    } else {
                        isLoadingMore.postValue(false)
                    }
                }
                .doOnError {
                    if (page == 0) {
                        isLoading.postValue(false)
                    } else {
                        isLoadingMore.postValue(false)
                    }
                }
                .subscribe({
                               suggestedResponse.postValue(it)
                           }, { e ->
                               processError.postValue(e)
                           })
        } else {
            suggestedResponse.postValue(null)
        }
    }

}