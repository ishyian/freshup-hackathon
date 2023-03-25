package com.melitopolcherry.hackathon.features.eventdetails

import androidx.lifecycle.MutableLiveData
import com.melitopolcherry.hackathon.base.BaseViewModel
import com.melitopolcherry.hackathon.data.DateHelper
import com.melitopolcherry.hackathon.data.livedata.AuthTokenLiveData
import com.melitopolcherry.hackathon.data.livedata.EventsFilters
import com.melitopolcherry.hackathon.data.livedata.EventsLiveData
import com.melitopolcherry.hackathon.data.models.ProvidedIds
import com.melitopolcherry.hackathon.data.models.SimilarEventItem
import com.melitopolcherry.hackathon.data.models.TicketGroupsRequest
import com.melitopolcherry.hackathon.data.models.comprehensive.PerformersItem
import com.melitopolcherry.hackathon.data.models.event.TicketGroup
import com.melitopolcherry.hackathon.data.models.event.full.EventFull
import com.melitopolcherry.hackathon.data.models.event.small.EventMap
import com.melitopolcherry.hackathon.data.models.login.User
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
    private val eventsManager: IEventManager,
    private val accountManager: IAccountManager
) : BaseViewModel() {

    var authToken: AuthTokenLiveData? = AuthTokenLiveData.instance
    var eventsLiveData: EventsLiveData? = EventsLiveData.instance
    var ticketGroups: List<TicketGroup>? = null
    var userLocation: LatLng? = null
    var selectedEvent: EventMap? = null
    var fullEvent: EventFull? = null
    var user: User? = null

    val isLoading: MutableLiveData<Boolean> = MutableLiveData()
    val isPriceLoading: MutableLiveData<Boolean> = MutableLiveData()
    val setTrackImage: MutableLiveData<Boolean> = MutableLiveData()
    val setPerformerList: MutableLiveData<ArrayList<PerformersItem>> = MutableLiveData()
    val setTickets: MutableLiveData<List<TicketGroup>?> = MutableLiveData()
    val setSimilarEvents: MutableLiveData<List<SimilarEventItem>> = MutableLiveData()
    val setVideo: MutableLiveData<String?> = MutableLiveData()
    val processError: MutableLiveData<Throwable> = MutableLiveData()

    init {
        userLocation = accountManager.getLocation()
    }

    fun getEventById(id: String, ticketGroupId: ProvidedIds?) {
        val providedIds =
            if (ticketGroupId?.ticketevolution == null && ticketGroupId?.ticketnetwork == null) {
                null
            } else {
                ticketGroupId
            }
        eventsManager.getEventById(authToken?.value?.accessToken, id, providedIds)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { isLoading.postValue(true) }
            .doOnSuccess { isLoading.postValue(false) }
            .doOnError { isLoading.postValue(false) }
            .subscribe(::onEventReceived, ::onEventErrorReceived)
    }

    fun getEventTicketGroups(ids: ProvidedIds?) {
        eventsManager.getEventTicketGroups(TicketGroupsRequest(ids))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { isPriceLoading.postValue(true) }
            .doOnSuccess { isPriceLoading.postValue(false) }
            .doOnError { isPriceLoading.postValue(false) }
            .subscribe({
                           ticketGroups = it.ticketGroups!!
                           setTickets.postValue(ticketGroups)
                       }, { error ->
                           setTickets.postValue(null)
                           Timber.e(error)
                       })
    }

    fun trackEvent() {
        setTrackImage.postValue(true)
        eventsManager.trackEvent(authToken?.value?.accessToken!!, selectedEvent?.id!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({}, { e ->
                setTrackImage.postValue(false)
                changeTrackValue()
                processError.postValue(e)
            })
    }

    fun unTrackEvent() {
        setTrackImage.postValue(false)
        eventsManager.unTrackEvent(authToken?.value?.accessToken!!, selectedEvent?.id!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({}, { e ->
                setTrackImage.postValue(true)
                changeTrackValue()
                processError.postValue(e)
            })
    }

    fun searchSimilarEvents() {
        val startAndEndDates = DateHelper.getDatesStartAndEndDateInRange(30)
        val configurations = EventsFilters()
        configurations.radius = EventsFilters.RADIUS_SIMILAR_EVENTS_SEARCH
        configurations.latitude = selectedEvent?.venue?.latitude.toString()
        configurations.longitude = selectedEvent?.venue?.longitude.toString()

        eventsManager.searchSimilarEvents(
            authorization = authToken?.value?.accessToken,
            searchEventsRequest = configurations.toHashMap(),
            categories = listOf(selectedEvent?.eventCategory ?: ""),
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

    private fun onEventReceived(it: EventFull) {
        fullEvent = it
        if (selectedEvent?.providedIds?.ticketnetwork == null && selectedEvent?.providedIds?.ticketevolution == null) {
            getEventTicketGroups(it.providedIds)
        }
        setVideo.value = it.videoUrl
        val array = arrayListOf<PerformersItem>()
        println("🙌🏻" + fullEvent?.performers?.size)
        if (fullEvent?.performers != null) {
            array.addAll(fullEvent?.performers!!)
        }
        if (selectedEvent?.venue != null) {
            val venuePerf =
                PerformersItem(
                    selectedEvent?.venue?.imageUrl,
                    selectedEvent?.venue?.id,
                    selectedEvent?.venue?.name,
                    selectedEvent?.venue?.name,
                    selectedEvent?.venue?.getFullPlace(),
                    selectedEvent?.venue,
                    1
                )

            array.add(venuePerf)
        }
        setPerformerList.postValue(array)
    }

    private fun onEventErrorReceived(e: Throwable) {
        setTickets.postValue(null)
        processError.postValue(e)
    }

    private fun changeTrackValue() {
        eventsLiveData?.getEvents()?.value?.forEach { event ->
            if (event.id == selectedEvent?.id) {
                event.isTracked = selectedEvent?.isTracked
                return@forEach
            }
        }
    }
}