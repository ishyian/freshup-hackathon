package com.melitopolcherry.hackathon.features.bottom

import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.melitopolcherry.hackathon.base.BaseViewModel
import com.melitopolcherry.hackathon.data.livedata.AuthTokenLiveData
import com.melitopolcherry.hackathon.data.models.City
import com.melitopolcherry.hackathon.data.models.event.full.EventFull
import com.melitopolcherry.hackathon.data.models.event.ocurrences.OccurrencesItem
import com.melitopolcherry.hackathon.domain.managers.events.IEventManager
import com.melitopolcherry.hackathon.domain.managers.geoInfoProvider.IGeoInfoManager
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class OccurrencesViewModel @Inject constructor(
    private val geoInfoManager: IGeoInfoManager,
    private val eventsManager: IEventManager
) : BaseViewModel() {

    private var listOfDates: List<OccurrencesItem>? = null
    var layoutManager: LinearLayoutManager? = null
    var selectedCity: City? = null

    private var page = 0
    private var size = 15
    private var selectedPosition: OccurrencesItem? = null
    var event: EventFull? = null
    var authToken: AuthTokenLiveData? = AuthTokenLiveData.instance

    val isLoading: MutableLiveData<Boolean> = MutableLiveData()
    val locationSelected: MutableLiveData<City> = MutableLiveData()
    val setOccurrences: MutableLiveData<List<OccurrencesItem>> = MutableLiveData()
    val setCities: MutableLiveData<List<City>> = MutableLiveData()
    val reloadAdapter: MutableLiveData<Boolean> = MutableLiveData()
    val animateToSelectedPosition: MutableLiveData<OccurrencesItem> = MutableLiveData()
    val dismissView: MutableLiveData<Boolean> = MutableLiveData()
    val processError: MutableLiveData<Throwable> = MutableLiveData()

    fun getEventOccurrences() {
        var latitude = selectedCity?.latitude
        var longitude = selectedCity?.longitude
        if (latitude == null || longitude == null) {
            latitude = event?.venue?.latitude
            longitude = event?.venue?.longitude
        }
        if (latitude != null || longitude != null) {
            eventsManager.getEventOccurrences(
                event?.id!!,
                latitude!!,
                longitude!!,
                page,
                size
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    isLoading.postValue(true)
                }
                .doOnSuccess { isLoading.postValue(false) }
                .doOnError { isLoading.postValue(false) }
                .subscribe({
                               listOfDates = it.occurrences
                               listOfDates?.let { setOccurrences.postValue(it) }
                           }, { e ->
                               processError.postValue(e)
                           })
        }
    }

    fun searchCities(text: String?) {
        if (text.isNullOrEmpty()) {
            reloadAdapter.postValue(true)
            listOfDates?.let { setOccurrences.postValue(it) }
        } else {
            geoInfoManager.searchCities(text).subscribeOn(Schedulers.io())
                .doOnSubscribe { isLoading.postValue(true) }
                .doOnSuccess { isLoading.postValue(false) }
                .doOnError { isLoading.postValue(false) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                               Timber.d("🤢$result")
                               val list = arrayListOf<City>()
                               result.predictions?.forEach {
                                   val city = City()
                                   city.name =
                                       it.structuredFormatting?.mainText
                                   city.region =
                                       it.structuredFormatting?.secondaryText
                                   city.cityId = it.placeId
                                   list.add(city)
                               }
                               setCities.postValue(list)
                           }, { e ->
                               processError.postValue(e)
                           })
        }
    }

    fun getLocationByCity(city: City?) {
        if (city?.cityId != null) {
            geoInfoManager.getCityLocation(city.cityId!!)
                .doOnSubscribe { isLoading.postValue(true) }
                .doOnSuccess { isLoading.postValue(false) }
                .doOnError { isLoading.postValue(false) }
                .subscribe({ result ->
                               val results = result.results
                               val location = results?.get(0)?.geometry?.location

                               city.latitude = location?.lat
                               city.longitude = location?.lng
                               if (city.latitude != null && city.longitude != null) {
                                   selectedCity = city
                                   locationSelected.postValue(selectedCity)
                                   getEventOccurrences()
                               }
                           }, { e ->
                               processError.postValue(e)
                           })
        } else {
            Timber.d("🤢RELOAD ${city?.cityId}")
        }
    }

    val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val visibleItemPosition = layoutManager?.findFirstCompletelyVisibleItemPosition()
            visibleItemPosition?.let { position ->
                if (position >= 0 && position < listOfDates?.size!! && listOfDates?.isNotEmpty()!!) {
                    val currentSelected = listOfDates?.get(position)
                    if (selectedPosition == null) {
                        selectedPosition = currentSelected
                        selectedPosition?.let { animateToSelectedPosition.postValue(it) }
                    } else if (selectedPosition?.latitude != currentSelected?.latitude &&
                        selectedPosition?.longitude != currentSelected?.longitude
                    ) {
                        selectedPosition = currentSelected
                        selectedPosition?.let { animateToSelectedPosition.postValue(it) }
                    }
                }
            }
        }
    }
}