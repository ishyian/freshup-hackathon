package com.melitopolcherry.hackathon.data.restApi

import com.melitopolcherry.hackathon.data.models.Address
import com.melitopolcherry.hackathon.data.models.CheckPromocodeResponse
import com.melitopolcherry.hackathon.data.models.FeesResponse
import com.melitopolcherry.hackathon.data.models.LPEventsResponse
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
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface EvenzApi {

    //region Authenticate
    @POST("https://www.googleapis.com/oauth2/v4/token")
    @FormUrlEncoded
    @Headers("Content-Type:application/x-www-form-urlencoded")
    fun getGoogleAccessToken(
        @Field("grant_type")
        grantType: String,
        @Field("client_id")
        clientId: String,
        @Field("client_secret")
        clientSecret: String,
        @Field("redirect_uri")
        redirectUri: String,
        @Field("code")
        code: String
    ): Single<GoogleAccessTokenResponse>

    @POST("http://api.geonames.org/citiesJSON")
    @FormUrlEncoded
    fun getNearCities(
        @Field("north")
        north: String,
        @Field("south")
        south: String,
        @Field("east")
        east: String,
        @Field("west")
        west: String,
        @Field("lang")
        lang: String,
        @Field("username")
        username: String
    ): Single<GeonameResponse>

    @POST("authenticate/facebook-login")
    fun facebookLogin(
        @Body
        userFacebookToken: HashMap<String, String>
    ): Single<LoginResponse>

    @POST("authenticate/sign-in")
    @FormUrlEncoded
    fun emailLogin(
        @Header("Authorization")
        auth: String,
        @Field("user_name")
        email: String,
        @Field("password")
        password: String,
        @Field("grant_type")
        grant: String,
        @Field("device_token")
        token: String,
        @Field("platform")
        platform: String
    ): Single<LoginResponse>

    @POST("authenticate/sign-up/generate-code")
    fun emailSignUp(
        @Body
        emailSignUp: HashMap<String, String>
    ): Completable

    @POST("authenticate/sign-in")
    @FormUrlEncoded
    fun refreshToken(
        @Header("Authorization")
        auth: String,
        @Field("refresh_token")
        token: String,
        @Field("grant_type")
        grant: String
    ): Single<AccessToken>

    @POST("authenticate/sign-up")
    fun emailRegistration(
        @Body
        emailRegistration: HashMap<String, String>
    ): Single<LoginResponse>

    @Headers("Content-Type:application/json")
    @POST("authenticate/google-login")
    fun googleLogin(
        @Body
        userGoogleToken: HashMap<String, String>
    ): Single<LoginResponse>

    @POST("authenticate/restore-password")
    fun changePassword(
        @Body
        parameters: HashMap<String, String>
    ): Single<LoginResponse>

    @Headers("Content-Type:application/json")
    @POST("orders/lock")
    fun lockTickets(
        @Header("Authorization")
        auth: String,
        @Body
        parameters: HashMap<String, Any?>
    ): Single<LockResponce>

    @DELETE("orders/lock/{lockId}")
    fun unlockTickets(
        @Header("Authorization")
        auth: String,
        @Path("lockId")
        lockId: String,
    ): Completable

    @POST("profile/phones/verify")
    fun sendPhoneCode(
        @Header("Authorization")
        authorization: String
    ): Completable

    @POST("profile/phones/confirm")
    fun confirmPhoneCode(
        @Header("Authorization")
        authorization: String,
        @Body
        code: HashMap<String, String>
    ): Completable

    @POST("authenticate/restore-password/generate-code")
    fun forgotPassword(
        @Body
        parameters: HashMap<String, String>
    ): Completable
    //endregion

    //region Profile
    @DELETE("profile")
    fun deleteAccount(
        @Header("Authorization")
        authorization: String
    ): Completable

    @GET("profile")
    fun getProfile(
        @Header("Authorization")
        authorization: String
    ): Single<User>

    @POST("profile/addresses")
    fun addAddress(
        @Header("Authorization")
        authorization: String,
        @Body
        address: Address
    ): Single<AddAddressResponse>

    @Headers("Content-Type:application/json")
    @PUT("profile")
    fun updateUserName(
        @Header("Authorization")
        authorization: String,
        @Body
        parameters: HashMap<String, Any?>
    ): Single<UpdateUserNameResponse>

    @PUT("profile/emails")
    fun updateUserEmail(
        @Header("Authorization")
        authorization: String,
        @Body
        parameters: HashMap<String, Any?>
    ): Single<ChangeEmailResponse>

    @POST("profile/phones")
    fun addPhoneNumber(
        @Header("Authorization")
        authorization: String,
        @Body
        parameters: HashMap<String, Any?>
    ): Single<AddPhoneResponse>

    @POST("profile/password/change")
    fun updatePassword(
        @Header("Authorization")
        authorization: String,
        @Body
        parameters: HashMap<String, Any?>
    ): Single<LoginResponse>

    @POST("profile/credit-card")
    fun sendCreditCard(
        @Header("Authorization")
        authorization: String,
        @Body
        paymentMethod: PaymentMethod
    ): Single<CreditCardResponse>

    @GET("profile/tracked")
    fun getTracked(
        @Header("Authorization")
        authorization: String
    ): Single<TrackedEntetiesResponse>
    //endregion

    //region Search
    @GET("search/events")
    fun searchEventsByCords(
        @Header("Authorization")
        authorization: String?,
        @QueryMap
        parameters: HashMap<String, Any>,
        @Query("mainCategories")
        mainCategoriesList: List<String>?,
        @Query("categories")
        subCategoriesList: List<String>?,
        @Query("date")
        dates: List<String>?
    ): Single<List<EventMap>>

    @GET("search/venues")
    fun searchVenues(
        @Header("Authorization")
        authorization: String?,
        @Query("latitude") latitude: Double?,
        @Query("longitude") longitude: Double?
    ): Single<List<VenuesSearchItem>>

    @GET("search/events")
    fun searchSimilarEvents(
        @Header("Authorization")
        authorization: String?,
        @QueryMap
        parameters: HashMap<String, Any>,
        @Query("category")
        list: List<String>?,
        @Query("date_min")
        dateStart: String?,
        @Query("date_max")
        dateEnd: String?
    ): Single<List<EventMap>>

    @GET("search/comprehensive")
    fun searchComprehensive(
        @QueryMap
        parameters: HashMap<String, Any?>
    ): Single<ComprehensiveResponse>

    @GET("search/autocomplete")
    fun searchAutocomplete(
        @QueryMap
        parameters: HashMap<String, String>
    ): Single<AutoCompleteResponse>

    @GET("search/recent")
    fun searchRecent(
        @Header("Authorization")
        authorization: String?
    ): Single<RecentSearchResponse>

    @GET("profile/venues/tracked/search")
    fun searchInTrackedVenues(
        @Header("Authorization")
        authorization: String,
        @Query("query")
        query: String,
        @Query("size")
        size: Int,
        @Query("page")
        page: Int
    ): Single<TrackingVenuesList>

    @GET("profile/performers/tracked/search")
    fun searchInTrackedPerformers(
        @Header("Authorization")
        authorization: String,
        @Query("query")
        query: String,
        @Query("size")
        size: Int,
        @Query("page")
        page: Int
    ): Single<TrackingPerformersList>

    @GET("profile/events/tracked/search")
    fun searchInTrackedEvents(
        @Header("Authorization")
        authorization: String,
        @Query("query")
        query: String,
        @Query("size")
        size: Int,
        @Query("page")
        page: Int
    ): Single<TrackingEventsList>
    //endregion

    //region Event
    @GET("events/{id}")
    fun getEventById(
        @Header("Authorization")
        authorization: String?,
        @Path("id")
        eventId: String,

        ): Single<EventFull>

    @POST("events/occurrences")
    fun getEventTicketGroups(
        @Body
        ticketGroupsRequest: TicketGroupsRequest,
    ): Single<TicketGroupsResponce>

    @GET("events/{id}/occurrences")
    fun getEventOccurrences(
        @Path("id")
        eventId: String,
        @Query("latitude")
        latitude: Double,
        @Query("longitude")
        longitude: Double,
        @Query("page")
        page: Int,
        @Query("size")
        size: Int
    ): Single<EventOccurrencesResponse>

    @PUT("events/{id}/track")
    fun trackEvent(
        @Header("Authorization")
        authorization: String,
        @Path("id")
        eventId: String
    ): Single<TrackEventResponse>

    @GET("profile/events/liked")
    fun getLikedEvents(
        @Header("Authorization")
        authorization: String,
        @Query("page")
        page: Int,
        @Query("size")
        size: Int
    ): Single<LikedEventList>

    @GET("profile/events/tracked")
    fun getTrackedEvents(
        @Header("Authorization")
        authorization: String,
        @Query("page")
        page: Int,
        @Query("size")
        size: Int
    ): Single<TrackingEventsList>

    @DELETE("events/{id}/track")
    fun unTrackEvent(
        @Header("Authorization")
        authorization: String,
        @Path("id")
        eventId: String
    ): Single<TrackEventResponse>
    //endregion

    //region Performer
    @GET("performers/{id}")
    fun performerDetails(
        @Header("Authorization")
        authorization: String?,
        @Path("id")
        performerId: String
    ): Single<PerformerDetails>

    @GET("performers/{id}/events")
    fun loadPerformersEvents(
        @Header("Authorization")
        authorization: String?,
        @Path("id")
        performerId: String,
        @Query("use_new_api")
        p: Boolean,
        @Query("size")
        size: Int,
        @Query("start_date")
        date: String
    ): Single<LPEventsResponse>

    @PUT("performers/{id}/track")
    fun trackPerformer(
        @Header("Authorization")
        authorization: String,
        @Path("id")
        performerId: String
    ): Single<TrackPerformerResponse>

    @DELETE("performers/{id}/track")
    fun unTrackPerformer(
        @Header("Authorization")
        authorization: String,
        @Path("id")
        performerId: String
    ): Single<TrackPerformerResponse>

    @GET("profile/performers/tracked")
    fun getTrackedPerformers(
        @Header("Authorization")
        authorization: String,
        @Query("page")
        page: Int,
        @Query("size")
        size: Int
    ): Single<TrackingPerformersList>
    //endregion

    @GET("orders/insurance/quote")
    fun insuranceQuote(
        @Header("Authorization")
        authorization: String,
        @Query("ticket_group_id")
        ticketGroupId: String,
        @Query("quantity")
        quantity: Int,
        @Query("ticket_source")
        source: String,
        @Query("promocode_id")
        promocodeId: String?
    ): Single<ProtechtResponse>

    //region Venues
    @GET("venues/{id}")
    fun venueDetails(
        @Header("Authorization")
        authorization: String?,
        @Path("id")
        venueId: String
    ): Single<VenueDetails>

    @GET("venues/{id}/events")
    fun loadVenuesEvents(
        @Header("Authorization")
        authorization: String?,
        @Path("id")
        venueId: String,
        @Query("use_new_api")
        p: Boolean,
        @Query("size")
        size: Int,
        @Query("start_date")
        date: String
    ): Single<LPEventsResponse>

    @PUT("venues/{id}/track")
    fun trackVenue(
        @Header("Authorization")
        authorization: String,
        @Path("id")
        venueId: String
    ): Single<TrackVenueResponse>

    @DELETE("venues/{id}/track")
    fun unTrackVenue(
        @Header("Authorization")
        authorization: String,
        @Path("id")
        venueId: String
    ): Single<TrackVenueResponse>

    @GET("profile/venues/tracked")
    fun getTrackedVenues(
        @Header("Authorization")
        authorization: String,
        @Query("page")
        page: Int,
        @Query("size")
        size: Int
    ): Single<TrackingVenuesList>
    //endregion

    //region Orders
    @POST("orders")
    fun createOrder(
        @Header("Authorization")
        authorization: String,
        @Body order: OrderRequest
    ): Single<OrderResponse>

    @GET("orders/{id}")
    fun getOrder(
        @Header("Authorization")
        authorization: String,
        @Path("id")
        orderId: String
    ): Single<OrderDetails>

    @GET("orders")
    fun getUserOrders(
        @Header("Authorization")
        authorization: String,
        @Query("page")
        page: Int,
        @Query("size")
        size: Int
    ): Single<GetOrdersResponse>

    @POST("orders")
    fun sendPaymentRequest(
        @Header("Authorization")
        authorization: String,
        @Body
        request: PaymentRequest
    ): Single<OrderResponse>

    @GET("orders/past")
    fun getPastUserOrders(
        @Header("Authorization")
        authorization: String,
        @Query("page")
        page: Int,
        @Query("size")
        size: Int
    ): Single<GetOrdersResponse>

    @GET("orders/promocodes")
    fun checkPromocode(
        @Header("Authorization")
        authorization: String,
        @Query("code")
        code: String
    ): Single<CheckPromocodeResponse>

    @POST("profile/promocodes")
    fun addPromocode(
        @Header("Authorization")
        authorization: String,
        @Body
        code: HashMap<String, String>
    ): Single<PromoCode>

    @GET("profile/promocodes")
    fun getMyPromocodes(
        @Header("Authorization")
        authorization: String
    ): Single<List<PromoCode>>

    @DELETE("profile/promocodes/{id}")
    fun deletePromocode(
        @Header("Authorization")
        authorization: String,
        @Path("id")
        id: String
    ): Completable

    @GET("profile/suggested")
    fun getSuggestions(
        @Header("Authorization")
        authorization: String,
        @Query("size")
        size: Int
    ): Single<GetSuggestedResponse>

    @GET("orders/service-fees")
    fun getFees(
        @Header("Authorization")
        authorization: String,
        @Query("amount")
        amount: Int
    ): Single<FeesResponse>

    @GET("orders/shipments/suggest")
    fun suggestedShipment(
        @Header("Authorization")
        authorization: String,
        @Query("ticket_group_id")
        ticketGroupId: String,
        @Query("ticket_source")
        ticketSource: String
    ): Single<SuggestedShipmentResponse>
    //endregion

    //region Notifications
    @GET("notifications")
    fun getNotifications(
        @Header("Authorization")
        authorization: String
    ): Single<List<NotificationsItem>>

    @GET("profile/notification/test-image")
    fun getPushWithImage(
        @Header("Authorization")
        authorization: String
    ): Completable

    @GET("profile/notification/test-title")
    fun getPushWithTitle(
        @Header("Authorization")
        authorization: String
    ): Completable

    @GET("profile/notification/test")
    fun getPush(
        @Header("Authorization")
        authorization: String
    ): Completable

    @PUT("notifications/read")
    fun readNotifications(
        @Header("Authorization")
        authorization: String
    ): Completable

    @GET("notifications/unread/count")
    fun getCountUnreadNotifications(
        @Header("Authorization")
        authorization: String
    ): Single<UnreadNotificationsResponse>

    @DELETE("notifications/{notificationId}")
    fun deleteNotification(
        @Header("Authorization")
        authorization: String,
        @Path("notificationId")
        id: String
    ): Completable
    //endregion

    @GET("https://maps.googleapis.com/maps/api/place/autocomplete/json")
    fun searchCities(
        @Query("input")
        input: String,
        @Query("types")
        types: String,
        @Query("language")
        language: String,
        @Query("key")
        apiKey: String
    ): Single<GeoCitiesResponse>

    @GET("https://maps.googleapis.com/maps/api/geocode/json")
    fun getCityLocation(
        @Query("place_id")
        placeID: String,
        @Query("key")
        apiKey: String
    ): Single<LocationByCityResponse>

    //region Weather
    @GET("http://api.openweathermap.org/data/2.5/weather")
    fun getCurrentWeather(
        @Query("lat")
        lat: String,
        @Query("lon")
        lon: String,
        @Query("APPID")
        appid: String,
        @Query("units")
        units: String
    ): Single<CurrentWeatherResponse>

    @PUT("profile/devices/token")
    fun updateFCM(
        @Header("Authorization")
        authorization: String,
        @Body
        map: HashMap<String, String>
    ): Completable

    @GET("http://api.openweathermap.org/data/2.5/forecast")
    fun getHourlyWeather(
        @Query("lat")
        lat: String,
        @Query("lon")
        lon: String,
        @Query("APPID")
        appid: String,
        @Query("units")
        units: String
    ): Single<HourlyWeatherResponse>
    //endregion
}