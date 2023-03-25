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
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import javax.inject.Singleton

@Singleton
interface IEventManager {

    fun insuranceQuote(
        authorization: String,
        ticketGroupId: String,
        quantity: Int,
        source: String,
        promocode: String?
    ): Single<ProtechtResponse>

    //region Notifications
    fun getNotifications(authorization: String): Single<List<NotificationsItem>>

    fun getPush(authorization: String): Completable
    fun getPushWithImage(authorization: String): Completable
    fun getPushWithTitle(authorization: String): Completable
    fun sendPaymentRequest(
        authorization: String,
        request: PaymentRequest
    ): Single<OrderResponse>

    fun readNotifications(authorization: String): Completable
    fun getCountUnreadNotifications(authorization: String): Single<UnreadNotificationsResponse>
    fun deleteNotification(authorization: String, id: String): Completable
    //endregion

    //region Events
    fun getEventById(
        authorization: String?,
        eventId: String,
        providedId: ProvidedIds?
    ): Single<EventFull>

    fun getEventOccurrences(
        eventId: String,
        latitude: Double,
        longitude: Double,
        page: Int,
        size: Int
    ): Single<EventOccurrencesResponse>

    fun getEventTicketGroups(ticketGroupsRequest: TicketGroupsRequest): Single<TicketGroupsResponce>

    fun trackEvent(authorization: String, eventId: String): Single<TrackEventResponse>
    fun unTrackEvent(authorization: String, eventId: String): Single<TrackEventResponse>
    fun getLikedEvents(authorization: String, page: Int, size: Int): Single<LikedEventList>
    fun getTrackedEvents(authorization: String, page: Int, size: Int): Single<TrackingEventsList>

    fun getTracked(authorization: String): Single<TrackedEntetiesResponse>
    //endregion

    //region Performer
    fun performerDetails(
        authorization: String?,
        eventId: String
    ): Single<PerformerDetails>

    fun loadPerformersEvents(
        authorization: String?,
        performerId: String,
        size: Int,
        date: String
    ): Single<LPEventsResponse>

    fun trackPerformer(authorization: String, performerId: String): Single<TrackPerformerResponse>
    fun unTrackPerformer(authorization: String, performerId: String): Single<TrackPerformerResponse>
    fun getTrackedPerformers(
        authorization: String,
        page: Int,
        size: Int
    ): Single<TrackingPerformersList>
    //endregion

    //region Venue
    fun venueDetails(
        authorization: String?,
        eventId: String
    ): Single<VenueDetails>

    fun loadVenuesEvents(
        authorization: String?,
        venueId: String,
        size: Int,
        date: String
    ): Single<LPEventsResponse>

    fun trackVenue(authorization: String, venueId: String): Single<TrackVenueResponse>
    fun unTrackVenue(authorization: String, venueId: String): Single<TrackVenueResponse>
    fun getTrackedVenues(authorization: String, page: Int, size: Int): Single<TrackingVenuesList>
    //endregion

    fun searchComprehensive(parameters: HashMap<String, Any?>): Single<ComprehensiveResponse>
    fun searchVenues(
        authorization: String?, latitude: Double?, longitude: Double?
    ): Single<List<VenuesSearchItem>>

    fun searchEvents(
        authorization: String?, searchEventsRequest: HashMap<String, Any>,
        mainCategories: List<String>?, subCategories: List<String>?, dates: List<String>?
    ): Single<List<EventMap>>

    fun searchSimilarEvents(
        authorization: String?, searchEventsRequest: HashMap<String, Any>,
        categories: List<String>?, dateStart: String?, dateEnd: String?
    ): Single<List<EventMap>>

    fun searchAutocomplete(limit: Int?, query: String): Single<AutoCompleteResponse>
    fun searchRecent(authorization: String?): Single<RecentSearchResponse>

    fun getSuggestions(authorization: String, size: Int): Single<GetSuggestedResponse>

    fun searchInTrackedVenues(
        authorization: String,
        query: String,
        size: Int,
        page: Int
    ): Single<TrackingVenuesList>

    fun searchInTrackedPerformers(
        authorization: String,
        query: String,
        size: Int,
        page: Int
    ): Single<TrackingPerformersList>

    fun searchInTrackedEvents(
        authorization: String,
        query: String,
        size: Int,
        page: Int
    ): Single<TrackingEventsList>

    fun createOrder(authorization: String, order: OrderRequest): Single<OrderResponse>
    fun getOrder(authorization: String, orderId: String): Single<OrderDetails>
    fun lockTickets(
        authorization: String,
        ticketGroupId: String,
        ticketSource: String,
        quantity: Int
    ): Single<LockResponce>

    fun unlockTickets(
        authorization: String,
        ticketGroupId: String,
    ): Completable

    fun getUserOrders(authorization: String, page: Int, size: Int): Single<GetOrdersResponse>
    fun getPastUserOrders(authorization: String, page: Int, size: Int): Single<GetOrdersResponse>

    fun checkPromocode(
        authorization: String,
        code: String
    ): Single<CheckPromocodeResponse>

    fun addPromocode(
        authorization: String,
        code: String
    ): Single<PromoCode>

    fun getMyPromocodes(
        authorization: String
    ): Single<List<PromoCode>>

    fun deletePromocode(
        authorization: String,
        code: String
    ): Completable

    fun getFees(authorization: String, amount: Int): Single<FeesResponse>
    fun suggestedShipment(
        authorization: String,
        ticketGroupId: String,
        ticketSource: String
    ): Single<SuggestedShipmentResponse>

}