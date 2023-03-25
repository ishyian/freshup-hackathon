package com.melitopolcherry.hackathon.features.bottom

import androidx.lifecycle.MutableLiveData
import com.melitopolcherry.hackathon.adapters.callbacks.AddPromocodeCallback
import com.melitopolcherry.hackathon.base.BaseViewModel
import com.melitopolcherry.hackathon.data.livedata.AuthTokenLiveData
import com.melitopolcherry.hackathon.data.models.responces.PromoCode
import com.melitopolcherry.hackathon.domain.managers.events.IEventManager
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class PromosViewModel @Inject constructor(
    private val eventManager: IEventManager
) : BaseViewModel(), AddPromocodeCallback {

    var authToken: AuthTokenLiveData? = AuthTokenLiveData.instance
    var listOfCodes = arrayListOf<PromoCode>()

    val isLoading: MutableLiveData<Boolean> = MutableLiveData()
    val dismissView: MutableLiveData<Boolean> = MutableLiveData()
    val setData: MutableLiveData<List<PromoCode>> = MutableLiveData()
    val processError: MutableLiveData<Throwable> = MutableLiveData()

    fun getMyCodes() {
        eventManager.getMyPromocodes(authToken?.value?.accessToken!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { isLoading.postValue(true) }
            .doOnSuccess { isLoading.postValue(false) }
            .doOnError { isLoading.postValue(false) }
            .subscribe({
                listOfCodes.clear()
                listOfCodes.addAll(it)
                setData.postValue(it)
            }, {
                processError.postValue(it)
            })
    }

    fun deletePromo(code: PromoCode) {
        eventManager.deletePromocode(authToken?.value?.accessToken!!, code.id!!)
            .subscribe({}, { e ->
                processError.postValue(e)
            })
    }

    override val addPromocodeCallback = object : AddPromocodeCallback.Callback {
        override fun onAdded(promocode: PromoCode) {
            listOfCodes.add(promocode)
            setData.postValue(listOfCodes)
        }
    }
}