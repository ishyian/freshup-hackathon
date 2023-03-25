package com.melitopolcherry.hackathon.data.repo

import android.util.Base64
import android.util.Base64.NO_WRAP
import com.melitopolcherry.hackathon.data.BuildConfig
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
import com.melitopolcherry.hackathon.data.restApi.BaseEvenzRepository
import com.melitopolcherry.hackathon.data.restApi.EvenzApi
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import java.io.UnsupportedEncodingException

class EvenzRepositoryImpl(private val evenzApi: EvenzApi) : BaseEvenzRepository(),
    EvenzRepository {

    override fun getNearCities(
        north: Double, south: Double, east: Double, west: Double,
        lang: String, username: String
    ): Single<GeonameResponse> {
        return evenzApi.getNearCities(
            north.toString(), south.toString(), east.toString(),
            west.toString(), lang, username
        ).compose { processResponse(it) }
    }

    override fun searchCities(input: String): Single<GeoCitiesResponse> {
        return evenzApi.searchCities(
            input,
            "(cities)",
            "en",
            BuildConfig.PLACES_API_KEY
        )
            .compose { processResponse(it) }
    }

    override fun getCityLocation(placeID: String): Single<LocationByCityResponse> {
        return evenzApi.getCityLocation(placeID, BuildConfig.GEOCODE_API_KEY)
            .compose { processResponse(it) }
    }

    //region Authenticate
    override fun refreshToken(token: String, grant: String): Single<AccessToken> {
        return evenzApi.refreshToken(getAuthToken(), token, grant)
            .compose { processResponse(it) }
    }

    override fun emailSignUp(emailSignUp: HashMap<String, String>): Completable {
        return evenzApi.emailSignUp(emailSignUp).compose { processResponse(it) }
    }

    override fun emailLogin(email: String, password: String, token: String): Single<LoginResponse> {
        return evenzApi.emailLogin(
            getAuthToken(), email, password, "password", token,
            "ANDROID"
        ).compose { processResponse(it) }
    }

    override fun emailRegistration(
        emailRegistration: HashMap<String, String>
    ): Single<LoginResponse> {
        return evenzApi.emailRegistration(emailRegistration).compose { processResponse(it) }
    }

    override fun changePassword(parameters: HashMap<String, String>): Single<LoginResponse> {
        return evenzApi.changePassword(parameters).compose { processResponse(it) }
    }

    override fun forgotPassword(parameters: HashMap<String, String>): Completable {
        return evenzApi.forgotPassword(parameters).compose { processResponse(it) }
    }

    override fun sendPhoneCode(authorization: String): Completable {
        return evenzApi.sendPhoneCode("Bearer $authorization").compose { processResponse(it) }
    }

    override fun confirmPhoneCode(authorization: String, code: String): Completable {
        val map = hashMapOf<String, String>()
        map["code"] = code
        return evenzApi.confirmPhoneCode("Bearer $authorization", map)
            .compose { processResponse(it) }
    }

    override fun updatePassword(
        authorization: String,
        parameters: HashMap<String, Any?>
    ): Single<LoginResponse> {
        return evenzApi.updatePassword("Bearer $authorization", parameters)
            .compose { processResponse(it) }
    }

    override fun insuranceQuote(
        authorization: String,
        ticketGroupId: String,
        quantity: Int,
        source: String,
        promocode: String?
    ): Single<ProtechtResponse> {
        return evenzApi.insuranceQuote(
            "Bearer $authorization",
            ticketGroupId,
            quantity,
            source,
            promocode
        )
            .compose { processResponse(it) }
    }

    override fun getGoogleAccessToken(
        grantType: String, clientId: String, clientSecret: String,
        redirectUri: String,
        code: String
    ): Single<GoogleAccessTokenResponse> {
        return evenzApi.getGoogleAccessToken(
            grantType, clientId, clientSecret, redirectUri,
            code
        ).compose { processResponse(it) }
    }

    override fun facebookLogin(userFacebookToken: String, fcmToken: String): Single<LoginResponse> {
        val s = HashMap<String, String>()
        s["auth_code"] = userFacebookToken
        s["device_token"] = fcmToken
        s["platform"] = "ANDROID"
        return evenzApi.facebookLogin(s).compose { processResponse(it) }.map { it }
    }

    override fun googleLogin(userGoogleToken: String, fcmToken: String): Single<LoginResponse> {
        val s = HashMap<String, String>()
        s["auth_code"] = userGoogleToken
        s["device_token"] = fcmToken
        s["platform"] = "ANDROID"
        return evenzApi.googleLogin(s).compose { processResponse(it) }.map { it }
    }
    //endregion

    //region Profile
    override fun getProfile(authorization: String): Single<User> {
        return evenzApi.getProfile("Bearer $authorization").compose { processResponse(it) }
    }

    override fun deleteAccount(authorization: String): Completable {
        return evenzApi.deleteAccount("Bearer $authorization").compose { processResponse(it) }
    }

    override fun sendCreditCard(
        authorization: String,
        paymentMethod: PaymentMethod
    ): Single<CreditCardResponse> {
        return evenzApi.sendCreditCard("Bearer $authorization", paymentMethod)
            .compose { processResponse(it) }
    }

    override fun addAddress(
        authorization: String,
        address: Address
    ): Single<AddAddressResponse> {
        return evenzApi.addAddress("Bearer $authorization", address)
            .compose { processResponse(it) }
    }

    override fun updateUserName(
        authorization: String,
        parameters: HashMap<String, Any?>
    ): Single<UpdateUserNameResponse> {
        return evenzApi.updateUserName("Bearer $authorization", parameters)
            .compose { processResponse(it) }
    }

    override fun updateUserEmail(
        authorization: String,
        email: String
    ): Single<ChangeEmailResponse> {
        val map = HashMap<String, Any?>()
        map["email"] = email
        return evenzApi.updateUserEmail("Bearer $authorization", map)
            .compose { processResponse(it) }
    }

    override fun addPhoneNumber(
        authorization: String,
        parameters: HashMap<String, Any?>
    ): Single<AddPhoneResponse> {
        return evenzApi.addPhoneNumber("Bearer $authorization", parameters)
            .compose { processResponse(it) }
    }

    override fun getTracked(authorization: String): Single<TrackedEntetiesResponse> {
        return evenzApi.getTracked("Bearer $authorization").compose { processResponse(it) }
    }

    override fun getSuggestions(authorization: String, size: Int): Single<GetSuggestedResponse> {
        return evenzApi.getSuggestions("Bearer $authorization", size)
            .compose { processResponse(it) }
    }
    //endregion

    //region Search
    override fun searchEvents(
        authorization: String?, searchEventsRequest: HashMap<String, Any>,
        mainCategories: List<String>?,
        subCategories: List<String>?,
        dates: List<String>?
    ): Single<List<EventMap>> {
        val token = if (authorization != null) {
            "Bearer $authorization"
        } else {
            null
        }
        return evenzApi.searchEventsByCords(
            authorization = token,
            parameters = searchEventsRequest,
            mainCategoriesList = mainCategories,
            subCategoriesList = subCategories,
            dates = dates
        ).compose { processResponse(it) }
    }

    override fun searchVenues(
        authorization: String?,
        latitude: Double?,
        longitude: Double?
    ): Single<List<VenuesSearchItem>> {
        val token = if (authorization != null) {
            "Bearer $authorization"
        } else {
            null
        }
        return evenzApi.searchVenues(
            authorization = token,
            latitude = latitude,
            longitude = longitude
        ).compose { processResponse(it) }
    }

    override fun searchSimilarEvents(
        authorization: String?,
        searchEventsRequest: HashMap<String, Any>,
        categories: List<String>?,
        dateStart: String?,
        dateEnd: String?
    ): Single<List<EventMap>> {
        val token = if (authorization != null) {
            "Bearer $authorization"
        } else {
            null
        }
        return evenzApi.searchSimilarEvents(
            token,
            searchEventsRequest,
            categories,
            dateStart,
            dateEnd
        ).compose { processResponse(it) }
    }

    override fun searchComprehensive(
        parameters: HashMap<String, Any?>
    ): Single<ComprehensiveResponse> {
        return evenzApi.searchComprehensive(parameters).compose { processResponse(it) }
    }

    override fun searchAutocomplete(
        parameters: HashMap<String, String>
    ): Single<AutoCompleteResponse> {
        return evenzApi.searchAutocomplete(parameters).compose { processResponse(it) }
    }

    override fun searchRecent(authorization: String?): Single<RecentSearchResponse> {
        return evenzApi.searchRecent("Bearer $authorization").compose { processResponse(it) }
    }

    override fun searchInTrackedVenues(
        authorization: String, query: String, size: Int,
        page: Int
    ): Single<TrackingVenuesList> {
        return evenzApi.searchInTrackedVenues("Bearer $authorization", query, size, page)
            .compose { processResponse(it) }
    }

    override fun searchInTrackedPerformers(
        authorization: String, query: String, size: Int,
        page: Int
    ): Single<TrackingPerformersList> {
        return evenzApi.searchInTrackedPerformers("Bearer $authorization", query, size, page)
            .compose { processResponse(it) }
    }

    override fun searchInTrackedEvents(
        authorization: String, query: String, size: Int,
        page: Int
    ): Single<TrackingEventsList> {
        return evenzApi.searchInTrackedEvents("Bearer $authorization", query, size, page)
            .compose { processResponse(it) }
    }
    //endregion

    //region Events
    override fun getEventById(
        authorization: String?, eventId: String,
        providedId: ProvidedIds?
    ): Single<EventFull> {
        val token = if (authorization != null) {
            "Bearer $authorization"
        } else {
            null
        }
        return evenzApi.getEventById(token, eventId)
            .compose { processResponse(it) }
    }

    override fun getEventOccurrences(
        eventId: String, latitude: Double, longitude: Double,
        page: Int, size: Int
    ): Single<EventOccurrencesResponse> {
        return evenzApi.getEventOccurrences(eventId, latitude, longitude, page, size)
            .compose { processResponse(it) }
    }

    override fun getEventTicketGroups(ticketGroupsRequest: TicketGroupsRequest): Single<TicketGroupsResponce> {
        return evenzApi.getEventTicketGroups(ticketGroupsRequest)
            .compose { processResponse(it) }
    }

    override fun trackEvent(authorization: String, eventId: String): Single<TrackEventResponse> {
        return evenzApi.trackEvent("Bearer $authorization", eventId)
            .compose { processResponse(it) }
    }

    override fun removeTrackEvent(
        authorization: String,
        eventId: String
    ): Single<TrackEventResponse> {
        return evenzApi.unTrackEvent("Bearer $authorization", eventId)
            .compose { processResponse(it) }
    }

    override fun getLikedEvents(
        authorization: String,
        page: Int,
        size: Int
    ): Single<LikedEventList> {
        return evenzApi.getLikedEvents("Bearer $authorization", page, size)
            .compose { processResponse(it) }
    }

    override fun sendPaymentRequest(
        authorization: String,
        request: PaymentRequest
    ): Single<OrderResponse> {
        return evenzApi.sendPaymentRequest(authorization = "Bearer $authorization", request = request)
            .compose { processResponse(it) }
    }

    override fun getTrackedEvents(
        authorization: String,
        page: Int,
        size: Int
    ): Single<TrackingEventsList> {
        return evenzApi.getTrackedEvents("Bearer $authorization", page, size)
            .compose { processResponse(it) }
    }
    //endregion

    //region Performers
    override fun performerDetails(
        authorization: String?,
        performerId: String
    ): Single<PerformerDetails> {
        val token = if (authorization != null) {
            "Bearer $authorization"
        } else {
            null
        }
        return evenzApi.performerDetails(token, performerId)
            .compose { processResponse(it) }
    }

    override fun loadPerformersEvents(
        authorization: String?,
        performerId: String,
        size: Int,
        date: String
    ): Single<LPEventsResponse> {
        val token = if (authorization != null) {
            "Bearer $authorization"
        } else {
            null
        }
        return evenzApi.loadPerformersEvents(
            token,
            performerId,
            true,
            size,
            date
        )
            .compose { processResponse(it) }
    }

    override fun trackPerformer(
        authorization: String,
        performerId: String
    ): Single<TrackPerformerResponse> {
        return evenzApi.trackPerformer("Bearer $authorization", performerId)
            .compose { processResponse(it) }
    }

    override fun unTrackPerformer(
        authorization: String,
        performerId: String
    ): Single<TrackPerformerResponse> {
        return evenzApi.unTrackPerformer("Bearer $authorization", performerId)
            .compose { processResponse(it) }
    }

    override fun getTrackedPerformers(
        authorization: String,
        page: Int, size: Int
    ): Single<TrackingPerformersList> {
        return evenzApi.getTrackedPerformers("Bearer $authorization", page, size)
            .compose { processResponse(it) }
    }

    //endregion

    //region Venues
    override fun venueDetails(authorization: String?, performerId: String): Single<VenueDetails> {
        val token = if (authorization != null) {
            "Bearer $authorization"
        } else {
            null
        }
        return evenzApi.venueDetails(token, performerId)
            .compose { processResponse(it) }
    }

    override fun loadVenuesEvents(
        authorization: String?,
        venueId: String,
        size: Int,
        date: String
    ): Single<LPEventsResponse> {
        val token = if (authorization != null) {
            "Bearer $authorization"
        } else {
            null
        }
        return evenzApi.loadVenuesEvents(token, venueId, true, size, date)
            .compose { processResponse(it) }
    }

    override fun trackVenue(authorization: String, venueId: String): Single<TrackVenueResponse> {
        return evenzApi.trackVenue("Bearer $authorization", venueId)
            .compose { processResponse(it) }
    }

    override fun unTrackVenue(authorization: String, venueId: String): Single<TrackVenueResponse> {
        return evenzApi.unTrackVenue("Bearer $authorization", venueId)
            .compose { processResponse(it) }
    }

    override fun getTrackedVenues(
        authorization: String,
        page: Int,
        size: Int
    ): Single<TrackingVenuesList> {
        return evenzApi.getTrackedVenues("Bearer $authorization", page, size)
            .compose { processResponse(it) }
    }
    //endregion

    //region Orders
    override fun createOrder(authorization: String, order: OrderRequest): Single<OrderResponse> {
        return evenzApi.createOrder(authorization = "Bearer $authorization", order = order)
            .compose { processResponse(it) }
    }

    override fun lockTickets(
        authorization: String,
        ticketGroupId: String,
        ticketSource: String,
        quantity: Int
    ): Single<LockResponce> {
        val map = HashMap<String, Any?>()
        map["ticket_group_id"] = ticketGroupId
        map["quantity"] = quantity
        map["ticket_source"] = ticketSource
        return evenzApi.lockTickets("Bearer $authorization", map)
            .compose { processResponse(it) }
    }

    override fun unlockTickets(authorization: String, lockId: String): Completable {
        return evenzApi.unlockTickets("Bearer $authorization", lockId)
            .compose { processResponse(it) }
    }

    override fun getOrder(authorization: String, orderId: String): Single<OrderDetails> {
        return evenzApi.getOrder("Bearer $authorization", orderId)
            .compose { processResponse(it) }
    }

    override fun getUserOrders(
        authorization: String, page: Int,
        size: Int
    ): Single<GetOrdersResponse> {
        return evenzApi.getUserOrders("Bearer $authorization", page, size)
            .compose { processResponse(it) }
    }

    override fun getPastUserOrders(
        authorization: String, page: Int,
        size: Int
    ): Single<GetOrdersResponse> {
        return evenzApi.getPastUserOrders("Bearer $authorization", page, size)
            .compose { processResponse(it) }
    }

    override fun checkPromocode(
        authorization: String,
        code: String
    ): Single<CheckPromocodeResponse> {
        return evenzApi.checkPromocode("Bearer $authorization", code)
            .compose { processResponse(it) }
    }

    override fun addPromocode(authorization: String, code: String): Single<PromoCode> {
        val map = hashMapOf<String, String>()
        map["code"] = code
        return evenzApi.addPromocode("Bearer $authorization", map)
            .compose { processResponse(it) }
    }

    override fun getMyPromocodes(authorization: String): Single<List<PromoCode>> {
        return evenzApi.getMyPromocodes("Bearer $authorization").compose { processResponse(it) }
    }

    override fun deletePromocode(authorization: String, code: String): Completable {
        return evenzApi.deletePromocode("Bearer $authorization", code)
            .compose { processResponse(it) }
    }

    override fun getFees(authorization: String, amount: Int): Single<FeesResponse> {
        return evenzApi.getFees("Bearer $authorization", amount)
            .compose { processResponse(it) }
    }

    override fun suggestedShipment(
        authorization: String,
        ticketGroupId: String,
        ticketSource: String
    ): Single<SuggestedShipmentResponse> {
        return evenzApi.suggestedShipment("Bearer $authorization", ticketGroupId, ticketSource)
            .compose { processResponse(it) }
    }
    //endregion

    //region Notifications
    override fun getPush(authorization: String): Completable {
        return evenzApi.getPush("Bearer $authorization").compose { processResponse(it) }
    }

    override fun getPushWithImage(authorization: String): Completable {
        return evenzApi.getPushWithImage("Bearer $authorization").compose { processResponse(it) }
    }

    override fun getPushWithTitle(authorization: String): Completable {
        return evenzApi.getPushWithTitle("Bearer $authorization").compose { processResponse(it) }
    }

    override fun updateFCM(authorization: String, appid: String): Completable {
        val map = HashMap<String, String>()
        map["device_token"] = appid
        map["platform"] = "ANDROID"
        return evenzApi.updateFCM("Bearer $authorization", map).compose { processResponse(it) }
    }

    override fun getNotifications(authorization: String): Single<List<NotificationsItem>> {
        return evenzApi.getNotifications("Bearer $authorization").compose { processResponse(it) }
    }

    override fun readNotifications(authorization: String): Completable {
        return evenzApi.readNotifications("Bearer $authorization")
            .compose { processResponse(it) }
    }

    override fun getCountUnreadNotifications(
        authorization: String
    ): Single<UnreadNotificationsResponse> {
        return evenzApi.getCountUnreadNotifications("Bearer $authorization")
            .compose { processResponse(it) }
    }

    override fun deleteNotification(authorization: String, id: String): Completable {
        return evenzApi.deleteNotification("Bearer $authorization", id)
            .compose { processResponse(it) }
    }
    //endregion

    override fun getCurrentWeather(lat: String, lon: String): Single<CurrentWeatherResponse> {
        return evenzApi.getCurrentWeather(
            lat, lon, BuildConfig.OPENWEATHERMAP_APPID,
            "metric"
        ).compose { processResponse(it) }
    }

    override fun getHourlyWeather(lat: String, lon: String): Single<HourlyWeatherResponse> {
        return evenzApi.getHourlyWeather(lat, lon, BuildConfig.OPENWEATHERMAP_APPID, "metric")
            .compose { processResponse(it) }
    }

    private fun getAuthToken(): String {
        var data = ByteArray(0)
        try {
            data =
                ("${BuildConfig.AUTH_TOKEN_LOGIN}:${BuildConfig.AUTH_TOKEN_PASSWORD}").toByteArray(
                    charset("UTF-8")
                )
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }

        return "Basic " + Base64.encodeToString(data, NO_WRAP)
    }
}