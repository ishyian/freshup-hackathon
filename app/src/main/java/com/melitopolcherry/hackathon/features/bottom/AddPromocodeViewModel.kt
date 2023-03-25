package com.melitopolcherry.hackathon.features.bottom

import androidx.lifecycle.MutableLiveData
import com.melitopolcherry.hackathon.adapters.callbacks.AddPromocodeCallback
import com.melitopolcherry.hackathon.base.BaseViewModel
import com.melitopolcherry.hackathon.data.DateHelper
import com.melitopolcherry.hackathon.data.livedata.AuthTokenLiveData
import com.melitopolcherry.hackathon.domain.managers.events.IEventManager
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class AddPromocodeViewModel @Inject constructor(
    private val eventManager: IEventManager
) : BaseViewModel() {

    var addPromocodeCallback: AddPromocodeCallback.Callback? = null
    var authToken: AuthTokenLiveData? = AuthTokenLiveData.instance

    val isLoading: MutableLiveData<Boolean> = MutableLiveData()
    val dismissView: MutableLiveData<Boolean> = MutableLiveData()
    val processError: MutableLiveData<Throwable> = MutableLiveData()
    val showMessage: MutableLiveData<String> = MutableLiveData()

    fun checkPromoCode(code: String) {
        authToken?.value?.accessToken?.let {
            eventManager.checkPromocode(it, code)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { isLoading.postValue(true) }
                .doOnSuccess { isLoading.postValue(false) }
                .doOnError { isLoading.postValue(false) }
                .subscribe({
                               addPromoCode(code)
                           }, { e ->
                               processError.postValue(e)
                           })
        }
    }

    private fun addPromoCode(code: String) {
        authToken?.value?.accessToken?.let {
            eventManager.addPromocode(it, code)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { isLoading.postValue(true) }
                .doOnSuccess { isLoading.postValue(false) }
                .doOnError { isLoading.postValue(false) }
                .subscribe({ pc ->
                               if (DateHelper.checkPromoCodeDate(pc.validUntil!!)) {
                                   addPromocodeCallback?.onAdded(pc)
                                   dismissView.postValue(true)
                               } else {
                                   showMessage.postValue("Promo code expired")
                               }
                           }, { e ->
                               processError.postValue(e)
                           })
        }
    }
}