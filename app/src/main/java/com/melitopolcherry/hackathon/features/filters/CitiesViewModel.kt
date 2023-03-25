package com.melitopolcherry.hackathon.features.filters

import android.view.View
import androidx.lifecycle.MutableLiveData
import com.melitopolcherry.hackathon.adapters.cities_section.CitiesSection
import com.melitopolcherry.hackathon.adapters.cities_section.SectionedItemCallback
import com.melitopolcherry.hackathon.base.BaseViewModel
import com.melitopolcherry.hackathon.data.livedata.EventsFilters
import com.melitopolcherry.hackathon.data.livedata.FiltersConfigLiveData
import com.melitopolcherry.hackathon.data.models.City
import com.melitopolcherry.hackathon.domain.managers.account.IAccountManager
import com.melitopolcherry.hackathon.domain.managers.geoInfoProvider.IGeoInfoManager
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class CitiesViewModel @Inject constructor(
    private val geoInfoManager: IGeoInfoManager,
    private val accountManager: IAccountManager
) : BaseViewModel(), SectionedItemCallback {

    private var userLocation: LatLng? = null
    private var filtersConfigLiveData: FiltersConfigLiveData = FiltersConfigLiveData.instance
    private var listOfNearCities: ArrayList<City>? = arrayListOf()
    private var listOfRecentlyCities: ArrayList<City>? = arrayListOf()
    private var nearSection: CitiesSection? = null
    private var recentlySection: CitiesSection? = null
    private var searchCitySection: CitiesSection? = null

    val isLoading: MutableLiveData<Boolean> = MutableLiveData()
    val dismiss: MutableLiveData<Boolean> = MutableLiveData()
    val showEmptyState: MutableLiveData<Boolean> = MutableLiveData()
    val addSection: MutableLiveData<CitiesSection> = MutableLiveData()
    val setSections: MutableLiveData<CitiesSection?> = MutableLiveData()
    val processError: MutableLiveData<Throwable> = MutableLiveData()
    val showMessage: MutableLiveData<Boolean> = MutableLiveData()

    init {
        userLocation = accountManager.getLocation()
    }

    fun searchCities(text: String) {
        geoInfoManager.searchCities(text)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { isLoading.postValue(true) }
            .doOnSuccess { isLoading.postValue(false) }
            .doOnError { isLoading.postValue(false) }
            .observeOn(AndroidSchedulers.mainThread()).subscribe({ result ->
                val list = arrayListOf<City>()
                result.predictions?.forEach {
                    try {
                        if (it.structuredFormatting != null) {
                            val city = City()
                            city.name = it.structuredFormatting?.mainText
                            city.region = it.structuredFormatting?.secondaryText
                            city.cityId = it.placeId
                            list.add(city)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                if (list.isNotEmpty()) {
                    searchCitySection = CitiesSection("", text, list)
                    setSections.postValue(searchCitySection)
                    showEmptyState.postValue(false)
                } else {
                    showEmptyState.postValue(true)
                }
            }, { e ->
                processError.postValue(e)
            })
    }

    fun getRecentlyCities() {
        val listRecent: List<City>? = accountManager.getRecentlyCities()
        if (listRecent != null && listRecent.isNotEmpty()) {
            listOfRecentlyCities?.clear()
            listOfRecentlyCities?.addAll(listRecent)
            listOfRecentlyCities?.let {
                recentlySection = CitiesSection("Recently searched", "", it)
                setSections.postValue(recentlySection)
            }
        } else {
            setSections.postValue(null)
        }
    }

    fun getNearCities() {
        if (listOfNearCities != null && listOfNearCities?.isNotEmpty()!!) {
            nearSection = CitiesSection("Near places", "", listOfNearCities!!)
            addSection.postValue(nearSection!!)
        } else {
            loadNearCities()
        }
    }

    private fun loadNearCities() {
        userLocation?.let {
            geoInfoManager.getNearCities(
                userLocation?.latitude!! - 1,
                userLocation?.latitude!! + 1, userLocation?.longitude!! + 1,
                userLocation?.longitude!! - 1, "de", "dimius555"
            )
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { isLoading.postValue(true) }
                .doOnSuccess { isLoading.postValue(false) }
                .doOnError { isLoading.postValue(false) }
                .subscribe({ result ->
                    result.geonames?.forEach {
                        listOfNearCities?.add(
                            City(name = it.name!!, latitude = it.lat, longitude = it.lng)
                        )
                    }
                    if (listOfNearCities != null && listOfNearCities?.isNotEmpty()!!) {
                        nearSection = CitiesSection("Near places", "", listOfNearCities!!)
                        addSection.postValue(nearSection!!)
                    }
                }, { e ->
                    processError.postValue(e)
                })
        }
    }

    private fun getLocationByCity(city: City?) {
        if (city?.cityId != null) {
            geoInfoManager.getCityLocation(city.cityId!!)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { isLoading.postValue(true) }
                .doOnSuccess { isLoading.postValue(false) }
                .doOnError { isLoading.postValue(false) }
                .subscribe({ result ->
                    val results = result.results
                    val location = results?.get(0)?.geometry?.location

                    city.latitude = location?.lat
                    city.longitude = location?.lng
                    if (city.latitude != null && city.longitude != null) {

                        accountManager.saveRecentlyCities(city)

                        val eventsFiltersConfig: EventsFilters? =
                            if (filtersConfigLiveData.getFilters().value == null) {
                                EventsFilters()
                            } else {
                                filtersConfigLiveData.getFilters().value
                            }
                        eventsFiltersConfig?.selectedCity = city
                        eventsFiltersConfig?.page = 0
                        eventsFiltersConfig?.latitude = city.latitude.toString()
                        eventsFiltersConfig?.longitude = city.longitude.toString()
                        filtersConfigLiveData.getFilters().value = eventsFiltersConfig
                        dismiss.postValue(true)
                    } else {
                        showMessage.postValue(true)
                    }
                }, { e ->
                    processError.postValue(e)
                })
        } else {
            println("🤢RELOAD ${city?.cityId}")
        }
    }

    override val sectionedItemCallback = object : SectionedItemCallback.Callback {
        override fun onClick(it: View, city: City) {
            accountManager.saveRecentlyCities(city)

            if (city.latitude == null || city.longitude == null) {
                getLocationByCity(city)
            } else {
                val eventsFiltersConfig: EventsFilters? =
                    if (filtersConfigLiveData.getFilters().value == null) {
                        EventsFilters()
                    } else {
                        filtersConfigLiveData.getFilters().value
                    }
                eventsFiltersConfig?.selectedCity = city
                eventsFiltersConfig?.page = 0
                eventsFiltersConfig?.latitude = city.latitude.toString()
                eventsFiltersConfig?.longitude = city.longitude.toString()
                filtersConfigLiveData.getFilters().value = eventsFiltersConfig
                dismiss.postValue(true)
            }
        }
    }
}