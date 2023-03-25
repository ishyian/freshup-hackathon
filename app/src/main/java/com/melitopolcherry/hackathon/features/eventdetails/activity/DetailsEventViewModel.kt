package com.melitopolcherry.hackathon.features.eventdetails.activity

import androidx.lifecycle.MutableLiveData
import com.melitopolcherry.hackathon.base.BaseViewModel
import com.melitopolcherry.hackathon.data.DateHelper
import com.melitopolcherry.hackathon.data.livedata.AuthTokenLiveData
import com.melitopolcherry.hackathon.data.livedata.EventsFilters
import com.melitopolcherry.hackathon.data.livedata.EventsLiveData
import com.melitopolcherry.hackathon.data.models.ProvidedIds
import com.melitopolcherry.hackathon.data.models.SimilarEventItem
import com.melitopolcherry.hackathon.data.models.TicketGroupsRequest
import com.melitopolcherry.hackathon.data.models.event.TicketGroup
import com.melitopolcherry.hackathon.data.models.event.full.EventFull
import com.melitopolcherry.hackathon.domain.managers.account.IAccountManager
import com.melitopolcherry.hackathon.domain.managers.events.IEventManager
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class DetailsEventViewModel @Inject constructor(
    private val accountManager: IAccountManager,
    private val eventsManager: IEventManager
) : BaseViewModel() {

    var authToken: AuthTokenLiveData? = AuthTokenLiveData.instance
    var event: EventFull? = null
    var ticketGroups: List<TicketGroup>? = null
    var userLocation: LatLng? = null

    val isLoading: MutableLiveData<Boolean> = MutableLiveData()
    val isTrackLoading: MutableLiveData<Boolean> = MutableLiveData()
    val isPriceLoading: MutableLiveData<Boolean> = MutableLiveData()

    val setTrackImage: MutableLiveData<Boolean> = MutableLiveData()
    val processError: MutableLiveData<Throwable> = MutableLiveData()
    val showMessage: MutableLiveData<String> = MutableLiveData()
    val setUpEvent: MutableLiveData<EventFull> = MutableLiveData()
    val setVideo: MutableLiveData<String?> = MutableLiveData()
    val setUpTicketGroup: MutableLiveData<List<TicketGroup>?> = MutableLiveData()
    val setSimilarEvents: MutableLiveData<List<SimilarEventItem>> = MutableLiveData()
    var eventsLiveData: EventsLiveData? = EventsLiveData.instance

    init {
        userLocation = accountManager.getLocation()
    }

    private fun changeTrackValue() {
        EventsLiveData.instance.getEvents().value?.forEach { e ->
            if (e.id == event?.id) {
                e.isTracked = event?.isTracked
                return@forEach
            }
        }
    }

    fun getEventById(id: String, ticketGroupId: ProvidedIds?) {
        eventsManager.getEventById(authToken?.value?.accessToken, id, ticketGroupId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { isLoading.postValue(true) }
            .doOnSuccess { isLoading.postValue(false) }
            .doOnError { isLoading.postValue(false) }
            .subscribe({
                           event = it
                           event?.providedIds?.let { id ->
                               getEventTicketGroups(id)
                           }
                           setVideo.postValue(it.videoUrl)
                           searchSimilarEvents()
                           setUpEvent.postValue(it)
                       }, { e ->
                           processError.postValue(e)
                       })
    }

    fun getEventTicketGroups(ids: ProvidedIds) {
        eventsManager.getEventTicketGroups(TicketGroupsRequest(ids))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { isLoading.postValue(true) }
            .doOnSuccess { isLoading.postValue(false) }
            .doOnError { isLoading.postValue(false) }
            .subscribe({
                           ticketGroups = it.ticketGroups!!
                           setUpTicketGroup.postValue(it.ticketGroups)
                       }, { e ->
                           setUpTicketGroup.postValue(null)
                           processError.postValue(e)
                       })
    }

    fun searchSimilarEvents() {
        val startAndEndDates = DateHelper.getDatesStartAndEndDateInRange(30)
        val configurations = EventsFilters()
        configurations.radius = EventsFilters.RADIUS_SIMILAR_EVENTS_SEARCH
        configurations.latitude = event?.venue?.latitude.toString()
        configurations.longitude = event?.venue?.longitude.toString()

        eventsManager.searchSimilarEvents(
            authorization = authToken?.value?.accessToken,
            searchEventsRequest = configurations.toHashMap(),
            categories = listOf(event?.eventCategory ?: ""),
            dateStart = startAndEndDates.first,
            dateEnd = startAndEndDates.second
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ eventList ->
                           setSimilarEvents.postValue(eventList.map { event ->
                               SimilarEventItem(
                                   id = event.id,
                                   title = event.title,
                                   startDate = event.startDate,
                                   imageUrl = event.imageUrl,
                                   venue = event.venue
                               )
                           })
                       }, { error ->
                           Timber.e(error)
                       })
    }

    fun trackEvent() {
        setTrackImage.postValue(true)
        eventsManager.trackEvent(authToken?.value?.accessToken!!, event?.id!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { isTrackLoading.postValue(true) }
            .doOnSuccess { isTrackLoading.postValue(false) }
            .doOnError { isTrackLoading.postValue(false) }
            .subscribe({
                           changeTrackValue()
                       }, { e ->
                           setTrackImage.postValue(false)
                           processError.postValue(e)
                       })
    }

    fun unTrackEvent() {
        setTrackImage.postValue(false)
        eventsManager.unTrackEvent(authToken?.value?.accessToken!!, event?.id!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { isTrackLoading.postValue(true) }
            .doOnSuccess { isTrackLoading.postValue(false) }
            .doOnError { isTrackLoading.postValue(false) }
            .subscribe({
                           changeTrackValue()
                       }, { e ->
                           setTrackImage.postValue(true)
                           processError.postValue(e)
                       })
    }
}