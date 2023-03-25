package com.melitopolcherry.hackathon.features.maps.home.presentation

import androidx.lifecycle.MutableLiveData
import com.melitopolcherry.hackathon.base.BaseViewModel
import com.melitopolcherry.hackathon.data.livedata.AuthTokenLiveData
import com.melitopolcherry.hackathon.data.livedata.EventsFilters
import com.melitopolcherry.hackathon.data.livedata.EventsLiveData
import com.melitopolcherry.hackathon.data.livedata.MarkersLiveData
import com.melitopolcherry.hackathon.data.livedata.VenuesLiveData
import com.melitopolcherry.hackathon.data.models.event.small.EventMap
import com.melitopolcherry.hackathon.data.models.venue.VenuesSearchItem
import com.melitopolcherry.hackathon.domain.managers.account.IAccountManager
import com.melitopolcherry.hackathon.domain.managers.events.IEventManager
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class MapHomeViewModel @Inject constructor(
    private val eventsManager: IEventManager,
    private val accountManager: IAccountManager
) : BaseViewModel() {

    var authToken: AuthTokenLiveData? = AuthTokenLiveData.instance
    var eventsLiveData: EventsLiveData? = EventsLiveData.instance
    var venuesLiveData: VenuesLiveData? = VenuesLiveData.instance
    var markersLiveData: MarkersLiveData? = MarkersLiveData.instance

    val isLoading: MutableLiveData<Boolean> = MutableLiveData()
    val processError: MutableLiveData<Throwable> = MutableLiveData()

    fun searchEvents(configurations: EventsFilters) {
        eventsManager.searchEvents(
            authToken?.value?.accessToken, configurations.toHashMap(),
            configurations.mainCategoriesString, configurations.subCategoriesString, configurations.datesString
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                if (configurations.page == 0) {
                    isLoading.postValue(true)
                }
            }
            .doOnSuccess {
                if (configurations.page == 0) {
                    isLoading.postValue(false)
                }
            }
            .doOnError {
                if (configurations.page == 0) {
                    isLoading.postValue(false)
                }
            }.subscribe({ eventList ->
                            eventsLiveData?.page = configurations.page
                            if (configurations.page > 0) {
                                val array = arrayListOf<EventMap>()
                                array.addAll(eventsLiveData?.getEvents()?.value!!)
                                eventList.forEach {
                                    if (!array.contains(it)) {
                                        array.add(it)
                                    }
                                }
                                eventsLiveData?.getEvents()?.value = array
                            } else {
                                eventsLiveData?.getEvents()?.value = eventList
                            }
                        }, { e ->
                            processError.postValue(e)
                        })
    }

    fun searchVenues(configurations: EventsFilters) {
        eventsManager.searchVenues(
            authorization = authToken?.value?.accessToken,
            latitude = configurations.latitude?.toDouble(),
            longitude = configurations.longitude?.toDouble()
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                if (configurations.page == 0) {
                    isLoading.postValue(true)
                }
            }
            .doOnSuccess {
                if (configurations.page == 0) {
                    isLoading.postValue(false)
                }
            }
            .doOnError {
                if (configurations.page == 0) {
                    isLoading.postValue(false)
                }
            }.subscribe({ venuesList ->
                            venuesLiveData?.page = configurations.page
                            if (configurations.page > 0) {
                                val array = arrayListOf<VenuesSearchItem>()
                                array.addAll(venuesLiveData?.getVenues()?.value!!)
                                venuesList.forEach {
                                    if (!array.contains(it)) {
                                        array.add(it)
                                    }
                                }
                                venuesLiveData?.getVenues()?.value = array
                            } else {
                                venuesLiveData?.getVenues()?.value = venuesList
                            }
                        }, { e ->
                            processError.postValue(e)
                        })
    }

}