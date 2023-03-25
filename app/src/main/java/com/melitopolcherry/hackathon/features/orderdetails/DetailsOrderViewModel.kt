package com.melitopolcherry.hackathon.features.orderdetails

import androidx.lifecycle.MutableLiveData
import com.melitopolcherry.hackathon.base.BaseViewModel
import com.melitopolcherry.hackathon.data.livedata.AuthTokenLiveData
import com.melitopolcherry.hackathon.data.models.event.full.Venue
import com.melitopolcherry.hackathon.data.models.order.OrderDetails
import com.melitopolcherry.hackathon.data.models.weather.hourly.HourlyWeatherResponse
import com.melitopolcherry.hackathon.domain.managers.account.IAccountManager
import com.melitopolcherry.hackathon.domain.managers.events.IEventManager
import com.melitopolcherry.hackathon.domain.managers.geoInfoProvider.IGeoInfoManager
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class DetailsOrderViewModel @Inject constructor(
    private val geoInfoManager: IGeoInfoManager,
    private val accountManager: IAccountManager,
    private val eventsManager: IEventManager
) : BaseViewModel() {
    var authToken: AuthTokenLiveData? = AuthTokenLiveData.instance
    var order: OrderDetails? = null
    var userLocation: LatLng? = null
    var eventLocation: LatLng? = null
    var ticket: List<String>? = null

    val isLoading: MutableLiveData<Boolean> = MutableLiveData()
    val processError: MutableLiveData<Throwable> = MutableLiveData()
    val setUpViews: MutableLiveData<OrderDetails> = MutableLiveData()
    val setWeather: MutableLiveData<HourlyWeatherResponse> = MutableLiveData()
    val showMessage: MutableLiveData<String> = MutableLiveData()

    init {
        userLocation = accountManager.getLocation()
    }

    fun getOrderDetails(orderId: String) {
        println("🍀 $orderId")
        eventsManager.getOrder(authToken?.value?.accessToken!!, orderId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { isLoading.postValue(true) }
            .doOnSuccess { isLoading.postValue(false) }
            .doOnError { isLoading.postValue(false) }
            .subscribe({
                           order = it
                           ticket = it.ticket
                           setUpViews.postValue(it)
                           getHourlyWeather(it.venue)
                       }, { e ->
                           processError.postValue(e)
                       })
    }

    private fun getHourlyWeather(venue: Venue?) {
        geoInfoManager.getHourlyWeather(
            venue?.latitude?.toInt().toString(),
            venue?.longitude?.toInt().toString()
        ).subscribe({
                                setWeather.postValue(it)
                            }, { e ->
                                processError.postValue(e)
                            })
    }
}