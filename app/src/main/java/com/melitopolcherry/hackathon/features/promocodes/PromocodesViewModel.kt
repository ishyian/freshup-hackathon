package com.melitopolcherry.hackathon.features.promocodes

import androidx.lifecycle.MutableLiveData
import com.melitopolcherry.hackathon.adapters.callbacks.AddPromocodeCallback
import com.melitopolcherry.hackathon.base.BaseViewModel
import com.melitopolcherry.hackathon.data.livedata.AuthTokenLiveData
import com.melitopolcherry.hackathon.data.models.login.User
import com.melitopolcherry.hackathon.data.models.responces.PromoCode
import com.melitopolcherry.hackathon.domain.managers.events.IEventManager
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class PromocodesViewModel @Inject constructor(
    private val eventManager: IEventManager
) : BaseViewModel(), AddPromocodeCallback {
    private var listOfCodes = arrayListOf<PromoCode>()

    var user: User? = null
    var authToken: AuthTokenLiveData? = AuthTokenLiveData.instance

    val isLoading: MutableLiveData<Boolean> = MutableLiveData()
    val showEmptyState: MutableLiveData<Boolean> = MutableLiveData()
    val processError: MutableLiveData<Throwable> = MutableLiveData()
    val setData: MutableLiveData<List<PromoCode>> = MutableLiveData()

    init {
        getPromos()
    }

    private fun getPromos() {
        if (authToken?.value?.accessToken != null) {
            eventManager.getMyPromocodes(authToken?.value?.accessToken!!)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { isLoading.postValue(true) }
                .doOnSuccess { isLoading.postValue(false) }
                .doOnError { isLoading.postValue(false) }
                .subscribe({
                               listOfCodes.clear()
                               listOfCodes.addAll(it)

                               setData.postValue(listOfCodes)
                           }, { e ->
                               processError.postValue(e)
                           })
        } else {
            showEmptyState.postValue(true)
        }
    }

    override val addPromocodeCallback = object : AddPromocodeCallback.Callback {
        override fun onAdded(promocode: PromoCode) {
            listOfCodes.add(promocode)
            setData.postValue(listOfCodes)
        }
    }

    fun deletePromo(code: PromoCode) {
        listOfCodes.remove(code)
        if (listOfCodes.size == 0) {
            showEmptyState.postValue(true)
        }
        eventManager.deletePromocode(authToken?.value?.accessToken!!, code.id!!)
            .subscribe({}, { e ->
                processError.postValue(e)
            })
    }
}