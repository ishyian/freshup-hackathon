package com.melitopolcherry.hackathon.data.repo

import com.melitopolcherry.hackathon.data.models.Address
import com.melitopolcherry.hackathon.data.models.CheckPromocodeResponse
import com.melitopolcherry.hackathon.data.models.FeesResponse
import com.melitopolcherry.hackathon.data.models.LPEventsResponse
import com.melitopolcherry.hackathon.data.models.ProvidedIds
import com.melitopolcherry.hackathon.data.models.TicketGroupsRequest
import com.melitopolcherry.hackathon.data.models.checkout.CreditCardResponse
import com.melitopolcherry.hackathon.data.models.checkout.LockResponce
import com.melitopolcherry.hackathon.data.models.checkout.PaymentMethod
import com.melitopolcherry.hackathon.data.models.checkout.PaymentRequest
import com.melitopolcherry.hackathon.data.models.comprehensive.ComprehensiveResponse
import com.melitopolcherry.hackathon.data.models.event.full.EventFull
import com.melitopolcherry.hackathon.data.models.event.ocurrences.EventOccurrencesResponse
import com.melitopolcherry.hackathon.data.models.event.small.EventMap
import com.melitopolcherry.hackathon.data.models.geocities.GeoCitiesResponse
import com.melitopolcherry.hackathon.data.models.geolocation.LocationByCityResponse
import com.melitopolcherry.hackathon.data.models.geoname.GeonameResponse
import com.melitopolcherry.hackathon.data.models.getEventsList.LikedEventList
import com.melitopolcherry.hackathon.data.models.login.AccessToken
import com.melitopolcherry.hackathon.data.models.login.LoginResponse
import com.melitopolcherry.hackathon.data.models.login.User
import com.melitopolcherry.hackathon.data.models.notification.NotificationsItem
import com.melitopolcherry.hackathon.data.models.notification.UnreadNotificationsResponse
import com.melitopolcherry.hackathon.data.models.order.GetOrdersResponse
import com.melitopolcherry.hackathon.data.models.order.OrderDetails
import com.melitopolcherry.hackathon.data.models.order.OrderRequest
import com.melitopolcherry.hackathon.data.models.order.OrderResponse
import com.melitopolcherry.hackathon.data.models.performer.PerformerDetails
import com.melitopolcherry.hackathon.data.models.protect.ProtechtResponse
import com.melitopolcherry.hackathon.data.models.responces.AddAddressResponse
import com.melitopolcherry.hackathon.data.models.responces.AddPhoneResponse
import com.melitopolcherry.hackathon.data.models.responces.AutoCompleteResponse
import com.melitopolcherry.hackathon.data.models.responces.ChangeEmailResponse
import com.melitopolcherry.hackathon.data.models.responces.GoogleAccessTokenResponse
import com.melitopolcherry.hackathon.data.models.responces.PromoCode
import com.melitopolcherry.hackathon.data.models.responces.RecentSearchResponse
import com.melitopolcherry.hackathon.data.models.responces.SuggestedShipmentResponse
import com.melitopolcherry.hackathon.data.models.responces.UpdateUserNameResponse
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
import com.melitopolcherry.hackathon.data.models.weather.current.CurrentWeatherResponse
import com.melitopolcherry.hackathon.data.models.weather.hourly.HourlyWeatherResponse
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import javax.inject.Singleton

@Singleton
interface EvenzRepository {

    fun insuranceQuote(
        authorization: String,
        ticketGroupId: String,
        quantity: Int,
        source: String,
        promocode: String?
    ): Single<ProtechtResponse>

    //region Authenticate
    fun getGoogleAccessToken(
        grantType: String, clientId: String, clientSecret: String, redirectUri: String,
        code: String
    ): Single<GoogleAccessTokenResponse>

    fun getNearCities(
        north: Double, south: Double, east: Double, west: Double, lang: String,
        username: String
    ): Single<GeonameResponse>

    fun facebookLogin(userFacebookToken: String, fcmToken: String): Single<LoginResponse>
    fun googleLogin(userGoogleToken: String, fcmToken: String): Single<LoginResponse>
    fun refreshToken(token: String, grant: String): Single<AccessToken>

