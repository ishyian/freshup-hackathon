package com.melitopolcherry.hackathon.domain.managers.events

import com.melitopolcherry.hackathon.data.models.CheckPromocodeResponse
import com.melitopolcherry.hackathon.data.models.FeesResponse
import com.melitopolcherry.hackathon.data.models.LPEventsResponse
import com.melitopolcherry.hackathon.data.models.ProvidedIds
import com.melitopolcherry.hackathon.data.models.TicketGroupsRequest
import com.melitopolcherry.hackathon.data.models.checkout.LockResponce
import com.melitopolcherry.hackathon.data.models.checkout.PaymentRequest
import com.melitopolcherry.hackathon.data.models.comprehensive.ComprehensiveResponse
import com.melitopolcherry.hackathon.data.models.event.full.EventFull
import com.melitopolcherry.hackathon.data.models.event.ocurrences.EventOccurrencesResponse
import com.melitopolcherry.hackathon.data.models.event.small.EventMap
import com.melitopolcherry.hackathon.data.models.getEventsList.LikedEventList
import com.melitopolcherry.hackathon.data.models.notification.NotificationsItem
import com.melitopolcherry.hackathon.data.models.notification.UnreadNotificationsResponse
import com.melitopolcherry.hackathon.data.models.order.GetOrdersResponse
import com.melitopolcherry.hackathon.data.models.order.OrderDetails
import com.melitopolcherry.hackathon.data.models.order.OrderRequest
import com.melitopolcherry.hackathon.data.models.order.OrderResponse
import com.melitopolcherry.hackathon.data.models.performer.PerformerDetails
import com.melitopolcherry.hackathon.data.models.protect.ProtechtResponse
import com.melitopolcherry.hackathon.data.models.responces.AutoCompleteResponse
import com.melitopolcherry.hackathon.data.models.responces.PromoCode
import com.melitopolcherry.hackathon.data.models.responces.RecentSearchResponse
import com.melitopolcherry.hackathon.data.models.responces.SuggestedShipmentResponse
import com.melitopolcherry.hackathon.data.models.suggested.GetSuggestedResponse
import com.melitopolcherry.hackathon.data.models.ticketGroups.TicketGroupsResponce
import com.melitopolcherry.hackathon.data.models.tracking.TrackedEntetiesResponse
import com.melitopolcherry.hackathon.data.models.tracking.event.TrackEventResponse
import com.melitopolcherry.hackathon.data.models.tracking.event.TrackingEventsList
import com.melitopolcherry.hackathon.data.models.tracking.performer.TrackPerformerResponse
import com.melitopolcherry.hackathon.data.models.tracking.performer.TrackingPerformersList
import com.melitopolcherry.hackathon.data.models.tracking.venue.TrackVenueResponse
import com.melitopolcherry.hackathon.data.models.tracking.venue.TrackingVenuesList
import com.melitopolcherry.hackathon.data.models.venue.VenueDetails
import com.melitopolcherry.hackathon.data.models.venue.VenuesSearchItem
import com.melitopolcherry.hackathon.data.repo.EvenzRepository
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventManager @Inject constructor(
    private val repository: EvenzRepository
) : IEventManager {

    override fun insuranceQuote(
        authorization: String,
        ticketGroupId: String,
        quantity: Int,
        source: String,
        promocode: String?
    ): Single<ProtechtResponse> {
        return repository.insuranceQuote(authorization, ticketGroupId, quantity, source, promocode)
    }

    override fun getNotifications(authorization: String): Single<List<NotificationsItem>> {
        return repository.getNotifications(authorization)
    }

    override fun sendPaymentRequest(
        authorization: String,
        request: PaymentRequest
    ): Single<OrderResponse> {
        return repository.sendPaymentRequest(authorization, request)
    }

    override fun getPush(authorization: String): Completable {
        return repository.getPush(authorization)
    }

    override fun getPushWithImage(authorization: String): Completable {
        return repository.getPushWithImage(authorization)
    }

    override fun getPushWithTitle(authorization: String): Completable {
        return repository.getPushWithTitle(authorization)
    }

    override fun readNotifications(authorization: String): Completable {
        return repository.readNotifications(authorization)
    }

    override fun getCountUnreadNotifications(
        authorization: String
    ): Single<UnreadNotificationsResponse> {
        return repository.getCountUnreadNotifications(authorization)
    }

    override fun deleteNotification(authorization: String, id: String): Completable {
        return repository.deleteNotification(authorization, id)
    }

    override fun searchEvents(
        authorization: String?, searchEventsRequest: HashMap<String, Any>,
        mainCategories: List<String>?, subCategories: List<String>?, dates: List<String>?
    ): Single<List<EventMap>> {
        return repository.searchEvents(authorization, searchEventsRequest, mainCategories, subCategories, dates)
    }

    override fun searchVenues(
        authorization: String?,
        latitude: Double?,
        longitude: Double?
    ): Single<List<VenuesSearchItem>> {
        return repository.searchVenues(authorization, latitude, longitude)
    }

    override fun searchSimilarEvents(
        authorization: String?,
        searchEventsRequest: HashMap<String, Any>,
        categories: List<String>?,
        dateStart: String?,
        dateEnd: String?
    ): Single<List<EventMap>> {
        return repository.searchSimilarEvents(
            authorization,
            searchEventsRequest,
            categories,
            dateStart,
            dateEnd
        )
    }

    override fun searchComprehensive(
        parameters: HashMap<String, Any?>
    ): Single<ComprehensiveResponse> {
        return repository.searchComprehensive(parameters)
    }

    override fun searchAutocomplete(limit: Int?, query: String): Single<AutoCompleteResponse> {
        val s = HashMap<String, String>()
        limit?.let {
            s["limit"] = limit.toString()
        }
        s["query"] = query
        return repository.searchAutocomplete(s)
    }

    override fun searchRecent(authorization: String?): Single<RecentSearchResponse> {
        return repository.searchRecent(authorization)
    }

    override fun getSuggestions(authorization: String, size: Int): Single<GetSuggestedResponse> {
        return repository.getSuggestions(authorization, size)
    }

    override fun searchInTrackedVenues(
        authorization: String, query: String, size: Int, page: Int
    ): Single<TrackingVenuesList> {
        return repository.searchInTrackedVenues(authorization, query, size, page)
    }

    override fun searchInTrackedPerformers(
        authorization: String, query: String, size: Int,
        page: Int
    ): Single<TrackingPerformersList> {
        return repository.searchInTrackedPerformers(authorization, query, size, page)
    }

    override fun searchInTrackedEvents(
        authorization: String, query: String, size: Int, page: Int
    ): Single<TrackingEventsList> {
        return repository.searchInTrackedEvents(authorization, query, size, page)
    }

    override fun getEventById(
        authorization: String?, eventId: String,
        providedId: ProvidedIds?
    ): Single<EventFull> {
        return repository.getEventById(authorization, eventId, providedId)
    }

    override fun getEventOccurrences(
        eventId: String, latitude: Double, longitude: Double, page: Int,
        size: Int
    ): Single<EventOccurrencesResponse> {
        return repository.getEventOccurrences(eventId, latitude, longitude, page, size)
    }

    override fun getEventTicketGroups(ticketGroupsRequest: TicketGroupsRequest): Single<TicketGroupsResponce> {
        return repository.getEventTicketGroups(ticketGroupsRequest)
    }

    override fun performerDetails(
        authorization: String?,
        eventId: String
    ): Single<PerformerDetails> {
        return repository.performerDetails(authorization, eventId)
    }

    override fun getTracked(authorization: String): Single<TrackedEntetiesResponse> {
        return repository.getTracked(authorization)
    }

    override fun loadPerformersEvents(
        authorization: String?,
        performerId: String,
        size: Int,
        date: String
    ): Single<LPEventsResponse> {
        return repository.loadPerformersEvents(authorization, performerId, size, date)
    }

    override fun trackPerformer(
        authorization: String, performerId: String
    ): Single<TrackPerformerResponse> {
        return repository.trackPerformer(authorization, performerId)
    }

    override fun unTrackPerformer(
        authorization: String, performerId: String
    ): Single<TrackPerformerResponse> {
        return repository.unTrackPerformer(authorization, performerId)
    }

    override fun getTrackedPerformers(
        authorization: String, page: Int, size: Int
    ): Single<TrackingPerformersList> {
        return repository.getTrackedPerformers(authorization, page, size)
    }

    override fun loadVenuesEvents(
        authorization: String?,
        venueId: String,
        size: Int,
        date: String
    ): Single<LPEventsResponse> {
        return repository.loadVenuesEvents(authorization, venueId, size, date)
    }

    override fun trackVenue(authorization: String, venueId: String): Single<TrackVenueResponse> {
        return repository.trackVenue(authorization, venueId)
    }

    override fun unTrackVenue(authorization: String, venueId: String): Single<TrackVenueResponse> {
        return repository.unTrackVenue(authorization, venueId)
    }

    override fun getTrackedVenues(
        authorization: String,
        page: Int,
        size: Int
    ): Single<TrackingVenuesList> {
        return repository.getTrackedVenues(authorization, page, size)
    }

    override fun venueDetails(authorization: String?, eventId: String): Single<VenueDetails> {
        return repository.venueDetails(authorization, eventId)
    }

    override fun trackEvent(authorization: String, eventId: String): Single<TrackEventResponse> {
        return repository.trackEvent(authorization, eventId)
    }

    override fun unTrackEvent(authorization: String, eventId: String): Single<TrackEventResponse> {
        return repository.removeTrackEvent(authorization, eventId)
    }

    override fun createOrder(authorization: String, order: OrderRequest): Single<OrderResponse> {
        return repository.createOrder(authorization, order)
    }

    override fun getOrder(authorization: String, orderId: String): Single<OrderDetails> {
        return repository.getOrder(authorization, orderId)
    }

    override fun lockTickets(
        authorization: String,
        ticketGroupId: String,
        ticketSource: String,
        quantity: Int
    ): Single<LockResponce> {
        return repository.lockTickets(authorization, ticketGroupId, ticketSource, quantity)
    }

    override fun unlockTickets(authorization: String, ticketGroupId: String): Completable {
        return repository.unlockTickets(authorization, ticketGroupId)
    }

    override fun getUserOrders(
        authorization: String,
        page: Int,
        size: Int
    ): Single<GetOrdersResponse> {
        return repository.getUserOrders(authorization, page, size)
    }

    override fun getPastUserOrders(
        authorization: String,
        page: Int,
        size: Int
    ): Single<GetOrdersResponse> {
        return repository.getPastUserOrders(authorization, page, size)
    }

    override fun checkPromocode(
        authorization: String,
        code: String
    ): Single<CheckPromocodeResponse> {
        return repository.checkPromocode(authorization, code)
    }

    override fun addPromocode(authorization: String, code: String): Single<PromoCode> {
        return repository.addPromocode(authorization, code)
    }

    override fun getMyPromocodes(authorization: String): Single<List<PromoCode>> {
        return repository.getMyPromocodes(authorization)
    }

    override fun deletePromocode(authorization: String, code: String): Completable {
        return repository.deletePromocode(authorization, code)
    }

    override fun getFees(authorization: String, amount: Int): Single<FeesResponse> {
        return repository.getFees(authorization, amount)
    }

    override fun suggestedShipment(
        authorization: String,
        ticketGroupId: String,
        ticketSource: String
    ): Single<SuggestedShipmentResponse> {
        return repository.suggestedShipment(authorization, ticketGroupId, ticketSource)
    }

    override fun getLikedEvents(
        authorization: String,
        page: Int,
        size: Int
    ): Single<LikedEventList> {
        return repository.getLikedEvents(authorization, page, size)
    }

    override fun getTrackedEvents(
        authorization: String,
        page: Int,
        size: Int
    ): Single<TrackingEventsList> {
        return repository.getTrackedEvents(authorization, page, size)
    }
}