    fun emailLogin(email: String, password: String, token: String): Single<LoginResponse>
    fun emailSignUp(emailSignUp: HashMap<String, String>): Completable
    fun emailRegistration(emailRegistration: HashMap<String, String>): Single<LoginResponse>

    fun changePassword(parameters: HashMap<String, String>): Single<LoginResponse>
    fun forgotPassword(parameters: HashMap<String, String>): Completable
    //endregion

    //region Profile
    fun sendPhoneCode(authorization: String): Completable
    fun confirmPhoneCode(authorization: String, code: String): Completable
    fun updatePassword(
        authorization: String,
        parameters: HashMap<String, Any?>
    ): Single<LoginResponse>

    fun deleteAccount(authorization: String): Completable

    fun sendCreditCard(
        authorization: String,
        paymentMethod: PaymentMethod
    ): Single<CreditCardResponse>

    fun getProfile(authorization: String): Single<User>

    fun addAddress(
        authorization: String,
        address: Address
    ): Single<AddAddressResponse>

    fun updateUserName(
        authorization: String,
        parameters: HashMap<String, Any?>
    ): Single<UpdateUserNameResponse>

    fun updateUserEmail(authorization: String, email: String): Single<ChangeEmailResponse>

    fun addPhoneNumber(
        authorization: String,
        parameters: HashMap<String, Any?>
    ): Single<AddPhoneResponse>

    fun getTracked(authorization: String): Single<TrackedEntetiesResponse>
    fun getSuggestions(authorization: String, size: Int): Single<GetSuggestedResponse>
    //endregion

    //region Search
    fun searchEvents(
        authorization: String?, searchEventsRequest: HashMap<String, Any>,
        mainCategories: List<String>?, subCategories: List<String>?, dates: List<String>?
    ): Single<List<EventMap>>

    fun searchVenues(
        authorization: String?, latitude: Double?, longitude: Double?
    ): Single<List<VenuesSearchItem>>

    fun searchSimilarEvents(
        authorization: String?,
        searchEventsRequest: HashMap<String, Any>,
        categories: List<String>?,
        dateStart: String?,
        dateEnd: String?
    ): Single<List<EventMap>>

    fun sendPaymentRequest(
        authorization: String,
        request: PaymentRequest
    ): Single<OrderResponse>

    fun searchComprehensive(parameters: HashMap<String, Any?>): Single<ComprehensiveResponse>
    fun searchAutocomplete(parameters: HashMap<String, String>): Single<AutoCompleteResponse>
    fun searchRecent(authorization: String?): Single<RecentSearchResponse>

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
    //endregion

    //region Event
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
    fun removeTrackEvent(authorization: String, eventId: String): Single<TrackEventResponse>

    fun getLikedEvents(authorization: String, page: Int, size: Int): Single<LikedEventList>
    fun getTrackedEvents(authorization: String, page: Int, size: Int): Single<TrackingEventsList>
    //endregion

    //region Performer
    fun performerDetails(
        authorization: String?,
        performerId: String
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

    //region Venues
    fun venueDetails(
        authorization: String?,
        performerId: String
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

    //region Orders
    fun createOrder(authorization: String, order: OrderRequest): Single<OrderResponse>
    fun lockTickets(
        authorization: String,
        ticketGroupId: String,
        ticketSource: String,
        quantity: Int
    ): Single<LockResponce>

    fun unlockTickets(
        authorization: String,
        lockId: String,
    ): Completable

    fun getOrder(authorization: String, orderId: String): Single<OrderDetails>
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
    //endregion

    //region Notifications
    fun getNotifications(authorization: String): Single<List<NotificationsItem>>

    fun getPush(authorization: String): Completable
    fun getPushWithImage(authorization: String): Completable
    fun getPushWithTitle(authorization: String): Completable
    fun updateFCM(authorization: String, appid: String): Completable

    fun readNotifications(authorization: String): Completable
    fun getCountUnreadNotifications(authorization: String): Single<UnreadNotificationsResponse>
    fun deleteNotification(authorization: String, id: String): Completable
    //endregion

    fun searchCities(input: String): Single<GeoCitiesResponse>
    fun getCityLocation(placeID: String): Single<LocationByCityResponse>

    fun getCurrentWeather(lat: String, lon: String): Single<CurrentWeatherResponse>
    fun getHourlyWeather(lat: String, lon: String): Single<HourlyWeatherResponse>
}